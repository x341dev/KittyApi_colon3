package dev.x341.kittyapi_colon3.network

import dev.x341.kittyapi_colon3.model.CatImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {
    @GET("images/search")
    suspend fun getRandomCats(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 1
    ): List<CatImage>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.thecatapi.com/v1/"

    val service: CatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatApiService::class.java)
    }
}