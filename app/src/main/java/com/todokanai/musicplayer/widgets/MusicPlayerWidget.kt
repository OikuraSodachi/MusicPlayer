package com.todokanai.musicplayer.widgets

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.MyObjects.mainIntent
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent
import com.todokanai.musicplayer.player.CustomPlayerNewWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
class MusicPlayerWidget : AppWidgetProvider() {

    companion object{
        val widgetViews = RemoteViews(appContext.packageName, R.layout.music_player_widget)
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    }

    @Inject
    lateinit var icons:IconsRepository

   // @Inject
 //   lateinit var player: CustomPlayer

    @Inject
    lateinit var player:CustomPlayerNewWrapper

    private fun getWidgetIds(context: Context) = appWidgetManager.getAppWidgetIds(ComponentName(context, MusicPlayerWidget::class.java))

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        println("widget onUpdate")
        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach {
            updateMyAppWidget_td(appWidgetManager, it,widgetViews,player)
        }
    }

    /** stable **/
    override fun onEnabled(context: Context) {
        widgetViews.run {
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage(player.isLooping))
            setImageViewResource(R.id.widget_prevBtn,icons.prev)
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay(player.isPlaying))
            setImageViewResource(R.id.widget_nextBtn,icons.next)
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage(player.isShuffled()))

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
            onUpdate(context, appWidgetManager,getWidgetIds(context))
        }
    }

    private fun updateMyAppWidget_td(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
        player: CustomPlayerNewWrapper
    ) {
        val currentMusic = player.currentMusic()
        val albumUri = currentMusic.getAlbumUri()
        views.run {
            setTextViewText(R.id.widget_titleText,currentMusic.title)
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage(player.isLooping))
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay(player.isPlaying))
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage(player.isShuffled()))
          //  println("albumUri: $albumUri")
        //    Toast.makeText(appContext,"${albumUri?.path}",Toast.LENGTH_SHORT).show()
            setImageViewUri(R.id.widget_imageView, albumUri)   // Todo: updateMyAppWidget의 매 실행마다 이미지가 직전 이미지로 바뀌고 있음
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}