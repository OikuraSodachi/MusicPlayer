package com.todokanai.musicplayer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class DataStoreRepository(private val appContext: Context) {
    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mydatastore")
        val DATASTORE_SORT_BY = stringPreferencesKey("datastore_sort_by")

        val DATASTORE_IS_SHUFFLED = booleanPreferencesKey("datastore_is_shuffled")
        val DATASTORE_IS_LOOPING = booleanPreferencesKey("datastore_is_looping")
        val DATASTORE_RANDOM_SEED = doublePreferencesKey("datastore_random_seed")

        val DATASTORE_SHOULD_STOP_ON_NOISY = booleanPreferencesKey("datastore_should_stop_on_noisy")
    }


    fun saveSortBy(value:String){
        CoroutineScope(Dispatchers.IO).launch {
            appContext.dataStore.edit{
                it[DATASTORE_SORT_BY] = value
            }
        }
    }

    suspend fun sortBy() : String? {
        return appContext.dataStore.data.first()[DATASTORE_SORT_BY]
    }

    val sortBy: Flow<String?> = appContext.dataStore.data.map{
        it[DATASTORE_SORT_BY]
    }

    suspend fun saveIsShuffled(isShuffled:Boolean){
        appContext.dataStore.edit {
            it[DATASTORE_IS_SHUFFLED] = isShuffled
        }
    }

    suspend fun isShuffled() : Boolean {
        return appContext.dataStore.data.first()[DATASTORE_IS_SHUFFLED] ?: false
    }

    val isShuffled : Flow<Boolean> = appContext.dataStore.data.map {
        it[DATASTORE_IS_SHUFFLED] ?: false
    }

    suspend fun saveIsLooping(isLooping:Boolean){
        appContext.dataStore.edit{
            it[DATASTORE_IS_LOOPING] = isLooping
        }
    }

    suspend fun isLooping() : Boolean{
        return appContext.dataStore.data.first()[DATASTORE_IS_LOOPING] ?:false
    }

    val isLooping: Flow<Boolean> = appContext.dataStore.data.map {
        it[DATASTORE_IS_LOOPING] ?: false
    }

    val seed : Flow<Double> = appContext.dataStore.data.map {
        it[DATASTORE_RANDOM_SEED] ?: 0.0
    }

    suspend fun getSeed() : Double {
        return appContext.dataStore.data.first()[DATASTORE_RANDOM_SEED]?:0.1
    }

    suspend fun saveRandomSeed(seed:Double){
        appContext.dataStore.edit {
            it[DATASTORE_RANDOM_SEED] = seed
        }
    }
}