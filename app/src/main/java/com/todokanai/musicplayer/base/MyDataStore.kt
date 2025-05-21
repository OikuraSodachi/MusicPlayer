package com.todokanai.musicplayer.base

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** DataStore 코드 단순화를 위한? class **/
abstract class MyDataStore(private val appContext: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mydatastore")

    fun <Type : Any> Preferences.Key<Type>.flow(): Flow<Type?> {
        return appContext.dataStore.data.map {
            it[this]
        }
    }

    suspend fun <Type : Any> Preferences.Key<Type>.save(value: Type) {
        appContext.dataStore.edit {
            it[this@save] = value
        }
    }

    suspend fun <Type : Any> Preferences.Key<Type>.value(): Type? {
        return appContext.dataStore.data.first()[this]
    }

    fun <Type : Any> Preferences.Key<Type>.notNullFlow(defaultValue: Type): Flow<Type> {
        return appContext.dataStore.data.map {
            it[this] ?: defaultValue
        }
    }

    suspend fun <Type : Any> Preferences.Key<Type>.notNullValue(defaultValue: Type): Type {
        return appContext.dataStore.data.first()[this] ?: defaultValue
    }
}