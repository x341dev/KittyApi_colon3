package dev.x341.kittyapi_colon3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(cat: FavoriteCat)

    @Delete
    suspend fun removeFavorite(cat: FavoriteCat)

    @Query("SELECT * FROM favorite_cats ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteCat>>

    @Query("SELECT * FROM favorite_cats WHERE id = :catId")
    suspend fun getFavoriteById(catId: String): FavoriteCat?

    @Query("DELETE FROM favorite_cats")
    suspend fun deleteAllFavorites()

    @Query("SELECT COUNT(*) FROM favorite_cats")
    fun getFavoritesCount(): Flow<Int>
}
