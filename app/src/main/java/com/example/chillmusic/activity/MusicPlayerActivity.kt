package com.example.chillmusic.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.chillmusic.databinding.ActivityMusicPlayerBinding
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService

class MusicPlayerActivity : AppCompatActivity() {
    private val currentPlayerViewModel: CurrentPlayer by viewModels()
    lateinit var binding: ActivityMusicPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setViewModel()
        observer()
        setEvent()
    }

    private fun setViewModel(){
        binding.currentPlayer = currentPlayerViewModel
    }

    private fun setEvent(){
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                currentPlayerViewModel.isTouching.postValue(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                currentPlayerViewModel.isTouching.postValue(false)
                val intent = Intent(this@MusicPlayerActivity, MusicPlayerService::class.java)
                intent.putExtra("action", ACTION_SEEK_TO)
                intent.putExtra("progress", seekBar?.progress)
                startForegroundService(intent)
            }
        })
    }

    private fun observer(){
        currentPlayerViewModel.isActive.observe(this){
            if(!it){
                finish()
            }
        }

        currentPlayerViewModel.liveStyle.observe(this){
            window.navigationBarColor = it.backgroundColor
            window.navigationBarDividerColor = it.backgroundColor
            window.statusBarColor = it.backgroundColor
        }
    }
}