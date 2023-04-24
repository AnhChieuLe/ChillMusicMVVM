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
    private val adapter: ArtistAdapter by lazy { ArtistAdapter(viewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapter()
        observer()
        return binding.root
    }

    private fun setAdapter() {
        adapter.data = MediaStoreManager.artists.toMutableList()
        binding.recyclerView.adapter = adapter
    }

    private fun observer(){
        MediaStoreManager.allSong.observe(viewLifecycleOwner){
            adapter.data = MediaStoreManager.artists.toMutableList()
        }
    }
}