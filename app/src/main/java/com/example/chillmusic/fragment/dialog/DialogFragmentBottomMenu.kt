package com.example.chillmusic.fragment.dialog

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.MainNavDirections
import com.example.chillmusic.R
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.data.blacklist.BlackList
import com.example.chillmusic.data.blacklist.BlackListDatabase
import com.example.chillmusic.data.favorite.Favorite
import com.example.chillmusic.data.favorite.FavoriteDatabase
import com.example.chillmusic.databinding.FragmentBottomMenuBinding
import com.example.chillmusic.repository.BlackListRepository
import com.example.chillmusic.repository.FavoriteRepository
import com.example.chillmusic.service.MusicPlayerService
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class DialogFragmentBottomMenu : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomMenuBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val args: DialogFragmentBottomMenuArgs by navArgs()
    private val song by lazy { MediaStoreManager.getSongs(args.id) }

    companion object {
        fun newInstance(id: Long): DialogFragmentBottomMenu {
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
        handleMenu()
        handleAction()
        return binding.root
    }

    private fun observer(){
        val menu = binding.bottomMenu.menu
        viewModel.song.observe(viewLifecycleOwner){
            menu.findItem(R.id.menu_play_next).isVisible = it != null
            menu.findItem(R.id.menu_add_to_wait_list).isVisible = it != null
            menu.findItem(R.id.menu_delete).isVisible = it?.id != args.id
            if(it?.id == args.id){
                menu.findItem(R.id.menu_add_to_favorite).isVisible      = false
                menu.findItem(R.id.menu_add_to_black_list).isVisible    = false
                menu.findItem(R.id.menu_remove_favorite).isVisible      = false
                menu.findItem(R.id.menu_remove_black_list).isVisible    = false
            }else{
                handleMenu()
            }
        }
        viewModel.style.observe(viewLifecycleOwner) {
            dialog?.window?.navigationBarColor = it.itemBackGround
            val color = ColorUtils.setAlphaComponent(it.contentColor, 4)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(color))
        }
    }

    private fun handleMenu(){
        val menu = binding.bottomMenu.menu
        song?.let {
            menu.findItem(R.id.menu_add_to_favorite).isVisible   = !it.isFavorite
            menu.findItem(R.id.menu_remove_favorite).isVisible   = it.isFavorite
            menu.findItem(R.id.menu_add_to_black_list).isVisible = !it.isBlackList
            menu.findItem(R.id.menu_remove_black_list).isVisible = it.isBlackList
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
                R.id.menu_add_to_favorite -> addToFavorite(id)
                R.id.menu_remove_favorite -> removeFavorite(id)
                R.id.menu_add_to_black_list -> addToBlackList(id)
                R.id.menu_remove_black_list -> removeBlackList(id)
            }
            true
        }
    }

    private fun removeBlackList(id: Long) {
        val dao = BlackListDatabase.getDatabase(requireContext()).blackListDao()
        val blackListRepository = BlackListRepository(dao)
        MainScope().launch {
            blackListRepository.delete(BlackList(id))
        }
    }

    private fun removeFavorite(id: Long) {
        val dao = FavoriteDatabase.getDatabase(requireContext()).favoriteDao()
        val favoriteRepository = FavoriteRepository(dao)
        MainScope().launch {
            favoriteRepository.delete(Favorite(id))
        }
    }

    private fun addToBlackList(id: Long) {
        val dao = BlackListDatabase.getDatabase(requireContext()).blackListDao()
        val blackListRepository = BlackListRepository(dao)
        MainScope().launch {
            blackListRepository.insert(BlackList(id))
        }
    }

    private fun addToFavorite(id: Long) {
        val dao = FavoriteDatabase.getDatabase(requireContext()).favoriteDao()
        val favoriteRepository = FavoriteRepository(dao)
        MainScope().launch {
            favoriteRepository.insert(Favorite(id))
        }
    }

    private fun playNew(id: Long){
        viewModel.newPlayList(id)
        viewModel.song.postValue(song)
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
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.delete)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn muốn xóa bài hát này")
            .setPositiveButton("Ẩn"){ dialog, which ->

            }
            .setNegativeButton("Xóa") { dialog, which ->
                file.delete()
            }
        val dialog = builder.create()
        dialog.show()
    }
}