package com.todokanai.musicplayer.tools

class TestClass {

    fun getCircularNext(list:List<Any>,currentIndex:Int):Any{
        return if(currentIndex == list.size-1){
            list.first()
        } else{
            list[currentIndex+1]
        }
    }

}