package com.google.ar.sceneform.samples.gltf.model

data class ToolItem(
    val name: String,
    val description: String,
    val mediaList: List<MediaItem> // Images or videos
)

data class MediaItem(
    val type: MediaType, // IMAGE or VIDEO
    val resourceId: Int
)

enum class MediaType {
    IMAGE,
    VIDEO
}
