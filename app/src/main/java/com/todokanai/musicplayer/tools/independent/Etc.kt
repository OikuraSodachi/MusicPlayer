package com.todokanai.musicplayer.tools.independent

/**
 * @param [list] list of items
 * @param [current] current item
 * @return next element of the [list], or the first element(if [current] is the last element). -1 if [list] does not contain [current]
 * **/
fun <Type:Any> getCircularNext_td(list:List<Type>, current:Type):Type{
    val currentIndex = list.indexOf(current)
    return if(currentIndex == list.size-1){
        list.first()
    } else{
        list[currentIndex+1]
    }
}

/**
 * @param [list] list of items
 * @param [current] current item
 * @return previous element of the [list], or the last element( if [current] is the first element )
 * **/
fun <Type:Any> getCircularPrev_td(list:List<Type>,current:Type):Type{
    val currentIndex = list.indexOf(current)
    return if(currentIndex == 0){
        list.last()
    } else{
        list[currentIndex-1]
    }
}