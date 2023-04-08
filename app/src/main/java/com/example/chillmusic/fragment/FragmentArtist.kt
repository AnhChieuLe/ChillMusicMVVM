package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.AlbumAdapter
import com.example.chillmusic.adapter.ArtistAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentAlbumsBinding
import com.example.chillmusic.databinding.FragmentArtistBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentArtist : Fragment() {
    private lateinit var binding: FragmentArtistBinding
    private val viewModel : CurrentPlayer by activityViewModels()
    private lateinit var adapter: ArtistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        adapter = ArtistAdapter(viewModel)
        adapter.data = MediaStoreManager.artists.toMutableList()
        binding.recyclerView.adapter = adapter
    }
}