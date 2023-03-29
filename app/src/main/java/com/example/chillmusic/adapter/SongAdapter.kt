package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.example.chillmusic.databinding.ItemSongBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer

class SongAdapter(
    private val songs: List<Song>,
    private val currentPlayerViewModel: CurrentPlayer
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(){
    var onItemClick: (Song) -> Unit = {}
    var onOptionClick: (Song) -> Unit = {}

    fun setOnItemClickListener(onClick: (Song) -> Unit){
        this.onItemClick = onClick
    }

    fun setOnOptionClickListener(onClick: (Song) -> Unit){
        this.onOptionClick = onClick
    }

    class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner{
        private val lifecycleRegistry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = lifecycleRegistry

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        fun bind(song: Song, currentPlayerViewModel: CurrentPlayer){
            binding.songViewModel = song
            binding.currentPlayerViewModel = currentPlayerViewModel
            binding.lifecycleOwner = this
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, currentPlayerViewModel)
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
}