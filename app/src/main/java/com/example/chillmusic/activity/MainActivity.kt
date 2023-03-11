package com.example.chillmusic.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.chillmusic.R
import com.example.chillmusic.adapter.MainViewPagerAdapter
import com.example.chillmusic.contant.log
import com.example.chillmusic.databinding.ActivityMainBinding
import com.example.chillmusic.viewmodel.CurrentPlayerViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainViewPagerAdapter
    private val viewModel: CurrentPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.currentPlayer = viewModel
        binding.lifecycleOwner = this
        setViewPager()
        bindViewPager()
        setEvent()
    }

    private fun setEvent() {
        binding.miniPlayer.miniPlayer.setOnClickListener {
            val intent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(intent)
        }
        viewModel.song.observe(this){
            window.navigationBarColor = viewModel.style.get()!!.backgroundColor
            window.navigationBarDividerColor = viewModel.style.get()!!.backgroundColor
            window.statusBarColor = viewModel.style.get()!!.backgroundColor
        }
    }

    private fun setViewPager(){
        adapter = MainViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
    }

    private fun bindViewPager(){
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_songs -> binding.viewPager.currentItem = 0
                R.id.nav_playlist -> binding.viewPager.currentItem = 1
                R.id.nav_setting -> binding.viewPager.currentItem = 2
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> binding.bottomNavigation.menu.findItem(R.id.nav_songs).isChecked = true
                    1 -> binding.bottomNavigation.menu.findItem(R.id.nav_playlist).isChecked = true
                    2 -> binding.bottomNavigation.menu.findItem(R.id.nav_setting).isChecked = true
                }
            }
        })
    }
}