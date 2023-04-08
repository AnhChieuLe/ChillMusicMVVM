package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentPlayerBinding
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentPlayer : Fragment() {
    private lateinit var binding : FragmentPlayerBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val songAdapter by lazy { SongAdapter(viewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        observer()
        setPlaylist()
        setEvent()
        onBackCallback()
        return binding.root
    }

    private fun onBackCallback() {
        activity?.onBackPressedDispatcher?.addCallback {
            when (binding.mainLayout.currentState) {
                R.id.minimize -> activity?.finish()
                R.id.expanded -> binding.mainLayout.transitionToState(R.id.minimize)
                R.id.showPlaylist -> binding.mainLayout.transitionToState(R.id.expanded)
            }
        }
    }

    private fun setPlaylist(){
        songAdapter.setOnItemClickListener {
            viewModel.song.postValue(it)
            viewModel.start()
        }
        binding.fragmentPlaylist.adapter = songAdapter
    }

    private fun observer() {
        viewModel.song.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.mainLayout.transitionToState(R.id.hide)
            } else if (binding.mainLayout.currentState == R.id.hide) {
                binding.mainLayout.transitionToState(R.id.minimize)
            }
        }

        viewModel.playList.observe(viewLifecycleOwner){
            songAdapter.songs = MediaStoreManager.getSongs(*it.songs.toLongArray()).toMutableList()
        }
    }

    private fun setEvent() {
        binding.seekbar.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.isTouching.postValue(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.isTouching.postValue(false)
                val intent = Intent(requireContext(), MusicPlayerService::class.java)
                intent.putExtra("action", ACTION_SEEK_TO)
                intent.putExtra("progress", seekBar?.progress)
                activity?.startForegroundService(intent)
            }
        })
        binding.extensions.playlist.setOnClickListener {
            if(binding.mainLayout.currentState == R.id.expanded)
                binding.mainLayout.transitionToState(R.id.showPlaylist)
        }

        binding.extensions.playlistAdd.setOnClickListener {
            val action = MainNavDirections.openBottomPlaylist(viewModel.song.value!!.id)
            findNavController().navigate(action)
        }
    }
}