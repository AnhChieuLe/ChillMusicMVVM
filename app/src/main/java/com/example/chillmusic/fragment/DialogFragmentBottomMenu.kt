package com.example.chillmusic.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentBottomMenuBinding
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class DialogFragmentBottomMenu : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomMenuBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val args: DialogFragmentBottomMenuArgs by navArgs()

    companion object {
        fun newInstance(id: Long): DialogFragmentBottomMenu{
            val args = Bundle()
            args.putLong("id", id)
            val fragment = DialogFragmentBottomMenu()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme() = R.style.BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBottomMenuBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        observer()
        handleAction()
        return binding.root
    }

    private fun observer(){
        val menu = binding.bottomMenu.menu
        viewModel.song.observe(viewLifecycleOwner){
            menu.findItem(R.id.menu_play_next).isVisible = it != null
            menu.findItem(R.id.menu_add_to_wait_list).isVisible = it != null
            menu.findItem(R.id.menu_delete).isVisible = it?.id != args.id
        }
    }

    private fun handleAction(){
        val id = args.id
        binding.bottomMenu.setNavigationItemSelectedListener {
            findNavController().popBackStack()
            when(it.itemId){
                R.id.menu_play_next -> viewModel.addToNext(id)
                R.id.menu_add_to_wait_list -> viewModel.addToLast(id)
                R.id.menu_play_new -> playNew(id)
                R.id.menu_playlist_add -> addToPlayList(id)
                R.id.menu_view_info -> showInfo(id)
                R.id.menu_update_info -> {}
                R.id.menu_delete -> delete(id)
            }
            true
        }
    }

    private fun playNew(id: Long){
        viewModel.newPlayList(id)
        viewModel.song.postValue(MediaStoreManager.getSongs(id))
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        activity?.startService(intent)
    }

    private fun addToPlayList(id: Long){
        val action = MainNavDirections.openBottomPlaylist(id)
        findNavController().navigate(action)
    }

    private fun showInfo(id: Long){
        val action = MainNavDirections.openDetail(id)
        findNavController().navigate(action)
    }

    private fun delete(id: Long){
        val path = MediaStoreManager.getSongs(id)?.path ?: return
        val file = File(path)
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn muốn xóa bài hát này")
            .setPositiveButton("Ẩn"){ dialog, which ->

            }
            .setNegativeButton("Xóa") { dialog, which ->
                file.delete()
            }
            .setNeutralButton("Hủy"){ dialog, which ->

            }
        val dialog = builder.create()
        dialog.show()
    }
}