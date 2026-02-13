package dev.x341.kittyapi_colon3.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import dev.x341.kittyapi_colon3.Routes
import dev.x341.kittyapi_colon3.model.CatImage
import dev.x341.kittyapi_colon3.viewmodel.CatViewModel

@Composable
fun FavouritesScreen(@Suppress("UNUSED_PARAMETER") navController: NavHostController, viewModel: CatViewModel) {
    val favorites = viewModel.favoritesFlow.collectAsState(initial = emptyList()).value
    val showMode = viewModel.showModeFlow.collectAsState(initial = "List").value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Favorites", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites yet!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            if (showMode == "Grid") {
                LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(favorites) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onRemove = {
                                viewModel.removeFromFavorites(
                                    CatImage(
                                        id = favorite.id,
                                        url = favorite.url,
                                        width = favorite.width,
                                        height = favorite.height
                                    )
                                )
                            },
                            onOpenDetails = {
                                viewModel.selectCat(
                                    CatImage(
                                        id = favorite.id,
                                        url = favorite.url,
                                        width = favorite.width,
                                        height = favorite.height,
                                        breeds = favorite.breedName?.let { name ->
                                            listOf(
                                                dev.x341.kittyapi_colon3.model.CatBreed(
                                                    name = name,
                                                    description = favorite.breedDescription ?: "",
                                                    origin = favorite.breedOrigin ?: ""
                                                )
                                            )
                                        }
                                    )
                                )
                                navController.navigate(Routes.Details.route)
                            }
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(favorites) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onRemove = {
                                viewModel.removeFromFavorites(
                                    CatImage(
                                        id = favorite.id,
                                        url = favorite.url,
                                        width = favorite.width,
                                        height = favorite.height
                                    )
                                )
                            },
                            onOpenDetails = {
                                viewModel.selectCat(
                                    CatImage(
                                        id = favorite.id,
                                        url = favorite.url,
                                        width = favorite.width,
                                        height = favorite.height,
                                        breeds = favorite.breedName?.let { name ->
                                            listOf(
                                                dev.x341.kittyapi_colon3.model.CatBreed(
                                                    name = name,
                                                    description = favorite.breedDescription ?: "",
                                                    origin = favorite.breedOrigin ?: ""
                                                )
                                            )
                                        }
                                    )
                                )
                                navController.navigate(Routes.Details.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    favorite: dev.x341.kittyapi_colon3.database.FavoriteCat,
    onRemove: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = favorite.url,
                contentDescription = "Favorite cat",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (favorite.breedName != null) {
                Text("Breed: ${favorite.breedName}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Unknown breed", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRemove, modifier = Modifier.align(Alignment.End)) {
                Text("Remove")
            }
        }
    }
}