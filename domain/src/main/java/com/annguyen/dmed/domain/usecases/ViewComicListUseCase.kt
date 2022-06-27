package com.annguyen.dmed.domain.usecases

import androidx.paging.PagingData
import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ViewComicListUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<PagingData<Comic>> = repository.getComicsWithPagination()
}