package com.google.mediapipe.examples.poselandmarker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.poselandmarker.databinding.ActivityMainBinding
import com.google.mediapipe.examples.poselandmarker.fragment.CameraFragment

class TeachingActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var statusTextView: TextView

    private var currentStageInt: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaching)

        statusTextView = findViewById(R.id.realtime_text)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.navigation.setupWithNavController(navController)
        activityMainBinding.navigation.setOnNavigationItemReselectedListener {
            // ignore the reselection
        }

        findViewById<Button>(R.id.button_standing).setOnClickListener {
            updateCurrentStage(1)
        }

        findViewById<Button>(R.id.button_back_swing).setOnClickListener {
            updateCurrentStage(2)
        }

        findViewById<Button>(R.id.button_follow_through).setOnClickListener {
            updateCurrentStage(3)
        }
    }

    private fun updateCurrentStage(stage: Int) {
        currentStageInt = stage
        val bundle = Bundle().apply {
            putInt("CURRENT_STAGE", currentStageInt) // set CURRENT_STAGE
        }

        val cameraFragment = CameraFragment().apply {
            arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, cameraFragment)
            .commit()
    }

    fun showOverlayText(message: String) {
        statusTextView.text = message
        statusTextView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        finish()
    }
}
