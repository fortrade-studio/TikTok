package com.fortrade.tiktok.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Video::class],version = 6,exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun getVideoDao():VideoDao

    companion object{
        var videoDatabase : VideoDatabase? = null

        @Volatile var temp = videoDatabase

        fun getDatabase(context: Context):VideoDatabase{
            return if(temp == null){
                videoDatabase = Room
                    .databaseBuilder(context,VideoDatabase::class.java,"Video-Database")
                    .fallbackToDestructiveMigration()
                    .build()
                temp = videoDatabase
                temp!!
            } else{
                temp!!
            }
        }
    }


}