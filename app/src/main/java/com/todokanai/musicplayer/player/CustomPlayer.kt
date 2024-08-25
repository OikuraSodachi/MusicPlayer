package com.todokanai.musicplayer.player

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext
import com.todokanai.musicplayer.tools.independent.getCircularPrev
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class CustomPlayer(
    val nextIntent:Intent,
    val musicRepo : MusicRepository,
    val dsRepo:DataStoreRepository,
    seed: Double,
    playList:List<Music>,
    shuffleMode:Boolean,
    val currentMusic:Music?,
    loop:Boolean,
    val setMediaPlaybackState_td:(Int)->Unit
): MediaPlayer() {
    val mediaPlayer = MediaPlayer()

    override fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            _isPlayingHolder.value = true
            setMediaPlaybackState_td(PlaybackStateCompat.STATE_PLAYING)
            mediaPlayer.start()
        }       // CoroutineScope 안할 경우, next/prev 할때 mediaPlayer.currentPosition 값이 1초 늦게 리셋되는 현상 있음
        // 지금 이 코드가 정상임
    }


    override fun pause() {
        mediaPlayer.pause()
        _isPlayingHolder.value = false
        setMediaPlaybackState_td(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun reset() {
        mediaPlayer.reset()
        _isPlayingHolder.value = false
        setMediaPlaybackState_td(PlaybackStateCompat.STATE_NONE)
    }


    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun release() {
        mediaPlayer.release()
        setMediaPlaybackState_td(PlaybackStateCompat.STATE_NONE)
    }

    /*
    /** NullPointerException 발생함. 이유는 몰?루 **/
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
    */

    // override
    //-------------------------

    private val initialPlayListNew = modifiedPlayList(playList,shuffleMode,seed)

    val seedHolder = dsRepo.seed.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = seed
    )

    private val _playListHolder = MutableStateFlow<List<Music>>(initialPlayListNew)
    val playListHolder: StateFlow<List<Music>> = _playListHolder

    val currentMusicHolder = musicRepo.currentMusic.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = currentMusic
    )

    val isLoopingHolder = dsRepo.isLooping.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = loop
    )

    private val _isPlayingHolder = MutableStateFlow(false)
    val isPlayingHolder : StateFlow<Boolean> = _isPlayingHolder

    val isShuffledHolder = dsRepo.isShuffled.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = shuffleMode
    )

    /**  currently not in use
     *
     * Room에서 끌어오는 형태로 바꾼 다음, playListHolder의 역할을 playListHolderNew에 옮길 것**/
    val musicListHolder = MutableStateFlow(playList)

    /** currently not in use **/
    val playListHolderNew = combine(
        musicListHolder,
        isShuffledHolder,
        seedHolder
    ){ musicList,shuffled, seed ->
        modifiedPlayList(musicList,shuffled,seed)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialPlayListNew
    )

    fun updatePlayList(){
        val isShuffled = isShuffledHolder.value
        CoroutineScope(Dispatchers.IO).launch {
            _playListHolder.value = modifiedPlayList(
                musicRepo.getAllNonFlow(), isShuffled,
                seedHolder.value
            )
        }
    }

    fun initAttributes(context: Context) {
        this.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setMediaPlaybackState_td(PlaybackStateCompat.STATE_NONE)
            this.setMusic(currentMusic,context)
        }
    }

    fun pausePlay() =
            if (mediaPlayer.isPlaying) {
                this@CustomPlayer.pause()
            } else {
                this@CustomPlayer.start()
            }

    /** onCompletion() 의 context에 주의 (?). 아직 미검증 상태  **/
    private fun setMusicPrimitive(music: Music?,context: Context){
        music?.let {
            val shouldLoop = isLoopingHolder.value
            val isMusicValid = music.fileDir != "empty"

            if (isMusicValid) {
                reset()
                mediaPlayer.apply {
                    setDataSource(context, music.getUri())
                    setOnCompletionListener {
                        if (!isLooping) {
                            context.sendBroadcast(nextIntent)
                        }
                    }

                    isLooping = shouldLoop
                    prepare()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    musicRepo.upsertCurrentMusic(it)
                }
            }
        }
    }

    private fun setMusic(music: Music?,context: Context){
        val playList = playListHolder.value
        var i = 0
        try {
            setMusicPrimitive(
                music = music,
                context = context
            )
        } catch(e:Exception){
            println(e.stackTrace)
            i++
            if(i!=playList.size) {
                setMusicPrimitive(
                    music = getCircularNext(playList, playList.indexOf(music)) as Music,
                    context = context
                )
            } else{
                Toast.makeText(context,context.getString(R.string.all_item_failure),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchMusic(context: Context,music: Music){
        this.setMusic(music,context)
        this.start()
    }

    fun prev_Original(context: Context,currentMusic: Music,playList: List<Music>){
        this.launchMusic(context, getCircularPrev(playList,playList.indexOf(currentMusic)) as Music)
    }

    fun prev(context: Context,currentMusic: Music? = currentMusicHolder.value,playList: List<Music> = playListHolder.value){
        currentMusic?.let {
            this.launchMusic(
                context,
                getCircularPrev(playList, playList.indexOf(currentMusic)) as Music
            )
        }
    }
    fun next_Original(context: Context,currentMusic: Music,playList: List<Music>){
        this.launchMusic(context, getCircularNext(playList,playList.indexOf(currentMusic)) as Music)
    }

    fun next(context: Context,currentMusic: Music? = currentMusicHolder.value,playList: List<Music> = playListHolder.value){
        currentMusic?.let {
            this.launchMusic(
                context,
                getCircularNext(playList, playList.indexOf(currentMusic)) as Music
            )
        }
    }

    fun repeat(){
        val shouldLoop = !mediaPlayer.isLooping
        mediaPlayer.isLooping = shouldLoop
        CoroutineScope(Dispatchers.IO).launch{
            dsRepo.saveIsLooping(shouldLoop)
        }
    }

    fun shuffle(wasShuffled:Boolean = isShuffledHolder.value){
        _playListHolder.value = modifiedPlayList(
            playListHolder.value,!wasShuffled,
            seedHolder.value
        )
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(!wasShuffled)
            dsRepo.saveRandomSeed(seedHolder.value)
        }
    }

    /** 수정된 playList: List<Music> 을 반환**/
    private fun modifiedPlayList(musicList:List<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}