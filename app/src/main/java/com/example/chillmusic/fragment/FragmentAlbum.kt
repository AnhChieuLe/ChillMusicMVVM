package com.example.chillmusic.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.R
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentAlbumBinding
import com.example.chillmusic.model.Album
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentAlbum : Fragment(){
    private lateinit var binding: FragmentAlbumBinding
    private val currentPlayer: CurrentPlayer by activityViewModels()
    private val adapter by lazy { SongAdapter(currentPlayer) }
    private val args: FragmentAlbumArgs by navArgs()
    private val album : Album by lazy {
        args.album
    }

    companion object {
        fun newInstance(album: Album?): FragmentAlbum{
            val args = Bundle()
            args.putSerializable("album", album)
            val fragment = FragmentAlbum()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.album = album
        binding.currentPlayer = currentPlayer
        setListSong()
        return binding.root
    }

    private fun setListSong(){
        val ids = album.ids.toLongArray()
        val songs = MediaStoreManager.getSongs(*ids)
        adapter.songs = songs.toMutableList()
        binding.rcvSongs.adapter = adapter
    }
}