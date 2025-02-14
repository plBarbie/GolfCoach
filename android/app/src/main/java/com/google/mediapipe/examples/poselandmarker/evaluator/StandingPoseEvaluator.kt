package com.google.mediapipe.examples.poselandmarker.evaluator

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

class StandingPoseEvaluator {

    companion object {
        // Constants for tolerance levels, adjust as needed
        private const val FEET_SHOULDER_TOLERANCE = 0.1f // Percentage tolerance for feet-shoulder width
        private const val ARM_ANGLE_TOLERANCE = 10.0f // Degree tolerance for arm angles
        private const val HAND_POSITION_TOLERANCE = 0.1f // Degree tolerance for arm symmetry

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

    data class HandsPositionResult(
        val isCorrect: Boolean,
        val message: String,
        val score: Int
    )

    // Check if feet are shoulder-width apart
    private fun checkFeetShoulderWidth(poseLandmarks: PoseLandmarkerResult): FeetPositionResult {
        if (poseLandmarks.landmarks().isEmpty()) {
            return FeetPositionResult(
                isCorrect = false,
                message = "I can't see you.",
                score = 0
            )
        }
        val leftShoulder = poseLandmarks.landmarks()[0][11]
        val rightShoulder = poseLandmarks.landmarks()[0][12]
        val leftFoot = poseLandmarks.landmarks()[0][27]
        val rightFoot = poseLandmarks.landmarks()[0][28]

        // 计算肩膀宽度和脚之间的距离
        val shoulderWidth = calculateDistance(leftShoulder.x(), leftShoulder.y(), rightShoulder.x(), rightShoulder.y())
        val feetDistance = calculateDistance(leftFoot.x(), leftFoot.y(), rightFoot.x(), rightFoot.y())

        // 定义双脚与肩宽的比例阈值
//        val narrowThreshold = 0.85 * shoulderWidth  // 双脚过窄的阈值
//        val wideThreshold = 1.15 * shoulderWidth    // 双脚过宽的阈值

        // 判断双脚距离与肩宽的关系并返回相应结果
        return when {
//            feetDistance < narrowThreshold -> FeetPositionResult(
            shoulderWidth-feetDistance > FEET_SHOULDER_TOLERANCE -> FeetPositionResult(
                isCorrect = false,
                message = "Feet too narrow.",
                score = ((1- abs(feetDistance-shoulderWidth)/shoulderWidth)*30).roundToInt()
            )
//            feetDistance > wideThreshold -> FeetPositionResult(
            feetDistance - shoulderWidth >FEET_SHOULDER_TOLERANCE-> FeetPositionResult(
                isCorrect = false,
                message = "Feet too wide apart.",
                score = ((1- abs(feetDistance-shoulderWidth)/shoulderWidth)*30).roundToInt()
            )
            else -> FeetPositionResult(
                isCorrect = true,
                message = "Good job!",
                score = 30
            )
        }
    }

    // Check if both arm angles are close to 180 degrees
    private fun checkArmsStraight(poseLandmarks: PoseLandmarkerResult): ArmsStraightResult {
        if (poseLandmarks.landmarks().isEmpty()) {
            return ArmsStraightResult(
                isCorrect = false,
                message = "",
                score = 0
            )
        }
        val leftShoulder = poseLandmarks.landmarks()[0][11]
        val leftElbow = poseLandmarks.landmarks()[0][13]
        val leftWrist = poseLandmarks.landmarks()[0][15]
        val rightShoulder = poseLandmarks.landmarks()[0][12]
        val rightElbow = poseLandmarks.landmarks()[0][14]
        val rightWrist = poseLandmarks.landmarks()[0][16]

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
                message = "Good job!",
                score = 40
            )
        }
    }

    // Check if the symmetry axis of both arms is perpendicular to the ground
    private fun checkHandsPosition(poseLandmarks: PoseLandmarkerResult): HandsPositionResult {
        if (poseLandmarks.landmarks().isEmpty()) {
            return HandsPositionResult(
                isCorrect = false,
                message = "",
                score = 0
            )
        }
        val leftShoulder = poseLandmarks.landmarks()[0][11]
        val rightShoulder = poseLandmarks.landmarks()[0][12]
        // 计算肩膀的中心点
        val midShoulderX = (leftShoulder.x() + rightShoulder.x()) / 2
        // 获取左右手的坐标
        val leftHand = poseLandmarks.landmarks()[0][15] // 左手腕
        val rightHand = poseLandmarks.landmarks()[0][16] // 右手腕
        // 计算左手和右手相对于肩膀中点的x轴偏移
        val midHandX = (leftHand.x() + rightHand.x()) / 2
        val leftHandOffsetX = abs(leftHand.x() - midShoulderX)
        val rightHandOffsetX = abs(rightHand.x() - midShoulderX)


        // 计算肩膀的宽度
        val shoulderWidth = calculateDistance(leftShoulder.x(), leftShoulder.y(), rightShoulder.x(), rightShoulder.y())

        // 手臂偏差在合理范围内的评分公式
        val leftHandScore = max(0.0f,1 - (leftHandOffsetX / (shoulderWidth)) * 15)
        val rightHandScore = max(0.0f,(1 - (rightHandOffsetX / (shoulderWidth))) * 15)

        return when{
            abs(midHandX-midShoulderX) > HAND_POSITION_TOLERANCE -> HandsPositionResult(
                isCorrect = false,
                message = "Hands are not in the right position.",
                score = (leftHandScore + rightHandScore).toInt()
            )
            else -> HandsPositionResult(
                isCorrect = true,
                message = "Good job!",
                score = 30
            )
        }
    }

    fun returnText(poseLandmarks: PoseLandmarkerResult): String{
        val feetDistanceEvaluationMessage = checkFeetShoulderWidth(poseLandmarks).message
        val feetDistanceEvaluationScore = checkFeetShoulderWidth(poseLandmarks).score
        val armsStraightEvaluationMessage = checkArmsStraight(poseLandmarks).message
        val armsStraightEvaluationScore = checkArmsStraight(poseLandmarks).score
        val handsPositionEvaluationMessage = checkHandsPosition(poseLandmarks).message
        val handsPositionEvaluationScore = checkHandsPosition(poseLandmarks).score
        val scoreTotal = feetDistanceEvaluationScore+armsStraightEvaluationScore+handsPositionEvaluationScore

        return "$feetDistanceEvaluationMessage\n$armsStraightEvaluationMessage\n$handsPositionEvaluationMessage\nYour score: $scoreTotal"
    }
}
