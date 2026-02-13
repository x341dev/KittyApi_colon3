package dev.x341.kittyapi_colon3.network

import dev.x341.kittyapi_colon3.BuildConfig
import dev.x341.kittyapi_colon3.model.CatImage

class CatRepository {
    private val api = RetrofitClient.service

    suspend fun getRandomCats(limit: Int): Result<List<CatImage>> {
        return try {
            val response = api.getRandomCats(BuildConfig.CAT_API_KEY, limit)
            if (response.isNotEmpty()) {
                Result.success(response)
            } else {
                Result.failure(Exception("No cat images found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}