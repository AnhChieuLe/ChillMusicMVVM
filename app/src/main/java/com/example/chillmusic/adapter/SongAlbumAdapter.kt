package com.example.chillmusic.adapter

import android.graphics.Paint.Style
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chillmusic.databinding.ItemSongAlbumBinding
import com.example.chillmusic.databinding.ItemSongBinding
import com.example.chillmusic.library.MusicStyle
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class SongAlbumAdapter(
    val style: MusicStyle
) : LifecycleAdapter<SongAlbumAdapter.ViewHolder>(){
    var onItemClick: (Song) -> Unit = {}
    var onOptionClick: (Song) -> Unit = {}

    var songs: List<Song> = listOf()
    set(value) {
        val callback = SongDiffCallback(value, field)
        val result = DiffUtil.calculateDiff(callback)
        field = value
        result.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(onClick: (Song) -> Unit){
        this.onItemClick = onClick
    }

    fun setOnOptionClickListener(onClick: (Song) -> Unit){
        this.onOptionClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        holder.bind(song, style)
        holder.binding.root.setOnClickListener {
            onItemClick(song)
        }
        holder.binding.imgMore.setOnClickListener {
            onOptionClick(song)
        }

        holder.binding.root.setOnLongClickListener {
            onOptionClick(song)
            true
        }
    }

    class ViewHolder(val binding: ItemSongAlbumBinding) : LifecycleViewHolder(binding.root){
        fun bind(song: Song, style: MusicStyle){
            binding.song = song
            binding.style = style
            binding.lifecycleOwner = this
        }
    }

    class SongDiffCallback(
        private val newList : List<Song>,
        private val oldList : List<Song>
    ) : DiffUtil.Callback(){
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
    }
}