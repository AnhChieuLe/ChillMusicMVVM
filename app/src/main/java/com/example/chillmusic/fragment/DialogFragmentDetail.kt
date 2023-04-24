package com.example.chillmusic.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.chillmusic.R
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.databinding.FragmentDetailBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogFragmentDetail : DialogFragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val args: DialogFragmentDetailArgs by navArgs()

    companion object {
        fun newInstance(id: Long): DialogFragmentDetail{
            val args = Bundle()
            args.putLong("id", id)
            val fragment = DialogFragmentDetail()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme() = R.style.FloatingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        val song = MediaStoreManager.getSongs(args.id)
        song?.extractMetaData()
        binding.song = song

        return binding.root
    }
}