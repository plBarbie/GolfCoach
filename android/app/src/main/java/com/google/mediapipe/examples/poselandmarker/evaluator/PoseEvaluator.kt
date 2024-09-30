package com.google.mediapipe.examples.poselandmarker.evaluator

import com.google.common.math.DoubleMath.roundToInt
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

class PoseEvaluator {

    companion object {
        // Constants for tolerance levels, adjust as needed
        private const val FEET_SHOULDER_TOLERANCE = 0.1f // Percentage tolerance for feet-shoulder width
        private const val ARM_ANGLE_TOLERANCE = 10.0f // Degree tolerance for arm angles
        private const val SYMMETRY_AXIS_TOLERANCE = 10.0f // Degree tolerance for arm symmetry

        // Function to calculate distance between two landmarks
        private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
        }

        // Function to calculate angle between three points (elbow between shoulder and wrist)
        private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Double {
            val angle = Math.toDegrees(
                atan2((y3 - y2).toDouble(), (x3 - x2).toDouble()) -
                        atan2((y1 - y2).toDouble(), (x1 - x2).toDouble())
            )
            return abs(angle)
        }
    }

    data class FeetPositionResult(
        val isCorrect: Boolean,
        val message: String,
        val score: Int
    )

    data class ArmsStraightResult(
        val isCorrect: Boolean,
        val message: String,
        val score: Int
    )

    // Check if feet are shoulder-width apart
    fun checkFeetShoulderWidth(poseLandmarks: PoseLandmarkerResult): FeetPositionResult {
        val leftShoulder = poseLandmarks.landmarks().get(0)[11]
        val rightShoulder = poseLandmarks.landmarks().get(0)[12]
        val leftFoot = poseLandmarks.landmarks().get(0)[27]
        val rightFoot = poseLandmarks.landmarks().get(0)[28]

        // 计算肩膀宽度和脚之间的距离
        val shoulderWidth = calculateDistance(leftShoulder.x(), leftShoulder.y(), rightShoulder.x(), rightShoulder.y())
        val feetDistance = calculateDistance(leftFoot.x(), leftFoot.y(), rightFoot.x(), rightFoot.y())

        // 定义双脚与肩宽的比例阈值
//        val narrowThreshold = 0.85 * shoulderWidth  // 双脚过窄的阈值
//        val wideThreshold = 1.15 * shoulderWidth    // 双脚过宽的阈值

        // 判断双脚距离与肩宽的关系并返回相应结果
        return when {
//            feetDistance < narrowThreshold -> FeetPositionResult(
            feetDistance < shoulderWidth -> FeetPositionResult(
                isCorrect = false,
                message = "双脚距离过窄，建议调宽。",
                score = ((1- abs(feetDistance-shoulderWidth)/shoulderWidth)*30).roundToInt()
            )
//            feetDistance > wideThreshold -> FeetPositionResult(
            feetDistance > shoulderWidth -> FeetPositionResult(
                isCorrect = false,
                message = "双脚距离过宽，建议调窄。",
                score = ((1- abs(feetDistance-shoulderWidth)/shoulderWidth)*30).roundToInt()
            )
            else -> FeetPositionResult(
                isCorrect = true,
                message = "双脚距离刚好，符合肩宽。",
                score = 30
            )
        }
    }

    // Check if both arm angles are close to 180 degrees
    fun checkArmsStraight(poseLandmarks: PoseLandmarkerResult): ArmsStraightResult {
        val leftShoulder = poseLandmarks.landmarks().get(0)[11]
        val leftElbow = poseLandmarks.landmarks().get(0)[13]
        val leftWrist = poseLandmarks.landmarks().get(0)[15]
        val rightShoulder = poseLandmarks.landmarks().get(0)[12]
        val rightElbow = poseLandmarks.landmarks().get(0)[14]
        val rightWrist = poseLandmarks.landmarks().get(0)[16]

        val leftArmAngle = calculateAngle(
            leftShoulder.x(), leftShoulder.y(),
            leftElbow.x(), leftElbow.y(),
            leftWrist.x(), leftWrist.y()
        )
        val rightArmAngle = calculateAngle(
            rightShoulder.x(), rightShoulder.y(),
            rightElbow.x(), rightElbow.y(),
            rightWrist.x(), rightWrist.y()
        )

        return when{
            abs(leftArmAngle - 180) > ARM_ANGLE_TOLERANCE && abs(rightArmAngle - 180) > ARM_ANGLE_TOLERANCE-> ArmsStraightResult(
                isCorrect = false,
                message = "Arms are not straight.",
                score = (( 2- (abs(leftArmAngle-180)+abs(rightArmAngle-180))/180)*20).roundToInt()
            )
            abs(leftArmAngle - 180) > ARM_ANGLE_TOLERANCE && abs(rightArmAngle - 180) <= ARM_ANGLE_TOLERANCE-> ArmsStraightResult(
                isCorrect = false,
                message = "Left arm is not straight.",
                score = (( 2- (abs(leftArmAngle-180)+abs(rightArmAngle-180))/180)*20).roundToInt()
            )
            (abs(leftArmAngle - 180) <= ARM_ANGLE_TOLERANCE && abs(rightArmAngle - 180) > ARM_ANGLE_TOLERANCE) -> ArmsStraightResult(
                isCorrect = false,
                message = "Right arm is not straight.",
                score = (( 2- (abs(leftArmAngle-180)+abs(rightArmAngle-180))/180)*20).roundToInt()
            )
            else -> ArmsStraightResult(
                isCorrect = true,
                message = "Arms are straight.",
                score = 40
            )
        }
    }

    // Check if the symmetry axis of both arms is perpendicular to the ground
//    fun checkSymmetryAxisVertical(poseLandmarks: PoseLandmarkerResult): Boolean {
//        val leftElbow = poseLandmarks.landmarks().get(0)[13]
//        val rightElbow = poseLandmarks.landmarks().get(0)[14]
//
//        // Symmetry axis: a line between the left and right elbows
//        val symmetryAxisAngle = calculateAngle(
//            0f, 0f, // Simulating a vertical line (0,0) to (1,0) for ground reference
//            leftElbow.x(), leftElbow.y(),
//            rightElbow.x(), rightElbow.y()
//        )
//
//        return abs(symmetryAxisAngle - 90) <= SYMMETRY_AXIS_TOLERANCE
//    }
}
