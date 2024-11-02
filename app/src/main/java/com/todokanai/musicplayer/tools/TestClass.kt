package com.todokanai.musicplayer.tools

import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.tools.independent.getCircularNext_td

class TestClass {

    fun setMusic(
        targetMusic:Music,
        playList:List<Music>,
        setMusicPrimitive:(Music)->Unit,
        trialCount:Int = 1,
        onFailure:()->Unit,
        onListEmpty:()->Unit
    ):Unit{
        try{
            if(playList.isNotEmpty()) {
                setMusicPrimitive(targetMusic)
            }else{
                onListEmpty()
            }
        }catch (e:Exception){
            e.printStackTrace()
            if(playList.size != trialCount) {
                setMusic(
                    getCircularNext_td(playList, playList.indexOf(targetMusic)),
                    playList,
                    setMusicPrimitive,
                    trialCount+1,
                    onFailure,
                    onListEmpty
                )
            }else{
                onFailure()
            }
        }
    }
}