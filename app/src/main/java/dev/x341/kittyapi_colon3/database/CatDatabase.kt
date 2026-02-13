package dev.x341.kittyapi_colon3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteCat::class], version = 1, exportSchema = false)
abstract class CatDatabase : RoomDatabase() {
    abstract fun favoriteCatDao(): FavoriteCatDao

    companion object {
        @Volatile
        private var instance: CatDatabase? = null

        fun getDatabase(context: Context): CatDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    CatDatabase::class.java,
                    "cat_database"
                ).build()
                instance = db
                db
            }
        }
    }
}
