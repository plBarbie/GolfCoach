package com.google.mediapipe.examples.poselandmarker.evaluator

class BackSwingEvaluator {
    /**
     * 评估 Backswing
     */
//    fun evaluateBackSwing(poseLandmarks: PoseLandmarkerResult): BackSwingResult {
//        if (poseLandmarks.landmarks().isEmpty()) {
//            return BackSwingResult(false, "I can't see you.", 0)
//        }
//
//        val landmarks = poseLandmarks.landmarks()[0]
//        val leftWrist = landmarks[15]   // 左手腕
//        val chin = landmarks[9]         // 下巴
//        val leftShoulder = landmarks[11] // 左肩
//        val rightWrist = landmarks[16]  // 右手腕
//        val rightShoulder = landmarks[12] // 右肩
//        val rightKnee = landmarks[26] // 右膝
//        val leftKnee = landmarks[25] // 左膝
//
//        if (checkPose(poseLandmarks)) {
//            return BackSwingResult(false, "倒计时结束，进入下一步！", 50)
//        } else {
//            return BackSwingResult(false, "请做准备动作", 50)
//        }
//
//
//
//
//        // Backswing 开始
//        if (!backSwingStarted && leftWrist.y() < leftShoulder.y()) {
//            backSwingStarted = true
//        }
//
//        // Backswing 最高点检测
//        if (backSwingStarted) {
//            val currentHandDiff = abs(leftWrist.y() - chin.y())
//            val hasReachedMaxPoint = avgDiff != null && abs(currentHandDiff - avgDiff!!) < ARM_Y_STABILITY_THRESHOLD
//
//            if (!hasReachedMaxPoint) {
//                return BackSwingResult(false, "Raise your left hand to the highest point!", 50)
//            }
//        }
//
//        // Backswing 结束
//        if (backSwingStarted && leftWrist.y() > leftShoulder.y()) {
//            backSwingStarted = false
//            avgDiff = null
//            isReadyForBackswing = false  // 检测完成后重置准备状态
//
//            val isLeftKneeStable = abs(leftKnee.x() - leftKnee.x()) < KNEE_X_THRESHOLD
//            val isRightKneeStable = abs(rightKnee.y() - rightKnee.y()) < KNEE_Y_THRESHOLD
//
//            if (!isLeftKneeStable) {
//                return BackSwingResult(false, "Don't move your left knee too much!", 50)
//            }
//            if (!isRightKneeStable) {
//                return BackSwingResult(false, "Keep your right knee more stable!", 50)
//            }
//
//            return BackSwingResult(true, "Great backswing!", 100)
//        }
//
//        return BackSwingResult(false, "Keep going!", 0)
//    }

}