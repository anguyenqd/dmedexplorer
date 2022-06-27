package com.annguyen.dmed.data.repository.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.annguyen.dmed.data.NETWORK_PAGE_SIZE
import com.annguyen.dmed.data.repository.local.Comic
import com.annguyen.dmed.data.repository.local.LocalDataSource
import com.annguyen.dmed.data.repository.local.RemoteOffset
import com.annguyen.dmed.data.toLocal
import logcat.logcat
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ComicServiceMediator(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource
) : RemoteMediator<Int, Comic>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Comic>
    ): MediatorResult {
        logcat { "ServiceMediator loadType $loadType" }
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteOffset = getOffsetForLastItem(state)
                logcat { "ServiceMediator remoteOffset $remoteOffset" }
                val nextOffset = remoteOffset?.nextOffset ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteOffset != null
                )
                logcat { "ServiceMediator nextOffset $nextOffset" }
                nextOffset
            }
        }

        return try {
            // Making network request
            val response = networkDataSource.getComics(
                offset = offset
            )

            val comicsDataFromNetwork = response.getOrNull()

            if (response.isFailure || comicsDataFromNetwork == null) return MediatorResult.Error(
                response.exceptionOrNull()
                    ?: Exception("Fetching comic data from network failed without exception!")
            )

            val endOfPaginationReached =
                comicsDataFromNetwork.isEmpty() || comicsDataFromNetwork.size < NETWORK_PAGE_SIZE
            logcat { "ServiceMediator endOfPaginationReached $endOfPaginationReached" }
            // Handling data in database
            // Make sure all operation is executed or nothing
            localDataSource.atomicRun {
                if (loadType == LoadType.REFRESH) {
                    logcat { "ServiceMediator clearAllComicData" }
                    localDataSource.clearAllComicData()
                }

                val comicsDataLocal = comicsDataFromNetwork.map { it.toLocal() }
                val nextOffset = if (endOfPaginationReached) null else offset + NETWORK_PAGE_SIZE
                logcat { "ServiceMediator nextOffset for new data (offset + NETWORK_PAGE_SIZE) = $nextOffset" }
                val offsets = comicsDataLocal.map { comic ->
                    logcat { "ServiceMediator RemoteOffsets is created id = ${comic.id} - nextOffset = $nextOffset" }
                    RemoteOffset(id = comic.id, nextOffset = nextOffset)
                }

                localDataSource.insertAllRemoteOffset(remoteOffsets = offsets)
                localDataSource.insertAllComics(comics = comicsDataLocal)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (ex: IOException) {
            MediatorResult.Error(ex)
        } catch (ex: HttpException) {
            MediatorResult.Error(ex)
        }
    }


    /**
     * Using the last item in the [PagingState] to identify which is the last offset
     */
    private suspend fun getOffsetForLastItem(state: PagingState<Int, Comic>): RemoteOffset? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { comic ->
            localDataSource.remoteOffsetByGifId(comic.id)
        }
}
