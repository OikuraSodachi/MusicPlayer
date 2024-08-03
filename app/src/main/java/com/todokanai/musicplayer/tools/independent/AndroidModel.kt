package com.todokanai.musicplayer.tools.independent

/**
 * 독립적으로 사용 가능하고, Android 의존성 있는 함수 모음
 */
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Handler
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

/**
 * Application 종료 함수
 * Service가 실행중일 경우 서비스를 실행한 Intent도 입력할것
 */
fun exit_td(activity: Activity, serviceIntent: Intent? = null){
    ActivityCompat.finishAffinity(activity)
    serviceIntent?.let{ activity.stopService(it) }     // 서비스 종료
    System.runFinalization()
    exitProcess(0)
}

suspend fun getThumbnail_td(file: File,width:Int = 100,height:Int = 100): Bitmap = withContext(Dispatchers.IO){
    ThumbnailUtils.extractThumbnail(
        BitmapFactory.decodeFile(file.absolutePath), width, height)
}

fun durationText_td(duration:Int?):String{
    if(duration == null){
        return "null"
    } else if(duration < 3600000){
        return SimpleDateFormat("mm:ss").format(duration)
    }else {
        return SimpleDateFormat("hh:mm:ss").format(duration)
    }
}

/**
 *  looper.
 *
 *  Main thread 바깥에서 호출 용도
 */
fun callHandler_td(
    handler:Handler,
    callback:()->Unit
){
    handler.post({callback()})
}

/*
/** Logcat Tag 매크로 용도 **/
fun setupTimber_td() {
    Timber.plant(
        object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                val className = super.createStackElementTag(element)
                return "TAG $className ${element.methodName}"
            }
        }
    )
}
 */

fun setMediaPlaybackState_td(state:Int,mediaSession:MediaSessionCompat){
    val playbackState = PlaybackStateCompat.Builder()
        .apply {
            val actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            } else {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            }
            setActions(actions)
        }
        .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
    mediaSession.setPlaybackState(playbackState.build())
}

fun isPermissionGranted_td(activity: Activity, permission: String):Boolean{
    return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED
}

/** requestCode를 111로 냅둬도 무방한게 정말 맞는지 확인 필요
 *
 * system에 permission n개를 한번에 요청함
 **/
fun requestPermission_td(
    activity: Activity,
    permissions: Array<String>,
    permissionNotice:()->Unit,
    requestCode:Int = 111
){
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permissions.first()
        )
    ) {
        permissionNotice()
    } else {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }
}