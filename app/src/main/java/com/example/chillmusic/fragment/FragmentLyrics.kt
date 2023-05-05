package com.example.chillmusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chillmusic.R
import com.example.chillmusic.adapter.TrackAdapter
import com.example.chillmusic.api.model.Track
import com.example.chillmusic.databinding.FragmentLyricsBinding
import com.example.chillmusic.library.LyricManager
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import kotlinx.coroutines.*

class FragmentLyrics : Fragment() {
    private lateinit var binding: FragmentLyricsBinding
    private val currentPlayer: CurrentPlayer by activityViewModels()
    private val adapter: TrackAdapter by lazy { TrackAdapter(currentPlayer) }
    private val lyricManager = LyricManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLyricsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = currentPlayer
        setRecyclerView()
        observer()
        return binding.root
    }

    private fun observer() {
        currentPlayer.lyric.addSource(lyricManager.lyrics) {
            currentPlayer.lyric.postValue(it)
        }
        currentPlayer.song.observe(viewLifecycleOwner) {
            lyricManager.clear()
            loadLyrics(it)
        }
        lyricManager.tracks.observe(viewLifecycleOwner) {
            setData(it)
        }
    }

    private fun setData(tracks: List<Track>){
        binding.tracks.visibility = if(tracks.size < 2) View.GONE else View.VISIBLE
        adapter.setData(tracks)
    }

    private fun loadLyrics(song: Song?) {
        song ?: return
        MainScope().launch {
            val handle = CoroutineExceptionHandler { job, throwable ->
                throwable.printStackTrace()
                job.cancel()
            }
            supervisorScope {
                launch(handle) { lyricManager.setLyric(song) }
                launch(handle) {
                    lyricManager.setTracks(song)
                    lyricManager.setDefaultLyrics { adapter.setDefaultSelected() }
                }
            }
        }
    }

    private fun setRecyclerView() {
        val handle = CoroutineExceptionHandler { job, throwable -> job.cancel(); throwable.printStackTrace() }
        binding.tracks.adapter = adapter
        adapter.onSelect = {
            MainScope().launch(handle) { lyricManager.setLyric(it.id) }
        }
        val divider = DividerItemDecoration(requireContext(), LinearLayout.HORIZONTAL)
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.divider, null) ?: return
        divider.setDrawable(drawable)
        binding.tracks.addItemDecoration(divider)
    }
}