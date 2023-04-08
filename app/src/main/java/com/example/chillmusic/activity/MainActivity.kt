package com.example.chillmusic.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.ActivityMainBinding
import com.example.chillmusic.fragment.FragmentSongs
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrentPlayer by viewModels()
    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_host) as NavHostFragment
        navHostFragment.navController
    }

    companion object {
        const val ACTION_EXPAND = "ACTION_EXPAND"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.currentPlayer = viewModel
        setContentView(binding.root)
        observer()
        //handleIntent(intent)
    }

//    private fun handleIntent(intent: Intent?){
//        if(intent?.action == ACTION_EXPAND){
//            binding.mainLayout.transitionToState(R.id.expanded)
//        }
//    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        handleIntent(intent)
//    }

    private fun observer() {
        viewModel.style.observe(this) {
            window.navigationBarColor = it.backgroundColor
            window.statusBarColor = it.backgroundColor
        }
    }
}