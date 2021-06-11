package com.fortrade.tiktok.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Video (
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "VPI")val videoPublicId : String?=null
)