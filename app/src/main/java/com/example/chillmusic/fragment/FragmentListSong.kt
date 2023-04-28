package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.ListSongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentListSongBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FragmentListSong : Fragment() {
    private lateinit var binding: FragmentListSongBinding
    private lateinit var adapter: ListSongAdapter
    private val viewModel: CurrentPlayer by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListSongBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        setAdapter()
        setEvent()
        return binding.root
    }

    private fun setEvent() {
//        binding.refresh.setOnRefreshListener {
//            activity?.let {
//                MainScope().launch {
//                    MediaStoreManager.refreshMediaStore(it.application){
//                        MediaStoreManager.loadData(it.application.contentResolver)
//                        binding.refresh.isRefreshing = false
//                    }
//                }
//            }
//        }
    }

    private fun setAdapter() {
        adapter = ListSongAdapter(this)
        binding.viewpager.adapter = adapter
        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bài hát"
                1 -> "Danh sách có sẵn"
                2 -> "Danh sách yêu thích"
                3 -> "Danh sách phát"
                4 -> "Album"
                5 -> "Artist"
                6 -> "Danh sách đen"
                else -> ""
            }
        }
        mediator.attach()
    }


}