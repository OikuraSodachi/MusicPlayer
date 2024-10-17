package com.todokanai.musicplayer.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.MyObjects.mainIntent
import com.todokanai.musicplayer.myobjects.MyObjects.nextIntent
import com.todokanai.musicplayer.myobjects.MyObjects.pausePlayIntent
import com.todokanai.musicplayer.myobjects.MyObjects.prevIntent
import com.todokanai.musicplayer.myobjects.MyObjects.repeatIntent
import com.todokanai.musicplayer.myobjects.MyObjects.shuffleIntent
import dagger.hilt.android.AndroidEntryPoint

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
class MusicPlayerWidget : AppWidgetProvider() {

    companion object{
        val widgetViews = RemoteViews(appContext.packageName, R.layout.music_player_widget)
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    }
    private val icons = IconsRepository()
    private val player by lazy{ customPlayer}

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val currentMusic = player.currentMusicHolder.value
        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach {
            println("widget : updateAppWidget")

            // Construct the RemoteViews object
            val albumUri = currentMusic.getAlbumUri()
            widgetViews.run {
                setTextViewText(R.id.widget_titleText,currentMusic.title)
                setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage())
                setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay())
                setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage())
                setImageViewUri(R.id.widget_imageView, albumUri)   // Todo: updateMyAppWidget의 매 실행마다 이미지가 직전 이미지로 바뀌고 있음. setImageViewUri 내부 구조 확인해야 할듯?

            }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(it, widgetViews)        }
    }

    override fun onEnabled(context: Context) {
        println("widget : onEnabled")
        // Construct the RemoteViews object
        widgetViews.run {
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage())
            setImageViewResource(R.id.widget_prevBtn,icons.prev)
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay())    // 아마 paused 상태를 기준으로 시작해야 할듯?
            setImageViewResource(R.id.widget_nextBtn,icons.next)
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage())

            setOnClickPendingIntent(R.id.widget_repeatBtn, repeatIntent)
            setOnClickPendingIntent(R.id.widget_prevBtn, prevIntent)
            setOnClickPendingIntent(R.id.widget_pausePlayBtn, pausePlayIntent)
            setOnClickPendingIntent(R.id.widget_nextBtn, nextIntent)
            setOnClickPendingIntent(R.id.widget_shuffleBtn, shuffleIntent)
            setOnClickPendingIntent(R.id.widget_background, mainIntent)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
       // println("widget : onReceive")
        if (context != null) {
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(ComponentName(appContext,MusicPlayerWidget::class.java)))
        }
        super.onReceive(context, intent)
    }
}