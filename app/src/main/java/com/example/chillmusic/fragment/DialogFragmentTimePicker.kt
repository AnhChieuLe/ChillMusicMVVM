package com.example.chillmusic.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.activityViewModels
import com.example.chillmusic.R
import com.example.chillmusic.databinding.FragmentTimePickerBinding
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogFragmentTimePicker : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentTimePickerBinding
    private val viewModel: CurrentPlayer by activityViewModels()

    override fun getTheme() = R.style.BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimePickerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.currentPlayer = viewModel
        setEvent()
        return binding.root
    }

    private fun setEvent() {
        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.create.setOnClickListener {
            val hour   = binding.picker.hour.value * 3600
            val minute = binding.picker.minute.value * 60
            val second = binding.picker.second.value

            val timeInMillis = (hour + minute + second) * 1000.toLong()
            viewModel.startCountDown(timeInMillis)
            dismiss()
        }
    }
}