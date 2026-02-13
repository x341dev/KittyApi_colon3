package dev.x341.kittyapi_colon3.model

data class CatImage(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val breeds: List<CatBreed>? = null
)

data class CatBreed(
    val name: String,
    val description: String,
    val origin: String
)