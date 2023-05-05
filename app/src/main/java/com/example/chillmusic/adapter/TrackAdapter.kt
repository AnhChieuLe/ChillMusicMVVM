package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.example.chillmusic.api.model.Track
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

    var onSelect: (Track) -> Unit = {}

    fun setDefaultSelected() {
        if (tracks.isNotEmpty()) {
            clearSelected()
            tracks[0].isSelected.value = true
        }
    }

    fun setSelected(id: Int) {
        clearSelected()
        tracks.forEach { if (id == it.track.id) it.isSelected.value = true }
    }

    private fun clearSelected() {
        tracks.forEach { it.isSelected.value = false }
    }

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
            tracks.forEach { it.isSelected.postValue(false) }
            tracks[position].isSelected.postValue(true)
            onSelect(tracks[position].track)
        }
    }

    class TrackDiffCallback(
        private val newList: List<Track>,
        private val oldList: List<Track>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
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