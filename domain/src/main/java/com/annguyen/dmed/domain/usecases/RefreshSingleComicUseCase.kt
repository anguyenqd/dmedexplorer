package com.annguyen.dmed.domain.usecases

import com.annguyen.dmed.domain.repository.Repository
import javax.inject.Inject

class RefreshSingleComicUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(comicId: Int) = repository.refreshComicById(comicId)
}