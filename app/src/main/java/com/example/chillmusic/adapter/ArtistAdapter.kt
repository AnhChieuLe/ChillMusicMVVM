package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.example.chillmusic.databinding.ItemAlbumBinding
import com.example.chillmusic.databinding.ItemArtistBinding
import com.example.chillmusic.model.Album
import com.example.chillmusic.model.Artist
import com.example.chillmusic.viewmodel.CurrentPlayer

class ArtistAdapter(val viewModel: CurrentPlayer) : LifecycleAdapter<ArtistAdapter.ViewHolder>(){
    var data : List<Artist> = listOf()

    class ViewHolder(val binding: ItemArtistBinding) : LifecycleViewHolder(binding.root){
        fun bind(viewModel: CurrentPlayer, artist: Artist){
            binding.lifecycleOwner = this
            binding.artist = artist
            binding.currentPlayer = viewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, data[position])
    }
}