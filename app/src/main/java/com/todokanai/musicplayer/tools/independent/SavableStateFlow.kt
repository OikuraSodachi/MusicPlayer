package com.todokanai.musicplayer.tools.independent

import kotlinx.coroutines.flow.MutableStateFlow

/** MutableStateFlow for saving last known value
 * @param initialValue the initial value
 * @param saveValue callback to save value to database **/
class SavableStateFlow<Type:Any>(
    initialValue:Type,
    private val saveValue:(value:Type)->Unit,
    private val originalStateFlow : MutableStateFlow<Type> = MutableStateFlow(initialValue)
): MutableStateFlow<Type> by originalStateFlow {

    override var value: Type
        get() = originalStateFlow.value
        set(value) {
            originalStateFlow.value = value
            saveValue(value)
        }
}