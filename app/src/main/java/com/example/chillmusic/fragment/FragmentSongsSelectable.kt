package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.databinding.FragmentSongsBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.`object`.SongsManager

class FragmentSongsSelectable : Fragment() {
    private val viewModel: CurrentPlayer by activityViewModels()
    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: SongAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = SongAdapter(SongsManager.listSong, viewModel)
        adapter.setOnItemClickListener {
            startSongs(it)
        }
        binding.rcvSongs.adapter = adapter

        return binding.root
    }

    private fun startSongs(song: Song){

    }
}