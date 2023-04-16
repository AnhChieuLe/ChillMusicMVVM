package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.chillmusic.R
import com.example.chillmusic.activity.SettingsActivity
import com.example.chillmusic.adapter.ListSongAdapter
import com.example.chillmusic.databinding.FragmentListSongBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.tabs.TabLayoutMediator

class FragmentListSong : Fragment() {
    private lateinit var binding: FragmentListSongBinding
    private lateinit var adapter: ListSongAdapter
    private val viewModel: CurrentPlayer by activityViewModels()

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
    }

    private fun showPopup(view: View) {
        val listener = PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_setting -> {
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        PopupMenu(requireContext(), view, Gravity.END, R.style.MyPopupMenu, R.style.MyPopupMenu).apply {
            inflate(R.menu.toolbar_menu)
            setOnMenuItemClickListener(listener)
        }.show()
    }

    private fun setAdapter() {
        adapter = ListSongAdapter(this)
        binding.viewpager.adapter = adapter
        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bài hát"
                1 -> "Danh sách phát"
                2 -> "Album"
                3 -> "Artist"
                else -> ""
            }
        }
        mediator.attach()
    }
}