package com.annguyen.dmed.data.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RemoteOffsetDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(remoteOffsets: List<RemoteOffset>)

    @Query("SELECT * FROM remoteoffset WHERE id = :id")
    suspend fun remoteOffsetByGifId(id: String): RemoteOffset?

    @Query("DELETE FROM remoteoffset")
    suspend fun clearAll()
}