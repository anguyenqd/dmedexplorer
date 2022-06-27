package com.annguyen.dmed.data.repository.network

import com.annguyen.dmed.data.NETWORK_PAGE_SIZE
import com.annguyen.dmed.data.repository.network.model.Comic
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val apiService: MarvelAPIService
) : NetworkDataSource {
    override suspend fun getSingleComicById(comicId: Int): Result<Comic?> = try {
        val response = apiService.getComicById(comicId = comicId)
        if (response.isSuccessful) {
            Result.success(response.body()?.data?.results?.lastOrNull())
        } else {
            Result.failure(
                Exception(
                    response.errorBody()?.extractErrorMessage()
                        ?: "Getting a single comic from network API has failed but no " +
                        "error has been returned!"
                )
            )
        }
    } catch (ex: IOException) {
        Result.failure(ex)
    } catch (ex: HttpException) {
        Result.failure(ex)
    }

    override suspend fun getComics(offset: Int): Result<List<Comic>> = try {
        val response = apiService.getComics(limit = NETWORK_PAGE_SIZE, offset = offset)
        if (response.isSuccessful) {
            Result.success(response.body()?.data?.results.orEmpty())
        } else {
            Result.failure(
                Exception(
                    response.errorBody()?.extractErrorMessage()
                        ?: "Getting a list of comics from network API has failed but no " +
                        "error has been returned!"
                )
            )
        }
    } catch (ex: IOException) {
        Result.failure(ex)
    } catch (ex: HttpException) {
        Result.failure(ex)
    }

    private fun ResponseBody.extractErrorMessage() = try {
        JSONObject(string())
            .getJSONObject("error")
            .getString("message")
    } catch (ex: JSONException) {
        ex.localizedMessage ?: "Can't extract error message from response!"
    }
}