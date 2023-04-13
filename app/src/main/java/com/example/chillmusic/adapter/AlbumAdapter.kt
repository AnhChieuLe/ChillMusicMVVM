package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.example.chillmusic.databinding.ItemAlbumBinding
import com.example.chillmusic.model.Album
import com.example.chillmusic.viewmodel.CurrentPlayer

class AlbumAdapter(val viewModel: CurrentPlayer) : LifecycleAdapter<AlbumAdapter.ViewHolder>(){
    var data : MutableList<Album> = mutableListOf()

    class ViewHolder(val binding: ItemAlbumBinding) : LifecycleViewHolder(binding.root){
        fun bind(viewModel: CurrentPlayer, album: Album){
            binding.lifecycleOwner = this
            binding.album = album
            binding.currentPlayer = viewModel
        }
    }

    private var onItemClick: (Album)->Unit = {}
    fun setOnItemClickListener(action: (Album)->Unit){
        onItemClick = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, data[position])
        holder.binding.root.setOnClickListener {
            onItemClick(data[position])
        }
    }
}