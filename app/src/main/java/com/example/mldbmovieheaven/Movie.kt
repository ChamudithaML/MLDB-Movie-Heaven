package com.example.mldbmovieheaven

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie(
    @PrimaryKey val id: String,
    val title: String?,
    val year: String?,
    val rated: String?,
    val released:String?,
    val runtime:String?,
    val genre:String?,
    val director:String,
    val writer:String?,
    val actors:String?,
    val plot:String?,
)