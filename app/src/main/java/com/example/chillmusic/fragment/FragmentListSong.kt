package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.R
import com.example.chillmusic.activity.SettingsActivity
import com.example.chillmusic.adapter.ListSongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentListSongBinding
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.repository.MediaViewModel
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FragmentListSong : Fragment() {
    private lateinit var binding: FragmentListSongBinding
    private lateinit var adapter: ListSongAdapter
    private val viewModel: CurrentPlayer by activityViewModels()
    private val mediaViewModel: MediaViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListSongBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        setAdapter()
        setEvent()
        return binding.root
    }

    private fun setEvent() {
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
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun setAdapter() {
        adapter = ListSongAdapter(this)
        binding.viewpager.adapter = adapter
        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bài hát"
                1 -> "Danh sách có sẵn"
                2 -> "Danh sách yêu thích"
                3 -> "Danh sách phát"
                4 -> "Album"
                5 -> "Artist"
                6 -> "Danh sách đen"
                else -> ""
            }
        }
        mediator.attach()
    }

    private fun showPopup(view: View) {
        val listener = PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting       -> startActivity(Intent(requireContext(), SettingsActivity::class.java))
                R.id.sort_by_name       -> mediaViewModel.setSort(Sort.BY_NAME)
                R.id.sort_by_duration   -> mediaViewModel.setSort(Sort.BY_DURATION)
                R.id.sort_by_date       -> mediaViewModel.setSort(Sort.BY_DATE)
                R.id.sort_by_quality    -> mediaViewModel.setSort(Sort.BY_QUALITY)
                R.id.sort_by_size       -> mediaViewModel.setSort(Sort.BY_SIZE)
                R.id.sort_asc           -> mediaViewModel.setSortType(SortType.INCREASING)
                R.id.sort_des           -> mediaViewModel.setSortType(SortType.DECREASING)
            }
            true
        }
        PopupMenu(requireContext(), view, Gravity.END, R.style.MyPopupMenu, R.style.MyPopupMenu).apply {
            inflate(R.menu.toolbar_menu)
            setOnMenuItemClickListener(listener)
        }.show()
    }
}