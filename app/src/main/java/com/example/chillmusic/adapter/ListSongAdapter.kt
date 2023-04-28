package com.example.chillmusic.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.fragment.*

class ListSongAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val listFragment = listOf(
        FragmentAllSong(),
        FragmentList(),
        FragmentFavorite(),
        FragmentPlaylists(),
        FragmentAlbums(),
        FragmentArtist(),
        FragmentBlackList(),
    )

    override fun getItemCount() = listFragment.size

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}