package com.annguyen.dmed.data.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteOffset(
    @PrimaryKey
    val id: String,
    val nextOffset: Int?
)