package com.annguyen.dmed.data.repository.local

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(comic: Comic)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(comics: List<Comic>)

    @Query("SELECT * FROM comic where id=:comicId")
    fun getComicByIdFlow(comicId: String): Flow<List<Comic>>

    @Query("SELECT * FROM comic where id=:comicId")
    fun getComicById(comicId: String): List<Comic>

    @Update
    suspend fun updateComic(comic: Comic)

    @Query("DELETE FROM comic")
    fun clearAll()

    @Transaction
    @Query("SELECT * FROM comic")
    fun allComics(): PagingSource<Int, Comic>
}