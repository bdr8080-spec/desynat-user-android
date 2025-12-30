package com.desynatuser.ui.home.imageDetector

data class VisionRequestBody(
    val requests: List<AnnotateRequest>
)

data class AnnotateRequest(
    val image: ImageContent,
    val features: List<Feature>
)

data class ImageContent(
    val content: String
)

data class Feature(
    val type: String,
    val maxResults: Int
)

data class VisionResponse(
    val responses: List<AnnotateImageResponse>
)

data class AnnotateImageResponse(
    val labelAnnotations: List<LabelAnnotation>?
)

data class LabelAnnotation(
    val description: String,
    val score: Float
)

