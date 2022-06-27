package com.annguyen.dmed.domain.repository

data class RepositoryResponse<T>(
    val result: Result<T>,
    val type: ResponseType
)

sealed class ResponseType {
    object Local : ResponseType()
    object Network : ResponseType()
}