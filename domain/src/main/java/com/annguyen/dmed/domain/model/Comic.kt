package com.annguyen.dmed.domain.model

data class Comic(
    val id: Int,
    val title: String,
    val description: String?,
    val imageUrl: String,
    val imageUrlExtension: String,
)