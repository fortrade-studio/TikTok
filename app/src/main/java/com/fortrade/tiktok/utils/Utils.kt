package com.fortrade.tiktok.utils

fun getVideoId(link:String):String{
    return link.substringAfter("?","-1")
}