package com.example.chillmusic.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.R
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.adapter.SongAlbumAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentPlaylistBinding
import com.example.chillmusic.library.MusicStyle
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.repository.MediaViewModel
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentPlaylist : Fragment(){
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val mediaViewModel: MediaViewModel by activityViewModels()
    private val args: FragmentPlaylistArgs by navArgs()
    private val id by lazy { args.id }
    private val album get() = MediaStoreManager.getAlbum(id)
    private val songs get() = MediaStoreManager.getSongs(album?.ids ?: listOf())

    companion object {
        fun newInstance(ids: LongArray): FragmentPlaylist{
            val args = Bundle()
            args.putLongArray("ids", ids)
            val fragment = FragmentPlaylist()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.album = album
        binding.currentPlayer = viewModel
        setRecyclerView()
        return binding.root
    }

    private fun setRecyclerView(){
        val adapter = SongAlbumAdapter(album?.style ?: MusicStyle(BitmapFactory.decodeResource(resources, R.drawable.avatar)))
        binding.rcvSongs.adapter = adapter
        adapter.songs = songs
        adapter.setOnItemClickListener {
            CurrentPlayer.run {
                playList.value = album?.toPlayList()
                this.song.postValue(it)
                start()
            }
            val intent = Intent(requireContext(), MusicPlayerService::class.java)
            activity?.startService(intent)
        }
    }
}