package com.annguyen.dmed.data.repository.local

import androidx.paging.PagingSource
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val localDatabase: ComicDatabase
) : LocalDataSource {

    override suspend fun getSingleComicById(comicId: String): Flow<Comic> =
        localDatabase.comicDao.getComicByIdFlow(comicId)
            .map { it.lastOrNull() }
            .filterNotNull()

    override suspend fun addComic(comic: Comic) {
        val isExist = localDatabase.comicDao.getComicById(comicId = comic.id).isNotEmpty()
        if (isExist) {
            localDatabase.comicDao.updateComic(comic)
        } else {
            localDatabase.comicDao.insert(comic)
        }
    }

    override suspend fun atomicRun(block: suspend () -> Unit) {
        localDatabase.withTransaction(block)
    }

    override suspend fun clearAllComicData() {
        localDatabase.comicDao.clearAll()
        localDatabase.remoteOffsetDao.clearAll()
    }

    override suspend fun insertAllRemoteOffset(remoteOffsets: List<RemoteOffset>) {
        localDatabase.remoteOffsetDao.insertAll(remoteOffsets)
    }

    override suspend fun insertAllComics(comics: List<Comic>) {
        localDatabase.comicDao.insertAll(comics)
    }

    override suspend fun remoteOffsetByGifId(id: String): RemoteOffset? =
        localDatabase.remoteOffsetDao.remoteOffsetByGifId(id)

    override fun allComics(): PagingSource<Int, Comic> = localDatabase.comicDao.allComics()
}