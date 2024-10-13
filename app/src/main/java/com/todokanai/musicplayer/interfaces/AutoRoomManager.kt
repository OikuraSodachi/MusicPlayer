package com.todokanai.musicplayer.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface AutoRoomManager<Type:Any> {

    val holder : MutableStateFlow<Type>

    fun setValue(value:Type){
        holder.value = value
        CoroutineScope(Dispatchers.IO).launch {
            roomBackup(value)
        }
    }

    /** room에 가장 최근 값을 저장. 이걸 observe 하지 않고, holder를 observe 해야 함. **/
    suspend fun roomBackup(value:Type)

}