package com.google.mediapipe.examples.poselandmarker

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
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
//    private lateinit var backSwingEvaluator: BackSwingEvaluator
    private val backSwingEvaluator = EvaluatorManager.backSwingPreEvaluator

    private var currentStageInt: Int = 1

    private lateinit var overlayFrame: FrameLayout
    private lateinit var countdownTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teaching)

        statusTextView = findViewById(R.id.realtime_text)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
// 设置回调监听，当 checkPose 返回 true 时触发 startCountdown()
        backSwingEvaluator.onPoseCorrectListener = {
            println("收到了Listener triggered, starting countdown!")
            startCountdown()
        }
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
        // 动态创建蒙版和倒计时TextView
        createOverlay()

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

    private fun createOverlay() {
        // 创建蒙版
        overlayFrame = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#80000000")) // 半透明黑色背景
            visibility = View.GONE // 初始时蒙版隐藏
        }

        // 创建倒计时 TextView
        countdownTextView = TextView(this).apply {
            text = "3"  // 初始显示 3
            textSize = 150f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER  // 设置为居中显示
        }

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                // 让 TextView 居中
                gravity = Gravity.CENTER
            }
            countdownTextView.layoutParams = layoutParams
                    overlayFrame.addView(countdownTextView)

            // 将蒙版添加到当前的Activity布局中
            (findViewById<ViewGroup>(android.R.id.content)).addView(overlayFrame)

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
        // 触发蒙版倒计时
    }

    private fun startCountdown() {
        // 显示蒙版
        overlayFrame.visibility = View.VISIBLE

        // 设置初始数字
        var countdown = 3
        countdownTextView.text = countdown.toString()

        // 启动倒计时
        val handler = Handler(mainLooper)
        handler.postDelayed(object : Runnable {
            override fun run() {
                countdown--
                countdownTextView.text = countdown.toString()

                if (countdown > 0) {
                    handler.postDelayed(this, 1000)
                } else {
                    // 隐藏蒙版
                    overlayFrame.visibility = View.GONE
                    backSwingEvaluator.resetCheck()
                }
            }
        }, 1000) // 每秒更新一次
    }


    fun showOverlayText(message: String) {
        statusTextView.text = message
        statusTextView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        finish()
    }
}
