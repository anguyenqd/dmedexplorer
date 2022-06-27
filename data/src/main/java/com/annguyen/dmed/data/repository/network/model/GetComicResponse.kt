package com.annguyen.dmed.data.repository.network.model

data class GetComicResponse(
    val data: Data? = null
)

data class Data(
    val results: List<Comic>? = null
)