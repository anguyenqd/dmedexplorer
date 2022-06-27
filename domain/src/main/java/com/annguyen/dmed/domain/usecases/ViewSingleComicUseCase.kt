package com.annguyen.dmed.domain.usecases

import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.repository.Repository
import com.annguyen.dmed.domain.repository.RepositoryResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ViewSingleComicUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(comicId: Int): Flow<Comic> = repository.getSingleComicById(comicId)
}