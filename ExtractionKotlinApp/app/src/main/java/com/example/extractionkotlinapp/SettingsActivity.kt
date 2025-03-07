package com.example.extractionkotlinapp

import android.os.Bundle
import com.example.extractionkotlinapp.ui.settings.SettingsFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Check if the fragment is already added, if not, add it
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(android.R.id.content, SettingsFragment()) // Load SettingsFragment
            }
        }
    }
}



