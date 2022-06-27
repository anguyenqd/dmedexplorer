package com.annguyen.dmed.data.repository.network

import com.annguyen.dmed.data.repository.network.model.GetComicResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelAPIService {

    @GET("v1/public/comics/{comicId}")
    suspend fun getComicById(
        @Path("comicId") comicId: Int,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("ts") timeStamp: String = TIME_STAMP,
        @Query("hash") hash: String = API_HASH
    ): Response<GetComicResponse>

    @GET("v1/public/comics")
    suspend fun getComics(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("ts") timeStamp: String = TIME_STAMP,
        @Query("hash") hash: String = API_HASH,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("format") format: String = "comic", // TODO add filter to UI
        @Query("formatType") formatType: String = "comic", // TODO add filter to UI
        @Query("orderBy") orderBy: String = "-focDate", // TODO add filter to UI
    ): Response<GetComicResponse>

    companion object {
        private const val API_KEY = "e327d59ead68c14ffbf8d3d7e51990e7"
        private const val TIME_STAMP = "1"
        private const val API_HASH = "ab9ef2872c97a3129fe4c458714e02f8"

        fun build(): MarvelAPIService = Retrofit.Builder()
            .baseUrl("https://gateway.marvel.com/")
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MarvelAPIService::class.java)
    }
}