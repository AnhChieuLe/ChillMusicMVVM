package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chillmusic.databinding.ItemSongBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayerViewModel

class SongAdapter(
    private val songs: List<Song>,
    private val currentPlayerViewModel: CurrentPlayerViewModel
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(){
    var onItemClick: (Song) -> Unit = {}

    fun setOnItemClickListener(onClick: (Song) -> Unit){
        this.onItemClick = onClick
    }

    class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(song: Song, currentPlayerViewModel: CurrentPlayerViewModel){
            binding.songViewModel = song
            binding.currentPlayerViewModel = currentPlayerViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position], currentPlayerViewModel)
        holder.binding.root.setOnClickListener {
            onItemClick(songs[position])
        }
    }
}