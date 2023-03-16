package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.chillmusic.adapter.PlayListAdapter
import com.example.chillmusic.contant.log
import com.example.chillmusic.database.PlayList
import com.example.chillmusic.database.PlayListDataBase
import com.example.chillmusic.database.PlayListRepository
import com.example.chillmusic.databinding.FragmentPlaylistsBinding
import com.example.chillmusic.`object`.CurrentPlayer
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        setPlayList()
        return binding.root
    }

    private fun setPlayList(){
        playListViewModel.playLists.observe(viewLifecycleOwner){
            adapter.playLists = it.toMutableList()
        }
        binding.rcvPlaylist.adapter = adapter
        adapter.setOnClickListener {
            log(it.songs.size.toString())
        }

        adapter.setOnDeleteClickListener {
            playListViewModel.delete(it)
        }
    }
}