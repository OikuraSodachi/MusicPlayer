package com.todokanai.musicplayer.tools.independent

/** @param [list] list of items
 * @param [currentItem] current item
 * @return next element of the [list], or the first element(if [currentItem] is the last element)
 * @throws Exception if [currentItem] is not found in [list] **/
fun <Type:Any> getCircularNext_td(list:List<Type>, currentItem:Type):Type{
    return when (list.indexOf(currentItem)){
        -1 -> throw Exception("item not found in list")
        0 -> list.last()
        else -> list[list.indexOf(currentItem)-1]
    }
}

/** @param [list] list of items
 * @param [currentItem] current item
 * @return previous element of the [list], or the last element(if [currentItem] is the first element)
 * @throws Exception if [currentItem] is not found in [list] **/
fun <Type:Any> getCircularPrev_td(list:List<Type>, currentItem:Type):Type{
    return when (list.indexOf(currentItem)){
        -1 -> throw Exception("item not found in list")
        0 -> list.last()
        else -> list[list.indexOf(currentItem)-1]
    }
}