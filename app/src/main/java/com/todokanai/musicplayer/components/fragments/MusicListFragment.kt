package com.todokanai.musicplayer.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.todokanai.musicplayer.components.view.adapter.MusicRecyclerAdapter
import com.todokanai.musicplayer.databinding.FragmentMusicListBinding
import com.todokanai.musicplayer.viewmodel.MusicListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment : Fragment() {

    private val binding by lazy{FragmentMusicListBinding.inflate(layoutInflater)}
    private val viewModel:MusicListViewModel by viewModels()
    private val lManager by lazy{LinearLayoutManager(context)}

    /** adapter를 lazy 또는 lateinit var 로 선언하면 Swipe를 포함한 RecyclerView가 적용되지 않는 현상 있음
     *  아마 view의 LifeCycle 관련 문제이지 않을까 추측
     *  로그에 찍힌 내용: "No adapter attached; skipping layout"
     * **/
    private val adapterTest by lazy{MusicRecyclerAdapter(
        onItemClick = {viewModel.onMusicClick(requireActivity(),it)},
        itemFlow = viewModel.itemList,
        lifecycleOwner = viewLifecycleOwner
    )}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val adapter = MusicRecyclerAdapter(
            onItemClick = {viewModel.onMusicClick(requireActivity(),it)},
            itemFlow = viewModel.itemList,
            lifecycleOwner = viewLifecycleOwner
        )

        binding.run{
            musicRecyclerView.apply {
                this.adapter = adapter
                this.layoutManager = lManager
                addItemDecoration(DividerItemDecoration(context,lManager.orientation))
            }

            swipe.setOnRefreshListener {
                adapter.notifyDataSetChanged()
                swipe.isRefreshing = false
            }

            /*
            floatingActionButton.run{
                /*
                setOnLongClickListener { v ->
                    v.setOnTouchListener { view, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_MOVE -> {
                              //  if(isInArea(event,swipe)) {
                                    view.x = event.rawX - view.width / 2
                                    view.y = event.rawY - view.height
                             //   }
                            }
                            MotionEvent.ACTION_UP -> view.setOnTouchListener(null)
                            else -> {}
                        }
                        true
                    }
                    true
                }
                 */
                setOnClickListener{
                    viewModel.floatingButton(requireActivity())
                }
            }
             */
        }

        viewModel.itemList.asLiveData().observe(viewLifecycleOwner){
            adapter.itemList = it
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
}