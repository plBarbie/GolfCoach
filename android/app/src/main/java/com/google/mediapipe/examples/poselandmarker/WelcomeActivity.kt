package com.google.mediapipe.examples.poselandmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        val startUsingButton = findViewById<Button>(R.id.startUsingButton)
        startUsingButton.setOnClickListener {
            val intent = Intent(this, TeachingActivity::class.java)
            startActivity(intent)
        }
    }
}