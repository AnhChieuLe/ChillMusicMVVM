package com.example.chillmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.databinding.ItemPlaylistBinding
import com.example.chillmusic.viewmodel.CurrentPlayer

class PlayListAdapter(
    private val viewModel: CurrentPlayer,
) : RecyclerView.Adapter<PlayListAdapter.ViewHolder>(){
    var playLists: MutableList<PlayList> = mutableListOf()
    set(value) {
        val callback = PlayListDiffCallback(value, field)
        val result = DiffUtil.calculateDiff(callback)
        field.clear()
        field.addAll(value)
        result.dispatchUpdatesTo(this)
    }

    private var onClick: (PlayList) -> Unit = {}
    fun setOnClickListener(listener: (PlayList) -> Unit){
        onClick = listener
    }

    private var delete: (PlayList) -> Unit = {}
    fun setOnDeleteClickListener(listener: (PlayList) -> Unit){
        delete = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = playLists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, playLists[position])
        holder.binding.root.setOnClickListener {
            onClick(playLists[position])
        }
        holder.binding.btnDelete.setOnClickListener {
            delete(playLists[position])
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    class ViewHolder(val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root), LifecycleOwner{
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

        fun bind(currentPlayer: CurrentPlayer, playList: PlayList){
            binding.currentPlayer = currentPlayer
            binding.playlist = playList
            binding.lifecycleOwner = this
        }
    }

    class PlayListDiffCallback(
        private val newList: List<PlayList>,
        private val oldList: List<PlayList>,
    ) : DiffUtil.Callback(){
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].equal(newList[newItemPosition])
        }
    }
}