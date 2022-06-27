package com.annguyen.dmed.data.repository.network

import com.annguyen.dmed.data.repository.network.model.Comic

interface NetworkDataSource {
    suspend fun getSingleComicById(comicId: Int): Result<Comic?>
    suspend fun getComics(offset: Int): Result<List<Comic>>
}
