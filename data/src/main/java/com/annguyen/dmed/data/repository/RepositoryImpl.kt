package com.annguyen.dmed.data.repository

import androidx.paging.*
import com.annguyen.dmed.data.NETWORK_PAGE_SIZE
import com.annguyen.dmed.data.getImageUrl
import com.annguyen.dmed.data.getImageUrlExtension
import com.annguyen.dmed.data.repository.local.LocalDataSource
import com.annguyen.dmed.data.repository.network.ComicServiceMediator
import com.annguyen.dmed.data.repository.network.NetworkDataSource
import com.annguyen.dmed.data.toDomain
import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.repository.Repository
import com.annguyen.dmed.domain.repository.RepositoryResponse
import com.annguyen.dmed.domain.repository.ResponseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : Repository {
    override suspend fun getSingleComicById(comicId: Int): Flow<Comic> =
        localDataSource.getSingleComicById("$comicId").map { it.toDomain() }

    override suspend fun refreshComicById(comicID: Int): Result<Comic> {
        val networkResponse = networkDataSource.getSingleComicById(comicID)
        val comicNetworkResponse = networkResponse.getOrNull() ?: return Result.failure(
            networkResponse.exceptionOrNull() ?: Exception(
                "Getting single comic failed without exception!"
            )
        )

        localDataSource.addComic(comicNetworkResponse.toLocal())
        return Result.success(comicNetworkResponse.toDomain())
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getComicsWithPagination(): Flow<PagingData<Comic>> = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        remoteMediator = ComicServiceMediator(networkDataSource, localDataSource),
        pagingSourceFactory = { localDataSource.allComics() }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    private fun com.annguyen.dmed.data.repository.network.model.Comic.toLocal() =
        com.annguyen.dmed.data.repository.local.Comic(
            id = "$id",
            title = title,
            description = description,
            imageUrl = getImageUrl(),
            imageUrlExtension = getImageUrlExtension()
        )
}




