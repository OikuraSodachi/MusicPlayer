package com.todokanai.musicplayer

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.todokanai.musicplayer.components.service.MusicService.Companion.customPlayer
import com.todokanai.musicplayer.compose.IconsRepository
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
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
    }
    private val icons = IconsRepository()

    val appWidgetManager = AppWidgetManager.getInstance(appContext)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val views = widgetViews
        println("widget onUpdate")
        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach {
            updateAppWidget(appWidgetManager, it,views)

        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        val views = widgetViews
        // Construct the RemoteViews object
        views.run {
            setTextViewText(R.id.widget_titleText,customPlayer.currentMusicHolder.value?.title)
            setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage())
            setImageViewResource(R.id.widget_prevBtn,icons.prev)
            setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay())
            setImageViewResource(R.id.widget_nextBtn,icons.next)
            setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage())

            setOnClickPendingIntent(R.id.widget_repeatBtn, repeatIntent)
            setOnClickPendingIntent(R.id.widget_prevBtn, prevIntent)
            setOnClickPendingIntent(R.id.widget_pausePlayBtn, pausePlayIntent)
            setOnClickPendingIntent(R.id.widget_nextBtn, nextIntent)
            setOnClickPendingIntent(R.id.widget_shuffleBtn, shuffleIntent)
        }
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        println("onReceive: Widget")
        context?.let {
            val widgetIds = this.appWidgetManager.getAppWidgetIds(ComponentName(context, MusicPlayerWidget::class.java))
            widgetIds.forEach {
                updateAppWidget(appWidgetManager, it, widgetViews)
            }
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    views: RemoteViews
) {
    println("updateAppWidget")
    val icons = IconsRepository()
    // Construct the RemoteViews object

    views.run {
        setTextViewText(R.id.widget_titleText,customPlayer.currentMusicHolder.value?.title)
        setImageViewResource(R.id.widget_repeatBtn,icons.loopingImage())
        setImageViewResource(R.id.widget_prevBtn,icons.prev)
        setImageViewResource(R.id.widget_pausePlayBtn,icons.pausePlay())
        setImageViewResource(R.id.widget_nextBtn,icons.next)
        setImageViewResource(R.id.widget_shuffleBtn,icons.shuffledImage())
    }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}