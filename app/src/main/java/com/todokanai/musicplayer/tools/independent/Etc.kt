package com.todokanai.musicplayer.tools.independent

fun <Type:Any> getCircularNext_td(list:List<Type>, current:Type):Type{
    val currentIndex = list.indexOf(current)
    return if(currentIndex == list.size-1){
        list.first()
    } else{
        list[currentIndex+1]
    }
}

fun <Type:Any> getCircularPrev_td(list:List<Type>,current:Type):Type{
    val currentIndex = list.indexOf(current)
    return if(currentIndex == 0){
        list.last()
    } else{
        list[currentIndex-1]
    }
}