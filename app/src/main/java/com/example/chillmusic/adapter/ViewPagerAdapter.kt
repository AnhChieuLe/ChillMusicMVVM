package com.example.chillmusic.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.fragment.FragmentController
import com.example.chillmusic.fragment.FragmentSongs
import com.example.chillmusic.fragment.FragmentSongsArgs

class ViewPagerAdapter(
    fragment: FragmentActivity,
    ids: LongArray
) : FragmentStateAdapter(fragment) {
    private val fragmentSongs = FragmentSongs.newInstance(ids)
    private val fragmentController = FragmentController()

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> fragmentSongs
            1 -> fragmentController
            else -> FragmentSongs()
        }
    }
}