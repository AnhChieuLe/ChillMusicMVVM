package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.activity.MusicPlayerActivity
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.databinding.FragmentSongsBinding
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.service.ACTION_START
import com.example.chillmusic.service.MusicPlayerService

class FragmentSongs : Fragment() {
    private val viewModel: CurrentPlayer by activityViewModels()
    private lateinit var binding: FragmentSongsBinding
    private val adapter: SongAdapter by lazy { SongAdapter(songs, viewModel) }
    private val args: FragmentSongsArgs by navArgs()
    private val songs by lazy {
        args.songs?.let { MediaStoreManager.getSongs(*it) } ?: MediaStoreManager.songs.toMutableList()
    }

    companion object {
        fun newInstance(ids: LongArray): FragmentSongs{
            val args = Bundle()
            args.putLongArray("songs", ids)
            val fragment = FragmentSongs()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        binding.rcvSongs.adapter = adapter
        adapter.setOnItemClickListener { startSongs(it) }
        adapter.setOnOptionClickListener {
//            val action = MainNavDirections.openOptionMenu(it.id)
//            findNavController().navigate(action)
            val dialog = FragmentBottomMenu.newInstance(it.id)
            activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomMenu") }
        }

        return binding.root
    }

    private fun startSongs(song: Song){
        if(CurrentPlayer.song.value == song) {
            val intent = Intent(requireContext(), MusicPlayerActivity::class.java)
            startActivity(intent)
            return
        }
        CurrentPlayer.run {
            playList = PlayList("Tất cả bản nhạc", songs.map { it.id }.toMutableList())
            this.song.postValue(song)
            start()
        }
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        intent.putExtra("action", ACTION_START)
        activity?.startService(intent)
    }
}