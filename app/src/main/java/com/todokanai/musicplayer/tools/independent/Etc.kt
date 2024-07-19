package com.todokanai.musicplayer.tools.independent

fun getCircularNext(list:List<Any>, currentIndex:Int):Any{
    return if(currentIndex == list.size-1){
        list.first()
    } else{
        list[currentIndex+1]
    }
}

fun getCircularPrev(list:List<Any>,currentIndex: Int):Any{
    return if(currentIndex == 0){
        list.last()
    } else{
        list[currentIndex-1]
    }
}