package com.todokanai.musicplayer.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow

/** Handles recyclerView Item update **/
abstract class BaseRecyclerAdapter<E:Any,VH:RecyclerView.ViewHolder>(
    private val itemFlow: Flow<List<E>>,
    private val lifecycleOwner: LifecycleOwner
):RecyclerView.Adapter<VH>() {

    open var itemList = emptyList<E>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        // Todo: Memory Leak이 발생하는지 여부 체크할 것
        itemFlow.asLiveData().observe(lifecycleOwner){
            itemList = it
            //println("itemList: $itemList")
            notifyDataSetChanged()
        }
        super.onAttachedToRecyclerView(recyclerView)
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
}