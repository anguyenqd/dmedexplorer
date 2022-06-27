package com.annguyen.dmed.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comic(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String,
    val imageUrlExtension: String,
)
