package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.chillmusic.activity.MusicPlayerActivity
import com.example.chillmusic.`object`.ListSongManager
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.databinding.FragmentSongsBinding
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.model.Song
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.service.ACTION_START
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayerViewModel
import com.example.chillmusic.viewmodel.PlayListViewModel

class FragmentSongs : Fragment() {
    private val viewModel: CurrentPlayerViewModel by activityViewModels()
    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: SongAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = SongAdapter(ListSongManager.listSong, viewModel).apply {
            setOnItemClickListener {
                startSongs(it)
            }
        }
        binding.rcvSongs.adapter = adapter

        return binding.root
    }

    private fun startSongs(song: Song){
        if(CurrentPlayer.song.value == song) {
            val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
            startActivity(intent)
            return
        }
        CurrentPlayer.run {
            playList = PlayList(ListSongManager.listSong)
            this.song.postValue(song)
            start()
        }
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        intent.putExtra("action", ACTION_START)
        activity?.startService(intent)
    }
}