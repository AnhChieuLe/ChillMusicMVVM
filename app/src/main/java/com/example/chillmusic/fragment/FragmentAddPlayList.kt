package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chillmusic.adapter.SelectionSongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.databinding.FragmentNewPlaylistBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.viewmodel.PlayListViewModel

class FragmentAddPlayList : Fragment(){
    lateinit var binding: FragmentNewPlaylistBinding
    private val currentPlayerViewModel: CurrentPlayer by activityViewModels()
    private val playListViewModel: PlayListViewModel by activityViewModels()
    private val adapter by lazy { SelectionSongAdapter(MediaStoreManager.songs.toMutableList(), currentPlayerViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = currentPlayerViewModel
        binding.playList = playListViewModel
        setEvent()
        setSelectableList()
        return binding.root
    }

    private fun setEvent() {
        binding.btnAdd.setOnClickListener {
            insertPlayList()
            findNavController().popBackStack()
        }
    }

    private fun insertPlayList(){
        val name = binding.edtName.text.toString()
        val ids = adapter.getData().toMutableList()
        playListViewModel.insert(PlayList(name, ids))
        binding.edtName.clearComposingText()
    }

    private fun setSelectableList(){
        binding.recyclerview.adapter = adapter
    }
}