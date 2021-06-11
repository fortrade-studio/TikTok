package com.fortrade.tiktok.room

class VideoRepository(val videoDao: VideoDao) {

    fun insertToDatabase(video: Video){
        videoDao.insertVideo(video)
    }
    fun insertToDatabase(videoPublicId :String){
        videoDao.insertVideo(Video(0,videoPublicId))
    }

    // true returns means exists
    fun checkIfExists(videoPublicId :String):Boolean{
        return videoDao.checkIfExists(videoPublicId) != null
    }

    fun getVideos()= videoDao.getVideos()

    fun getLikedVideo(videoPublicID:String)  = videoDao.getLikeVideos(videoPublicID)

    fun insertLikedVideo(videoPublicId: String) {
        if (videoDao.getLikeVideos(videoPublicId).isEmpty()) {
            videoDao.insertToLikedVideos(Liked(0,videoPublicId))
        }
    }
}