package com.example.chillmusic.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import com.example.chillmusic.adapter.ViewPagerAdapter
import com.example.chillmusic.databinding.ActivityMusicPlayerBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService

class MusicPlayerActivity : AppCompatActivity() {
    private val currentPlayerViewModel: CurrentPlayer by viewModels()
    lateinit var binding: ActivityMusicPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.currentPlayer = currentPlayerViewModel
        setContentView(binding.root)
        setViewPager()
        observer()
    }

    private fun setViewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(this, currentPlayerViewModel.playList.songs.toLongArray())
        binding.viewPager.currentItem = 1
    }

    private fun observer(){
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        currentPlayerViewModel.song.observe(this){
            if(it == null)  finish()
        }

        currentPlayerViewModel.style.observe(this){
            window.navigationBarColor = it.backgroundColor
            window.navigationBarDividerColor = it.backgroundColor
        }
    }

}