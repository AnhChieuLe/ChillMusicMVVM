package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.SelectionSongAdapter
import com.example.chillmusic.contant.log
import com.example.chillmusic.database.PlayList
import com.example.chillmusic.databinding.FragmentNewPlaylistBinding
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.`object`.SongsManager
import com.example.chillmusic.viewmodel.PlayListViewModel

class FragmentAddPlayList : Fragment(){
    lateinit var binding: FragmentNewPlaylistBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val playListViewModel: PlayListViewModel by activityViewModels()
    private val adapter by lazy { SelectionSongAdapter(SongsManager.listSong, viewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        binding.playList = playListViewModel
        setEvent()
        setAdapter()
        return binding.root
    }

    private fun setEvent() {
        binding.btnAdd.setOnClickListener {
            val name = binding.edtName.text.toString()
            val ids = adapter.getData()
            playListViewModel.insert(PlayList(name, ids))
            binding.edtName.clearComposingText()
        }
    }

    private fun setAdapter(){
        binding.recyclerview.adapter = adapter
    }
}