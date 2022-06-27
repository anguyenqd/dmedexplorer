package com.annguyen.dmed.data.repository.network.model

data class Comic(
    val id: Int,
    val title: String,
    val description: String?,
    val images: List<Image>
)

data class Image(val path: String, val extension: String)