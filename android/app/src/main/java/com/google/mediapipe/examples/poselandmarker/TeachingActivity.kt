package com.google.mediapipe.examples.poselandmarker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.poselandmarker.databinding.ActivityMainBinding

class TeachingActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaching)

        statusTextView = findViewById(R.id.realtime_text)
    }

    fun showOverlayText(message: String) {
        statusTextView.text = message
        statusTextView.visibility = View.VISIBLE
    }
    override fun onBackPressed() {
        finish()
    }
}