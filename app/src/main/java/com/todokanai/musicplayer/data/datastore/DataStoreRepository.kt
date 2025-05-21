package com.todokanai.musicplayer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todokanai.musicplayer.base.MyDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(appContext: Context): MyDataStore(appContext) {
    companion object{
        val DATASTORE_SORT_BY = stringPreferencesKey("datastore_sort_by")

        val DATASTORE_IS_SHUFFLED = booleanPreferencesKey("datastore_is_shuffled")
        val DATASTORE_IS_LOOPING = booleanPreferencesKey("datastore_is_looping")
        val DATASTORE_RANDOM_SEED = doublePreferencesKey("datastore_random_seed")

        val DATASTORE_CURRENT_MUSIC = stringPreferencesKey("datastore_current_music")

        val DATASTORE_MEDIA_BUTTON_ENABLED = booleanPreferencesKey("datastore_enable_media_button")
        val DATASTORE_SHOULD_STOP_ON_NOISY = booleanPreferencesKey("datastore_should_stop_on_noisy")
    }

    suspend fun saveSortBy(value:String) = DATASTORE_SORT_BY.save(value)
    suspend fun sortBy() = DATASTORE_SORT_BY.value()
    val sortBy = DATASTORE_SORT_BY.flow()

    suspend fun saveIsShuffled(isShuffled:Boolean) = DATASTORE_IS_SHUFFLED.save(isShuffled)
    suspend fun isShuffled() = DATASTORE_IS_SHUFFLED.value()
    val isShuffled = DATASTORE_IS_SHUFFLED.flow()

    suspend fun saveIsLooping(isLooping:Boolean) = DATASTORE_IS_LOOPING.save(isLooping)
    suspend fun isLooping() = DATASTORE_IS_LOOPING.value()
    val isLooping = DATASTORE_IS_LOOPING.flow()

    suspend fun getSeed() = DATASTORE_RANDOM_SEED.value() ?:0.0
    suspend fun saveRandomSeed(seed:Double) = DATASTORE_RANDOM_SEED.save(seed)
    val seed = DATASTORE_RANDOM_SEED.flow()

    suspend fun saveEnableMediaButton(enabled:Boolean) = DATASTORE_MEDIA_BUTTON_ENABLED.save(enabled)
    suspend fun isMediaButtonEnabled() = DATASTORE_MEDIA_BUTTON_ENABLED.value()
    val isMediaButtonEnabled = DATASTORE_MEDIA_BUTTON_ENABLED.flow()

    suspend fun saveCurrentMusic(absolutePath:String) = DATASTORE_CURRENT_MUSIC.save(absolutePath)
    suspend fun currentMusic() = DATASTORE_CURRENT_MUSIC.value()
    val currentMusic = DATASTORE_CURRENT_MUSIC.flow()

}