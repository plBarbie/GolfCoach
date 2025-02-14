package com.google.mediapipe.examples.poselandmarker

import com.google.mediapipe.examples.poselandmarker.evaluator.BackSwingPreEvaluator

object EvaluatorManager {
    val backSwingPreEvaluator: BackSwingPreEvaluator by lazy {
        BackSwingPreEvaluator()
    }
}