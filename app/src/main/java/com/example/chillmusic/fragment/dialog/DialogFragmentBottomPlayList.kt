package com.example.chillmusic.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.R
import com.example.chillmusic.adapter.PlayListAdapter
import com.example.chillmusic.databinding.FragmentAddToPlaylistBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.example.chillmusic.viewmodel.PlayListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogFragmentBottomPlayList : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentAddToPlaylistBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val playListViewModel: PlayListViewModel by activityViewModels()
    private val args: DialogFragmentBottomPlayListArgs by navArgs()
    private val adapter: PlayListAdapter by lazy {
        PlayListAdapter(viewModel)
    }

    companion object {
        fun newInstance(id: Long): DialogFragmentBottomPlayList {
            val args = Bundle()
            args.putLong("id", id)
            val fragment = DialogFragmentBottomPlayList()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme() = R.style.BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddToPlaylistBinding.inflate(inflater, container, false)
        binding.currentPlayer = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setPlayList()
        setEvent()
        return binding.root
    }

    private fun setPlayList(){
        playListViewModel.playLists.observe(viewLifecycleOwner){
            adapter.playLists = it.toMutableList()
        }
        binding.rcvPlaylist.adapter = adapter
        adapter.setOnClickListener {
            val playList = it
            playList.songs.add(args.id)
            playListViewModel.update(playList)
            dismissNow()
        }

        adapter.setOnDeleteClickListener {
            playListViewModel.delete(it)
        }
    }

    private fun setEvent(){
        binding.itemAdd.root.setOnClickListener {
            findNavController().navigate(R.id.open_playlist_add)
        }

        viewModel.style.observe(viewLifecycleOwner) {
            dialog?.window?.navigationBarColor = it.backgroundColor
        }
    }
}