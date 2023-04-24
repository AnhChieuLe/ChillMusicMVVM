package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.constant.log
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.databinding.FragmentSongsBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.service.MusicPlayerService

class FragmentAllSong : Fragment() {
    private val viewModel: CurrentPlayer by activityViewModels()
    private lateinit var binding: FragmentSongsBinding
    private val adapter: SongAdapter by lazy { SongAdapter(viewModel) }
    private val songs get() = MediaStoreManager.songs.toMutableList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setListSong()
        observer()
        return binding.root
    }

    private fun setListSong(){
        adapter.songs = songs
        binding.rcvSongs.adapter = adapter
        adapter.setOnItemClickListener { startSongs(it) }
        adapter.setOnOptionClickListener {
            val action = MainNavDirections.openOptionMenu(it.id)
            findNavController().navigate(action)
        }
    }

    private fun observer(){
        MediaStoreManager.allSong.observe(viewLifecycleOwner){
            adapter.songs = songs
        }
    }

    private fun startSongs(song: Song){
        CurrentPlayer.run {
            playList.value = PlayList("Tất cả bản nhạc", songs.map { it.id }.toMutableList())
            this.song.postValue(song)
            start()
        }
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        activity?.startService(intent)
    }
}