package com.todokanai.musicplayer.tools.independent

fun <Type:Any> getCircularNext_td(list:List<Type>, currentIndex:Int):Type{
    return if(currentIndex == list.size-1){
        list.first()
    } else{
        list[currentIndex+1]
    }
}

fun <Type:Any> getCircularPrev_td(list:List<Type>,currentIndex:Int):Type{
    return if(currentIndex == 0){
        list.last()
    } else{
        list[currentIndex-1]
    }
}