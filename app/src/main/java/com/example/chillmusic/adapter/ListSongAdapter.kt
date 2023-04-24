package com.example.chillmusic.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.fragment.*

class ListSongAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        val fragmentSongs = FragmentAllSong()
        val fragmentPlaylists = FragmentPlaylists()
        val fragmentAlbums = FragmentAlbums()
        val fragmentArtist = FragmentArtist()

        return when (position) {
            0 -> fragmentSongs
            1 -> fragmentPlaylists
            2 -> fragmentAlbums
            3 -> fragmentArtist
            else -> fragmentSongs
        }
    }
}