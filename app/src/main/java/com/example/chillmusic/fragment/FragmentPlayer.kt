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
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.adapter.SongAdapter
import com.example.chillmusic.constant.log
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.data.favorite.FavoriteDatabase
import com.example.chillmusic.databinding.FragmentPlayerBinding
import com.example.chillmusic.enums.PlayerState
import com.example.chillmusic.repository.FavoriteRepository
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FragmentPlayer : Fragment() {
    private lateinit var binding : FragmentPlayerBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val songAdapter by lazy { SongAdapter(viewModel) }
    private val state = MediatorLiveData(PlayerState.STATE_HIDE)

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
                R.id.showLyric -> binding.mainLayout.transitionToState(R.id.expanded)
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
        state.addSource(viewModel.song){
            if(it == null)
                state.value = PlayerState.STATE_HIDE
            else if(binding.mainLayout.currentState == R.id.hide){
                state.value = PlayerState.STATE_MINI
            }
        }

        state.observe(viewLifecycleOwner){
            when(it){
                PlayerState.STATE_HIDE -> binding.mainLayout.transitionToState(R.id.hide)
                PlayerState.STATE_MINI -> binding.mainLayout.transitionToState(R.id.minimize)
                PlayerState.STATE_EXPAND -> binding.mainLayout.transitionToState(R.id.expanded)
                PlayerState.STATE_SHOW_PLAYLIST -> binding.mainLayout.transitionToState(R.id.showPlaylist)
                else -> binding.mainLayout.transitionToState(R.id.hide)
            }
        }

        viewModel.playList.observe(viewLifecycleOwner){
            val songs = MediaStoreManager.getSongs(it.songs)
            songAdapter.songs = songs
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

        binding.extensions.lyrics.setOnClickListener {
            binding.mainLayout.transitionToState(R.id.showLyric)
        }

        binding.extensions.timer.setOnClickListener {
            findNavController().navigate(R.id.action_global_dialogFragmentTimePicker)
        }

        binding.controller.favorite.setOnClickListener {
            val db = viewModel.song.value?.toDataBase() ?: return@setOnClickListener
            val dao = FavoriteDatabase.getDatabase(requireContext()).favoriteDao()
            val favoriteRepo = FavoriteRepository(dao)
            MainScope().launch {
                if(viewModel.song.value?.isFavorite == true)
                    favoriteRepo.insert(db)
                else
                    favoriteRepo.delete(db)
            }
        }
    }
}