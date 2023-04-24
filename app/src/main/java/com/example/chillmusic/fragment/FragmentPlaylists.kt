package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.adapter.PlayListAdapter
import com.example.chillmusic.databinding.FragmentPlaylistsBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.viewmodel.PlayListViewModel

class FragmentPlaylists : Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val playListViewModel: PlayListViewModel by activityViewModels()
    private val adapter: PlayListAdapter by lazy {
        PlayListAdapter(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        binding.currentPlayer = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setPlayList()
        setEvent()
        return binding.root
    }

    private fun setPlayList(){
        playListViewModel.playLists.observe(viewLifecycleOwner){
            adapter.playLists = it.toMutableList()
        }
        binding.rcvPlaylist.adapter = adapter
        adapter.setOnClickListener {
            val ids = it.songs.toLongArray()
            val action = MainNavDirections.actionPlayListToSongs(ids)
            findNavController().navigate(action)
        }

        adapter.setOnDeleteClickListener {
            playListViewModel.delete(it)
        }
    }

    private fun setEvent(){
        binding.itemAdd.root.setOnClickListener {
            findNavController().navigate(R.id.open_playlist_add)
        }
    }
}