package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chillmusic.R
import com.example.chillmusic.adapter.TrackAdapter
import com.example.chillmusic.constant.log
import com.example.chillmusic.network.crawl.MusixMatch
import com.example.chillmusic.network.crawl.model.Track
import com.example.chillmusic.databinding.FragmentLyricsBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import kotlinx.coroutines.*

class FragmentLyrics : Fragment() {
    private lateinit var binding: FragmentLyricsBinding
    private val currentPlayer: CurrentPlayer by activityViewModels()
    private val adapter: TrackAdapter by lazy { TrackAdapter(currentPlayer) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLyricsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = currentPlayer
        setRecyclerView()
        observer()
        return binding.root
    }

    private fun observer() {
        currentPlayer.tracks.observe(viewLifecycleOwner){
            adapter.setData(it)
        }

        currentPlayer.trackIndex.observe(viewLifecycleOwner){
            adapter.setSelected(it)
            binding.tracks.scrollToPosition(it)
        }
    }

    private fun setRecyclerView() {
        binding.tracks.adapter = adapter
        adapter.onSelect = { _, index ->
           currentPlayer.trackIndex.postValue(index)
        }
        val divider = DividerItemDecoration(requireContext(), LinearLayout.HORIZONTAL)
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.divider, null) ?: return
        divider.setDrawable(drawable)
        binding.tracks.addItemDecoration(divider)
    }
}