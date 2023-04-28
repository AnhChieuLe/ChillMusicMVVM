package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.adapter.ArtistAdapter
import com.example.chillmusic.databinding.FragmentArtistBinding
import com.example.chillmusic.repository.MediaViewModel
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentArtist : Fragment() {
    private lateinit var binding: FragmentArtistBinding
    private val viewModel : CurrentPlayer by activityViewModels()
    private val adapter: ArtistAdapter by lazy { ArtistAdapter(viewModel) }
    private val mediaViewModel: MediaViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapter()
        observer()
        return binding.root
    }

    private fun setAdapter() {
        adapter.data = mediaViewModel.artists
        binding.recyclerView.adapter = adapter
    }

    private fun observer(){
        mediaViewModel.songs.observe(viewLifecycleOwner){
            adapter.data = mediaViewModel.artists
        }
    }
}