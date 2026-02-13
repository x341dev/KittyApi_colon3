package dev.x341.kittyapi_colon3.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import dev.x341.kittyapi_colon3.database.FavoriteCat
import dev.x341.kittyapi_colon3.model.CatImage
import dev.x341.kittyapi_colon3.viewmodel.CatUiState
import dev.x341.kittyapi_colon3.viewmodel.CatViewModel
import dev.x341.kittyapi_colon3.viewmodel.CatViewModelFactory
import dev.x341.kittyapi_colon3.Routes

@Composable
fun ListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: CatViewModel = viewModel(factory = CatViewModelFactory(context))

    val uiState by viewModel.uiState.collectAsState()
    val showMode by viewModel.showModeFlow.collectAsState(initial = "List")
    val favorites by viewModel.favoritesFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Random Cats", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { viewModel.refreshCats() }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reload")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is CatUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is CatUiState.Error -> {
                val message = (uiState as CatUiState.Error).message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $message", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.refreshCats() }) { Text("Retry") }
                    }
                }
            }

            is CatUiState.Success -> {
                val cats = (uiState as CatUiState.Success).cats
                if (cats.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("No cats found", style = MaterialTheme.typography.bodyLarge) }
                } else {
                    if (showMode == "Grid") {
                        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                            items(cats) { cat ->
                                CatCard(
                                    cat = cat,
                                    isFavorite = favorites.any { it.id == cat.id },
                                    onToggleFavorite = { toggleFavorite(viewModel, cat, favorites) },
                                    onOpenDetails = {
                                        viewModel.selectCat(cat)
                                        navController.navigate(Routes.Details.route)
                                    }
                                )
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                            items(cats) { cat ->
                                CatCard(
                                    cat = cat,
                                    isFavorite = favorites.any { it.id == cat.id },
                                    onToggleFavorite = { toggleFavorite(viewModel, cat, favorites) },
                                    onOpenDetails = {
                                        viewModel.selectCat(cat)
                                        navController.navigate(Routes.Details.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatCard(cat: CatImage, isFavorite: Boolean, onToggleFavorite: () -> Unit, onOpenDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = cat.url,
                contentDescription = "Cat",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(cat.breeds?.firstOrNull()?.name ?: "Unknown breed", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(onClick = onToggleFavorite) {
                    Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isFavorite) "Remove" else "Favorite")
                }
            }
        }
    }
}

private fun toggleFavorite(viewModel: CatViewModel, cat: CatImage, favorites: List<FavoriteCat>) {
    if (favorites.any { it.id == cat.id }) {
        viewModel.removeFromFavorites(cat)
    } else {
        viewModel.addToFavorites(cat)
    }
}