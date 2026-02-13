package dev.x341.kittyapi_colon3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.x341.kittyapi_colon3.viewmodel.CatViewModel
import dev.x341.kittyapi_colon3.viewmodel.CatViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(@Suppress("UNUSED_PARAMETER") navController: NavHostController, viewModel: CatViewModel) {
    var isDarkMode by remember { mutableStateOf(false) }
    var showMode by remember { mutableStateOf("List") }
    var showUnnamed by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Load settings from DataStore
    LaunchedEffect(Unit) {
        viewModel.darkModeFlow.collect { isDarkMode = it }
    }

    LaunchedEffect(Unit) {
        viewModel.showModeFlow.collect { showMode = it }
    }

    LaunchedEffect(Unit) {
        viewModel.showUnnamedCatsFlow.collect { showUnnamed = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { newValue ->
                    isDarkMode = newValue
                    coroutineScope.launch {
                        viewModel.setDarkMode(newValue)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show Mode", style = MaterialTheme.typography.titleMedium)
            Box {
                Button(onClick = { expanded = true }) { Text(showMode) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("List") },
                        onClick = {
                            showMode = "List"
                            expanded = false
                            coroutineScope.launch {
                                viewModel.setShowMode("List")
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Grid") },
                        onClick = {
                            showMode = "Grid"
                            expanded = false
                            coroutineScope.launch {
                                viewModel.setShowMode("Grid")
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show unnamed cats", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = showUnnamed,
                onCheckedChange = { newValue ->
                    showUnnamed = newValue
                    coroutineScope.launch { viewModel.setShowUnnamedCats(newValue) }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Delete favs", color = Color.White)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm deletion") },
            text = { Text("Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllFavorites()
                    showDeleteDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("No") } }
        )
    }
}