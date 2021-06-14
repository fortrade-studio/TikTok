package com.fortrade.tiktok.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoDao {


    @Insert
    public fun insertVideo(video: Video)


    @Query("Select * From video where video.VPI=:videoPublicID")
    fun checkIfExists(videoPublicID:String):Video?

    @Query("Select * from video")
    fun getVideos():List<Video>

    @Query("Select * from liked where liked.VPI=:videoPublicID")
    fun getLikeVideos(videoPublicID:String):List<Liked>

    @Insert
    fun insertToLikedVideos(liked: Liked)
}