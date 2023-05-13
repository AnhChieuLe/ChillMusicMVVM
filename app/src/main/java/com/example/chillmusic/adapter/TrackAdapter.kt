package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.chillmusic.network.api.model.TrackAPI
import com.example.chillmusic.network.crawl.model.Track
import com.example.chillmusic.databinding.ItemTrackBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class TrackAdapter(val currentPlayer: CurrentPlayer) : LifecycleAdapter<TrackAdapter.ViewHolder>() {
    private val tracks: MutableList<SelectableTrack> = mutableListOf()

    fun setData(tracks: List<Track>) {
        val callback = TrackDiffCallback(tracks, this.tracks.map { it.track })
        val result = DiffUtil.calculateDiff(callback)
        this.tracks.clear()
        this.tracks.addAll(tracks.map { SelectableTrack(it) })
        result.dispatchUpdatesTo(this)
    }

    fun setSelected(position: Int){
        for ((index, track) in tracks.withIndex()){
            track.isSelected.postValue(index == position)
        }
    }

    var onSelect: (track: Track, index: Int) -> Unit = { _, _ ->  }

    class ViewHolder(val binding: ItemTrackBinding) : LifecycleViewHolder(binding.root) {
        fun bind(track: SelectableTrack, currentPlayer: CurrentPlayer) {
            binding.lifecycleOwner = this
            binding.track = track
            binding.currentPlayer = currentPlayer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tracks[position], currentPlayer)
        holder.binding.root.setOnClickListener {
            tracks.forEachIndexed { index, track ->
                if(index == position)
                    track.isSelected.postValue(true)
                else
                    track.isSelected.postValue(false)
            }
            onSelect(tracks[position].track, position)
        }
    }

    class TrackDiffCallback(
        private val newList: List<Track>,
        private val oldList: List<Track>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    class SelectableTrack(
        val track: Track,
        val isSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    )
}