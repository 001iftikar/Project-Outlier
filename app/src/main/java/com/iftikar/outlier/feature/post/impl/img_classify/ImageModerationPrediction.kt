package com.iftikar.outlier.feature.post.impl.img_classify

data class ImageModerationPrediction(
    val label: SafetyLabel,
    val confidence: Float,
    val scores: Map<SafetyLabel, Float>
)