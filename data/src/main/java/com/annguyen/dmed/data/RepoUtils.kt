package com.annguyen.dmed.data

import com.annguyen.dmed.domain.model.Comic

internal const val NETWORK_PAGE_SIZE = 30

fun com.annguyen.dmed.data.repository.local.Comic.toDomain() = Comic(
    id.toInt(), title, description, imageUrl, imageUrlExtension
)

fun com.annguyen.dmed.data.repository.network.model.Comic.toLocal() =
    com.annguyen.dmed.data.repository.local.Comic(
        id = "$id",
        title = title,
        description = description,
        imageUrl = getImageUrl(),
        imageUrlExtension = getImageUrlExtension()
    )

fun com.annguyen.dmed.data.repository.network.model.Comic.toDomain() =
    Comic(
        id = id,
        title = title,
        description = description,
        imageUrl = getImageUrl(),
        imageUrlExtension = getImageUrlExtension()
    )

fun com.annguyen.dmed.data.repository.network.model.Comic.getImageUrl() =
    images.lastOrNull()?.path ?: ""

fun com.annguyen.dmed.data.repository.network.model.Comic.getImageUrlExtension() =
    images.lastOrNull()?.extension ?: ""