package dev.x341.kittyapi_colon3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.x341.kittyapi_colon3.model.CatImage
import dev.x341.kittyapi_colon3.viewmodel.CatUiState
import dev.x341.kittyapi_colon3.viewmodel.CatViewModel

@Composable
fun CatDetailsScreen(viewModel: CatViewModel) {
    val state by viewModel.uiState.collectAsState()
    val selectedCat by viewModel.selectedCat.collectAsState()
    val allowUnnamed by viewModel.showUnnamedCatsFlow.collectAsState(initial = false)
    var isFavorite by remember { mutableStateOf(false) }

    val currentCat: CatImage? = selectedCat ?: when (state) {
        is CatUiState.Success -> (state as CatUiState.Success).cats.firstOrNull()
        else -> null
    }

    LaunchedEffect(currentCat) {
        currentCat?.let { cat ->
            isFavorite = viewModel.isFavorite(cat.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is CatUiState.Loading -> CircularProgressIndicator()
            is CatUiState.Error -> {
                Text("Error", color = Color.Red)
                Button(onClick = { viewModel.fetchCats() }) { Text("Retry") }
            }
            is CatUiState.Success -> {
                val cat = currentCat ?: return@Column

                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = cat.url,
                        contentDescription = "Cat",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            if (isFavorite) viewModel.addToFavorites(cat) else viewModel.removeFromFavorites(cat)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val title = cat.breeds?.firstOrNull()?.name?.takeIf { it.isNotBlank() }
                    ?: if (allowUnnamed) "Unnamed cat" else "Cat"
                Text(title, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "ID: ${cat.id}", style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(16.dp))

                if (!cat.breeds.isNullOrEmpty()) {
                    Text(
                        text = cat.breeds[0].description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (allowUnnamed) {
                    Text(
                        text = "No breed info available",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}