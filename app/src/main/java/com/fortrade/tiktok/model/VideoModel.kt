package com.leeladher.video

data class VideoModel(
    var videoTitle: String = "", var videoDesc: String = "", var videoUrl: String = "",
    var videoUploaderUsername:String = "",var likes:String = "",var uniqueVideoId:String = ""
)