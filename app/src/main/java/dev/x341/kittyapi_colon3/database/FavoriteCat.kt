package dev.x341.kittyapi_colon3.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cats")
data class FavoriteCat(
    @PrimaryKey
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val breedName: String? = null,
    val breedDescription: String? = null,
    val breedOrigin: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)
