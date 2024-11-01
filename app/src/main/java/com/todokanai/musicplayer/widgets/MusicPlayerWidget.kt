package com.todokanai.musicplayer.widgets

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.Getters.getPlayer
import com.todokanai.musicplayer.myobjects.MyObjects.mainIntent
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent

/**
 * Implementation of App Widget functionality.
 */
class MusicPlayerWidget : AppWidgetProvider() {

    companion object{
        val widgetViews = RemoteViews(appContext.packageName, R.layout.music_player_widget)
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    }
    private val icons = IconsRepository()
    private val player by lazy {getPlayer}
    /*
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val views = widgetViews
        println("widget onUpdate")
        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach {
            updateMyAppWidget(appWidgetManager, it,views,player.currentMusicHolder.value)
        }
    }

     */

    /** stable **/
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        val views = widgetViews
        // Construct the RemoteViews object
        views.run {
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage())
            setImageViewResource(R.id.widget_prevBtn,icons.prev)
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay())
            setImageViewResource(R.id.widget_nextBtn,icons.next)
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage())

            setOnClickPendingIntent(R.id.widget_repeatBtn, PendingIntent.getBroadcast(context,0, repeatIntent,FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_prevBtn, PendingIntent.getBroadcast(context,0, prevIntent,FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_pausePlayBtn, PendingIntent.getBroadcast(context,0, pausePlayIntent,FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_nextBtn, PendingIntent.getBroadcast(context,0, nextIntent,FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_shuffleBtn, PendingIntent.getBroadcast(context,0,shuffleIntent,FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_background, mainIntent)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        context?.let {
            val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MusicPlayerWidget::class.java))
            widgetIds.forEach { updateMyAppWidget_td(appWidgetManager, it, widgetViews,player.currentMusic()) }
        }
    }

    private fun updateMyAppWidget_td(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
        currentMusic: Music
    ) {
        val albumUri = currentMusic.getAlbumUri()
        views.run {
            setTextViewText(R.id.widget_titleText,currentMusic.title)
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage(player.isLooping))
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay(player.isPlaying))
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage(player.isShuffled()))
            println("albumUri: $albumUri")
            Toast.makeText(appContext,"${albumUri?.path}",Toast.LENGTH_SHORT).show()
            setImageViewUri(R.id.widget_imageView, albumUri)   // Todo: updateMyAppWidget의 매 실행마다 이미지가 직전 이미지로 바뀌고 있음
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}