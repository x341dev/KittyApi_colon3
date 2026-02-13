package dev.x341.kittyapi_colon3.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.x341.kittyapi_colon3.database.CatDatabase
import dev.x341.kittyapi_colon3.database.FavoriteCat
import dev.x341.kittyapi_colon3.model.CatImage
import dev.x341.kittyapi_colon3.network.CatRepository
import dev.x341.kittyapi_colon3.preferences.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CatUiState {
    object Loading : CatUiState()
    data class Success(val cats: List<CatImage>) : CatUiState()
    data class Error(val message: String) : CatUiState()
}

class CatViewModel(private val context: Context) : ViewModel() {
    private val repository = CatRepository()
    private val database = CatDatabase.getDatabase(context)
    private val favoriteCatDao = database.favoriteCatDao()
    private val settingsPreferences = SettingsPreferences(context)

    private val _uiState = MutableStateFlow<CatUiState>(CatUiState.Loading)
    val uiState: StateFlow<CatUiState> = _uiState

    val darkModeFlow: Flow<Boolean> = settingsPreferences.darkModeFlow
    val showModeFlow: Flow<String> = settingsPreferences.showModeFlow
    val favoritesFlow: Flow<List<FavoriteCat>> = favoriteCatDao.getAllFavorites()
    private val _selectedCat = MutableStateFlow<CatImage?>(null)
    val selectedCat: StateFlow<CatImage?> = _selectedCat

    init {
        fetchCats()
    }

    fun selectCat(cat: CatImage) {
        _selectedCat.value = cat
    }

    fun fetchCats(limit: Int = 10) {
        viewModelScope.launch {
            _uiState.value = CatUiState.Loading
            val result = repository.getRandomCats(limit)
            result.onSuccess { cats ->
                _uiState.value = CatUiState.Success(cats)
                if (_selectedCat.value == null && cats.isNotEmpty()) {
                    _selectedCat.value = cats.first()
                }
            }.onFailure { error ->
                _uiState.value = CatUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun refreshCats() = fetchCats()

    fun addToFavorites(catImage: CatImage) {
        viewModelScope.launch {
            favoriteCatDao.addFavorite(
                FavoriteCat(
                    id = catImage.id,
                    url = catImage.url,
                    width = catImage.width,
                    height = catImage.height,
                    breedName = catImage.breeds?.firstOrNull()?.name,
                    breedDescription = catImage.breeds?.firstOrNull()?.description,
                    breedOrigin = catImage.breeds?.firstOrNull()?.origin
                )
            )
        }
    }

    fun removeFromFavorites(catImage: CatImage) {
        viewModelScope.launch {
            favoriteCatDao.removeFavorite(
                FavoriteCat(
                    id = catImage.id,
                    url = catImage.url,
                    width = catImage.width,
                    height = catImage.height,
                    breedName = catImage.breeds?.firstOrNull()?.name,
                    breedDescription = catImage.breeds?.firstOrNull()?.description,
                    breedOrigin = catImage.breeds?.firstOrNull()?.origin
                )
            )
            if (_selectedCat.value?.id == catImage.id) {
                _selectedCat.value = null
            }
        }
    }

    suspend fun isFavorite(catId: String): Boolean = favoriteCatDao.getFavoriteById(catId) != null

    fun deleteAllFavorites() {
        viewModelScope.launch { favoriteCatDao.deleteAllFavorites() }
    }

    suspend fun setDarkMode(isDark: Boolean) { settingsPreferences.setDarkMode(isDark) }

    suspend fun setShowMode(mode: String) { settingsPreferences.setShowMode(mode) }
}