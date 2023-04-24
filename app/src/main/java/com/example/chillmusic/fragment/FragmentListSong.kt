package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.chillmusic.R
import com.example.chillmusic.activity.SettingsActivity
import com.example.chillmusic.adapter.ListSongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentListSongBinding
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
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
        binding.refresh.setOnRefreshListener {
            activity?.let {
                MainScope().launch {
                    MediaStoreManager.refreshMediaStore(it.application){
                        MediaStoreManager.loadSong(it.application)
                        MediaStoreManager.loadAlbum()
                        MediaStoreManager.loadArtist()
                    }
                    binding.refresh.isRefreshing = false
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = ListSongAdapter(this)
        binding.viewpager.adapter = adapter
        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bài hát"
                1 -> "Danh sách phát"
                2 -> "Album"
                3 -> "Artist"
                else -> ""
            }
        }
        mediator.attach()
    }


}