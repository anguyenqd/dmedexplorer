package com.annguyen.dmed.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingState
import com.annguyen.dmed.domain.model.Comic
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getSingleComicById(comicId: Int): Flow<Comic>
    suspend fun refreshComicById(comicID: Int): Result<Comic>
    fun getComicsWithPagination(): Flow<PagingData<Comic>>
}