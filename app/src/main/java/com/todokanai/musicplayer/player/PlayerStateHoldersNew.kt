package com.todokanai.musicplayer.player

/*
class PlayerStateHoldersNew():MediaInterface {

    private val dsRepo by lazy{DataStoreRepository(appContext)}
    lateinit var musicRepo:MusicRepository
 //   private val musicRepo by lazy{MusicRepository()}

    private val _isPlayingHolder_new = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = _isPlayingHolder_new

    fun setIsPlaying(isPlaying:Boolean){
        _isPlayingHolder_new.value = isPlaying
    }
    private val _currentMusicHolder_new = MutableStateFlow<Music>(dummyMusic)
    override val currentMusicHolder : StateFlow<Music>
        get() = _currentMusicHolder_new

    fun setCurrentMusic(music: Music){
        _currentMusicHolder_new.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    private val _isLoopingHolder_new = MutableStateFlow<Boolean>(initialLoop)
    override val isLoopingHolder : StateFlow<Boolean>
        get() = _isLoopingHolder_new

    fun setIsLooping(isLooping:Boolean){
        _isLoopingHolder_new.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private val _isShuffledHolder_new = MutableStateFlow<Boolean>(initialShuffle)
    override val isShuffledHolder : StateFlow<Boolean>
        get() = _isShuffledHolder_new

    fun setShuffle(isShuffled:Boolean,dsRepo: DataStoreRepository){
        _isShuffledHolder_new.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private val _seedHolder_new = MutableStateFlow<Double>(initialSeed)
    override val seedHolder : StateFlow<Double>
        get() = _seedHolder_new

    fun setSeed(seed: Double,dsRepo:DataStoreRepository){
        _seedHolder_new.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveRandomSeed(seed)
        }
    }

    val getAllTemp = MutableStateFlow<Array<Music>>(emptyArray())

    /** Todo: MusicRepo.getAll (Room을 observe) 대신 musicListHolder( Array<Music>)를 사용하도록 변경할것  **/
    override val playListHolder =
        combine(
            //musicRepo.getAll,
            getAllTemp,
            isShuffledHolder,
            seedHolder
        ){ musics ,shuffled,seed ->
            modifiedPlayList(musics.sortedBy{it.title},shuffled,seed)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = modifiedPlayList(initialPlayList,initialShuffle,initialSeed)
        )

    private fun modifiedPlayList(musicList:List<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }
}

 */