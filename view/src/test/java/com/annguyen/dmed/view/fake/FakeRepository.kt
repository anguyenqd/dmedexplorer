package com.annguyen.dmed.view.fake

import androidx.paging.PagingData
import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.mockito.kotlin.mock

class FakeRepository : Repository {

    private var comicToBeReturnedByRefresh: Comic? = null
    // Fake the local data Flow
    private val comicByIdFlow: MutableStateFlow<Comic> = MutableStateFlow(createDummyComicData(0))

    override suspend fun getSingleComicById(comicId: Int) =
        run {
            comicByIdFlow.value = createDummyComicData(comicId)
            comicByIdFlow
        }

    override suspend fun refreshComicById(comicID: Int): Result<Comic> = run {
        val newComicData = comicToBeReturnedByRefresh ?: createDummyComicData(comicID)
        comicByIdFlow.value = newComicData
        Result.success(newComicData)
    }

    override fun getComicsWithPagination(): Flow<PagingData<Comic>> = flow {
        emit(mock())
    }

    fun setComicToBeReturnedForRefreshing(comic: Comic) {
        comicToBeReturnedByRefresh = comic
    }

    private fun createDummyComicData(comicId: Int) = Comic(
        id = comicId,
        title = "",
        description = "",
        imageUrl = "",
        imageUrlExtension = ""
    )
}