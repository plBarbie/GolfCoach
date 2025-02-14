package com.google.mediapipe.examples.poselandmarker.evaluator

import android.os.Handler
import android.os.Looper
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.atan2


class BackSwingPreEvaluator {

    companion object {
        private const val ARM_ANGLE_TOLERANCE = 10.0f // Degree tolerance for arm angles

        private fun calculateAngle(
            x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float
        ): Double {
            val angle = Math.toDegrees(
                atan2((y3 - y2).toDouble(), (x3 - x2).toDouble()) -
                        atan2((y1 - y2).toDouble(), (x1 - x2).toDouble())
            )
            return abs(angle)
        }
    }

    var onPoseCorrectListener: (() -> Unit)? = null  // 定义回调
    private var hasTriggered = false
    private var wristChinDiff: Float? = null
    private var hasCheckedReady = false // 记录是否已经满足条件
    private var maxDiff = 0f
    private var backSwingStarted = false

    private fun checkPose(poseLandmarks: PoseLandmarkerResult): Boolean {
        if (poseLandmarks.landmarks().isEmpty()) {
            return false
        }
        val leftShoulder = poseLandmarks.landmarks()[0][12]
        val leftElbow = poseLandmarks.landmarks()[0][14]
        val leftWrist = poseLandmarks.landmarks()[0][16]
        val chin = poseLandmarks.landmarks()[0][10]         // 下巴

        val leftArmAngle = calculateAngle(
            leftShoulder.x(), leftShoulder.y(),
            leftElbow.x(), leftElbow.y(),
            leftWrist.x(), leftWrist.y()
        )

        // 直高true
        val isCorrectPose = when {
            abs(leftArmAngle - 180) > ARM_ANGLE_TOLERANCE && leftWrist.y() < chin.y() -> false //弯高
            abs(leftArmAngle - 180) > ARM_ANGLE_TOLERANCE && leftWrist.y() > chin.y() -> false //弯低
            abs(leftArmAngle - 180) < ARM_ANGLE_TOLERANCE && leftWrist.y() > chin.y() -> false //直低
            else -> true
        }

        if (isCorrectPose && !hasTriggered) {
            hasTriggered = true  // **标记已经触发**
            println("对了Pose is correct, invoking listener.")
            if (onPoseCorrectListener != null) {
                onPoseCorrectListener?.invoke()
                println("发了")
                Handler(Looper.getMainLooper()).postDelayed({
                    // 这里写要执行的代码
                    val diff = getDiff(poseLandmarks)
                    if (diff != null) {
                        maxDiff = maxOf(maxDiff, diff) // 只保留最大值
                    }
                }, 1000) // 延迟 1000 毫秒 (1秒)
                Handler(Looper.getMainLooper()).postDelayed({
                    // 这里写要执行的代码
                    val diff = getDiff(poseLandmarks)
                    if (diff != null) {
                        maxDiff = maxOf(maxDiff, diff) // 只保留最大值
                    }
                }, 1000) // 延迟 1000 毫秒 (1秒)
                Handler(Looper.getMainLooper()).postDelayed({
                    // 这里写要执行的代码
                    val diff = getDiff(poseLandmarks)
                    if (diff != null) {
                        maxDiff = maxOf(maxDiff, diff) // 只保留最大值
                    }
                }, 1000) // 延迟 1000 毫秒 (1秒)
            } else {
                println("值为空ERROR: Listener is NULL when invoked!")
            }
        }

        return isCorrectPose
    }

    fun resetCheck() {
        hasTriggered = false  // **倒计时结束后，重置检测状态**
    }

    private fun getDiff(poseLandmarks: PoseLandmarkerResult): Float? {
        val landmarks = poseLandmarks.landmarks()[0]
        val leftWrist = landmarks[16]   // 左手腕
        val chin = landmarks[10]         // 下巴

        wristChinDiff = chin.y() - leftWrist.y()
        return wristChinDiff
    }

    private fun checkReady(poseLandmarks: PoseLandmarkerResult): Boolean {
        if (!hasCheckedReady && checkPose(poseLandmarks) && hasTriggered) {
            hasCheckedReady = true  // 一旦满足条件，就标记为 true
        }
        return hasCheckedReady
    }

    private fun evaluateBackSwingPre(poseLandmarks: PoseLandmarkerResult): BackSwingPreResult {
        if (poseLandmarks.landmarks().isEmpty()) {
            return BackSwingPreResult(false, "I can't see you.", 0)
        }
        return if (checkReady(poseLandmarks)) {
            val landmarks = poseLandmarks.landmarks()[0]
            val leftWrist = landmarks[15]   // 左手腕
            val leftShoulder = landmarks[11] // 左肩

            if (!backSwingStarted && leftWrist.y() < leftShoulder.y()) {
                backSwingStarted = true
            }

            BackSwingPreResult(false, "Next step please. $maxDiff", 50)
        } else {
            BackSwingPreResult(
                false,
                "Please raise your left hand straight up to the right and press it against your chin.",
                0
            )
        }
    }

    fun returnText(poseLandmarks: PoseLandmarkerResult): String {
        val result = evaluateBackSwingPre(poseLandmarks)
        return "${result.message}\nYour score: ${result.score}"
    }

    data class BackSwingPreResult(
        val isCorrect: Boolean,
        val message: String,
        val score: Int
    )
}