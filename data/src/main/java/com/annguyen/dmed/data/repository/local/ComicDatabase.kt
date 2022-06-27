package com.annguyen.dmed.data.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Comic::class, RemoteOffset::class],
    version = 1,
    exportSchema = false
)
abstract class ComicDatabase : RoomDatabase() {
    abstract val comicDao: ComicDao
    abstract val remoteOffsetDao: RemoteOffsetDao

    companion object {
        fun create(applicationContext: Context) = Room.databaseBuilder(
            applicationContext,
            ComicDatabase::class.java,
            "comic.db"
        ).build()
    }
}