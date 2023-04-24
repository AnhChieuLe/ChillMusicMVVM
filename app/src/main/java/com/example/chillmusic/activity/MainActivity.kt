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
        binding.imgMenu.setOnClickListener {
            showPopup(it)
        }

        binding.imgNavigation.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigation.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_music -> {}
                R.id.navigation_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.fragmentAlbum -> binding.appbar.visibility = View.GONE
                else -> binding.appbar.visibility = View.VISIBLE
            }
        }
    }

    private fun showPopup(view: View) {
        val listener = PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting       -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.sort_by_name       -> MediaStoreManager.setSortedValue(Sort.BY_NAME)
                R.id.sort_by_duration   -> MediaStoreManager.setSortedValue(Sort.BY_DURATION)
                R.id.sort_by_date       -> MediaStoreManager.setSortedValue(Sort.BY_DATE)
                R.id.sort_by_quality    -> MediaStoreManager.setSortedValue(Sort.BY_QUALITY)
                R.id.sort_by_size       -> MediaStoreManager.setSortedValue(Sort.BY_SIZE)
                R.id.sort_asc           -> MediaStoreManager.setSortedType(SortType.INCREASING)
                R.id.sort_des           -> MediaStoreManager.setSortedType(SortType.DECREASING)
            }
            true
        }
        PopupMenu(this, view, Gravity.END, R.style.MyPopupMenu, R.style.MyPopupMenu).apply {
            inflate(R.menu.toolbar_menu)
            setOnMenuItemClickListener(listener)
        }.show()
    }
}