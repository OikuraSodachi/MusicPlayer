package com.todokanai.musicplayer.base

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

abstract class MyDataStore(private val dataStore: DataStore<Preferences>) {
    fun <Type:Any> Preferences.Key<Type>.flow(): Flow<Type?> {
        return dataStore.data.map {
            it[this]
        }
    }

    suspend fun <Type:Any> Preferences.Key<Type>.save(value:Type){
        dataStore.edit {
            it[this@save] = value
        }
    }

    suspend fun <Type:Any> Preferences.Key<Type>.value():Type?{
        return dataStore.data.first()[this]
    }
}