package com.example.chillmusic.fragment

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

class FragmentDetail : DialogFragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: CurrentPlayer by activityViewModels()
    private val args: FragmentDetailArgs by navArgs()

    companion object {
        fun newInstance(id: Long): FragmentDetail{
            val args = Bundle()
            args.putLong("id", id)
            val fragment = FragmentDetail()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FloatingDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        val song = MediaStoreManager.getSongs(args.id)[0]
        song.extractMetaData()
        binding.song = song

        return binding.root
    }
}