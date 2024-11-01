package com.todokanai.musicplayer.player

import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.myobjects.MyObjects.dummyMusic
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.tools.independent.getCircularNext_td
import com.todokanai.musicplayer.tools.independent.getCircularPrev_td
import javax.inject.Inject

class PlayListManager @Inject constructor(
    dsRepo:DataStoreRepository,
    musicRepo:MusicRepository,
    initialMusic:Music = dummyMusic,
    initialPlayList:List<Music> = emptyList(),
    initialShuffle:Boolean = false,
    initialSeed:Double = 0.1,
):PlayListManager_abstract(
    initialMusic,
    initialPlayList,
    initialShuffle,
    initialSeed,
    dsRepo,
    musicRepo
) {

  //  val shuffled = shuffledFlow

    override fun getNextMusic(): Music {
        val playList = playList()
      //  println("prev: ${playList[playList.indexOf(currentMusic())-1]}, next: ${playList[playList.indexOf(currentMusic())+1]}")
        return getCircularNext_td(playList,playList.indexOf(currentMusic()))
        ///return dummyMusic
    }

    override fun getPrevMusic(): Music {
        val playList = playList()

      //  println("prev: ${playList[playList.indexOf(currentMusic())-1].title}, next: ${playList[playList.indexOf(currentMusic())+1].title}")

        return getCircularPrev_td(playList,playList.indexOf(currentMusic()))
    }

}