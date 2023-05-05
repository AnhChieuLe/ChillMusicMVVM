package com.example.chillmusic.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media.session.MediaButtonReceiver
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.chillmusic.R
import com.example.chillmusic.constant.log
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.ActivityMainBinding
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.library.MusicStyle.Companion.getDarkness
import com.example.chillmusic.repository.MediaViewModel
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
        setEvent()
    }

    private fun observer() {
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        viewModel.style.observe(this) {
            window.navigationBarColor = it.backgroundColor
            window.statusBarColor = it.backgroundColor
            wic.isAppearanceLightStatusBars = !it.isDarkBackGround
            wic.isAppearanceLightNavigationBars = !it.isDarkBackGround
        }
    }

    private fun setEvent(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

        }
    }
}