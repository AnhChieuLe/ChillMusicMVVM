package com.example.chillmusic.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.chillmusic.R

class FragmentSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}