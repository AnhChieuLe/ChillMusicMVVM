package com.example.chillmusic.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chillmusic.R
import com.example.chillmusic.databinding.ActivityMainBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrentPlayer by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.currentPlayer = viewModel
        setBottomNav()
        setEvent()
    }

    private fun setBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_host) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    private fun setEvent() {
        binding.miniPlayer.miniPlayer.setOnClickListener {
            val intent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(intent)
        }
        viewModel.style.observe(this) {
            window.run {
                navigationBarColor = it.backgroundColor
                statusBarColor = it.backgroundColor
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_host) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentSongsFromPlaylist, R.id.fragmentAddPlayList ->
                    binding.bottomNavigation.visibility = View.GONE

                else ->
                    binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}