package com.annguyen.dmedandroiassignment.fake

import com.annguyen.dmed.data.repository.network.NetworkDataSource
import com.annguyen.dmed.data.repository.network.model.Comic
import com.annguyen.dmed.data.repository.network.model.Image
import javax.inject.Inject

class FakeNetworkDataSource @Inject constructor() : NetworkDataSource {

    override suspend fun getSingleComicById(comicId: Int): Result<Comic?> {
        val result: Comic? = fakeData.find { it.id == comicId }

        return if (result == null) {
            Result.failure(Exception("No Data"))
        } else {
            Result.success(result)
        }
    }

    override suspend fun getComics(offset: Int): Result<List<Comic>> = Result.success(fakeData)

    private val fakeData: List<Comic> = listOf(
        Comic(
            id = 1, title = "Amazing Spider man 1", description = "Description", images = listOf(
                Image(
                    path = "",
                    extension = ""
                )
            )
        ),
        Comic(
            id = 2, title = "Amazing Spider man 2", description = "Description", images = listOf(
                Image(
                    path = "",
                    extension = ""
                )
            )
        ),
        Comic(
            id = 3, title = "Amazing Spider man 3", description = "Description", images = listOf(
                Image(
                    path = "",
                    extension = ""
                )
            )
        )
    )
}