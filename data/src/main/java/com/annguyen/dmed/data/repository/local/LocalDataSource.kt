package com.annguyen.dmed.data.repository.local

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getSingleComicById(comicId: String): Flow<Comic>
    suspend fun addComic(comic: Comic)

    /**
     * Make sure the execution of [block] finish successful or nothing
     */
    suspend fun atomicRun(block: suspend () -> Unit)
    suspend fun clearAllComicData()
    suspend fun insertAllRemoteOffset(remoteOffsets: List<RemoteOffset>)
    suspend fun insertAllComics(comics: List<Comic>)
    suspend fun remoteOffsetByGifId(id: String): RemoteOffset?
    fun allComics(): PagingSource<Int, Comic>
}