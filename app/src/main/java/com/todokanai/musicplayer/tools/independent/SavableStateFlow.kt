package com.todokanai.musicplayer.tools.independent

import kotlinx.coroutines.flow.MutableStateFlow

/** MutableStateFlow designed for saving last known value
 * @author Oikura Sodachi
 * @param originalStateFlow original MutableStateFlow
 * @param saveValue save value to database **/
data class SavableStateFlow<Type:Any>(
    private val originalStateFlow:MutableStateFlow<Type>,
    private val saveValue:(value:Type)->Unit
): MutableStateFlow<Type> by originalStateFlow {
    override var value: Type
        get() = originalStateFlow.value
        set(newValue) {
            originalStateFlow.value = newValue
            saveValue
        }
}