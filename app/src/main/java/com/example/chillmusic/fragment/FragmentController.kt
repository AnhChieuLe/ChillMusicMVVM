package com.example.chillmusic.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.databinding.ActivityMusicPlayerBinding
import com.example.chillmusic.databinding.FragmentControllerBinding
import com.example.chillmusic.service.ACTION_SEEK_TO
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer

class FragmentController : Fragment() {
    private val currentPlayerViewModel: CurrentPlayer by activityViewModels()
    lateinit var binding: FragmentControllerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentControllerBinding.inflate(inflater, container, false)
        setViewModel()
        setEvent()
        return binding.root
    }

    private fun setViewModel(){
        binding.currentPlayer = currentPlayerViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setEvent(){
        binding.seekbarGroup.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                currentPlayerViewModel.isTouching.postValue(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                currentPlayerViewModel.isTouching.postValue(false)
                val intent = Intent(requireContext(), MusicPlayerService::class.java)
                intent.putExtra("action", ACTION_SEEK_TO)
                intent.putExtra("progress", seekBar?.progress)
                activity?.startForegroundService(intent)
            }
        })
    }
}