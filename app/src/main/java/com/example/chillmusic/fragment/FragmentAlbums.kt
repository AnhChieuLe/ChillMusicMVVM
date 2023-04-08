package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.AlbumAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentAlbumsBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentAlbums : Fragment() {
    private lateinit var binding: FragmentAlbumsBinding
    private val viewModel : CurrentPlayer by activityViewModels()
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        adapter = AlbumAdapter(viewModel)
        adapter.data = MediaStoreManager.albums.toMutableList()
        binding.recyclerView.adapter = adapter
    }
}