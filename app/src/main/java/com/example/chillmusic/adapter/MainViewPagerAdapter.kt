package com.example.chillmusic.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chillmusic.fragment.FragmentAddPlayList
import com.example.chillmusic.fragment.FragmentPlaylists
import com.example.chillmusic.fragment.FragmentSongs

class MainViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragmentSongs()
            1 -> FragmentPlaylists()
            2 -> FragmentAddPlayList()
            else -> FragmentSongs()
        }
    }
}