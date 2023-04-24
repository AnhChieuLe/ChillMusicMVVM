package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.adapter.AlbumAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentAlbumsBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentAlbums : Fragment() {
    private lateinit var binding: FragmentAlbumsBinding
    private val viewModel : CurrentPlayer by activityViewModels()
    private val adapter: AlbumAdapter by lazy { AlbumAdapter(viewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setAdapter()
        observer()
        return binding.root
    }

    private fun setAdapter() {
        adapter.data = MediaStoreManager.albums.toMutableList()
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener {
            val action = MainNavDirections.openAlbum(it)
            findNavController().navigate(action)
        }
    }

    private fun observer(){
        MediaStoreManager.allSong.observe(viewLifecycleOwner){
            adapter.data = MediaStoreManager.albums.toMutableList()
        }
    }
}