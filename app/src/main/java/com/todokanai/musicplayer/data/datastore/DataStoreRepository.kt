package com.todokanai.musicplayer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todokanai.musicplayer.base.MyDataStore
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(appContext: Context): MyDataStore(appContext) {
    companion object{
        val DATASTORE_SORT_BY = stringPreferencesKey("datastore_sort_by")

        val DATASTORE_IS_SHUFFLED = booleanPreferencesKey("datastore_is_shuffled")
        val DATASTORE_IS_LOOPING = booleanPreferencesKey("datastore_is_looping")
        val DATASTORE_RANDOM_SEED = longPreferencesKey("datastore_random_seed")

        val DATASTORE_CURRENT_MUSIC = stringPreferencesKey("datastore_current_music")

        val DATASTORE_MEDIA_BUTTON_ENABLED = booleanPreferencesKey("datastore_enable_media_button")
        val DATASTORE_SHOULD_STOP_ON_NOISY = booleanPreferencesKey("datastore_should_stop_on_noisy")
    }

    suspend fun saveSortBy(value:String) = DATASTORE_SORT_BY.save(value)
    suspend fun sortBy() = DATASTORE_SORT_BY.value()
    val sortBy = DATASTORE_SORT_BY.flow()

    suspend fun saveIsShuffled(isShuffled:Boolean) = DATASTORE_IS_SHUFFLED.save(isShuffled)
    suspend fun isShuffled() = DATASTORE_IS_SHUFFLED.notNullValue(defaultValue = false)
    val isShuffled = DATASTORE_IS_SHUFFLED.notNullFlow(defaultValue = false)

    suspend fun saveIsLooping(isLooping:Boolean) = DATASTORE_IS_LOOPING.save(isLooping)
    suspend fun isLooping() = DATASTORE_IS_LOOPING.notNullValue(defaultValue = false)
    val isLooping = DATASTORE_IS_LOOPING.notNullFlow(defaultValue = false)

    suspend fun saveRandomSeed(seed:Long) = DATASTORE_RANDOM_SEED.save(seed)
    suspend fun getSeed() = DATASTORE_RANDOM_SEED.notNullValue(defaultValue = 2L)
    val seed = DATASTORE_RANDOM_SEED.notNullFlow(defaultValue = 2L)

    suspend fun saveEnableMediaButton(enabled:Boolean) = DATASTORE_MEDIA_BUTTON_ENABLED.save(enabled)
    suspend fun isMediaButtonEnabled() = DATASTORE_MEDIA_BUTTON_ENABLED.notNullValue(defaultValue = true)
    val isMediaButtonEnabled = DATASTORE_MEDIA_BUTTON_ENABLED.notNullFlow(true)

    suspend fun saveCurrentMusic(absolutePath:String) = DATASTORE_CURRENT_MUSIC.save(absolutePath)
    suspend fun currentMusic() = DATASTORE_CURRENT_MUSIC.notNullValue(defaultValue = dummyMusic.fileDir)
    val currentMusic = DATASTORE_CURRENT_MUSIC.notNullFlow(defaultValue = dummyMusic.fileDir)

}