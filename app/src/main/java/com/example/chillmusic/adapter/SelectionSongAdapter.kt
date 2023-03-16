package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.example.chillmusic.databinding.ItemSongSelectionBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.`object`.CurrentPlayer

class SelectionSongAdapter(
    private val songs : List<Song> = emptyList(),
    private val viewModel: CurrentPlayer
) : RecyclerView.Adapter<SelectionSongAdapter.ViewHolder>(){
    private val selectableSongs = songs.map { SelectableSong(it) }

    fun getData(): List<Int>{
        return selectableSongs.filter {
            it.selected
        }.map { it.song.id }
    }

    class ViewHolder(val binding: ItemSongSelectionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(song: SelectableSong, viewModel: CurrentPlayer){
            binding.songViewModel = song
            binding.currentPlayerViewModel = viewModel
        }
    }

    class SelectableSong(
        val song: Song,
        var selected: Boolean = false
    )

    override fun getItemCount() = songs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(selectableSongs[position], viewModel)
        holder.binding.root.setOnClickListener {
            holder.binding.icCheck.isChecked = !holder.binding.icCheck.isChecked
        }
    }


}