package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadMe() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi perfil") }
            )
        }
    ) { padding ->

        // Contenedor principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(
                    message = state.error ?: "Error",
                    onRetry = { viewModel.retry() }
                )
                state.user != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ContentView(user = state.user!!)

                        // 👇 Aquí el botón de logout, dentro del mismo flujo de UI
                        Button(
                            onClick = {
                                // Opción A: desde ViewModel (si tiene repo con session)
                                viewModel.logout()

                                // Opción B: directo, más simple
                                // SessionManager(ctx).clear()

                                onLogout()
                            }
                        ) {
                            Text("Cerrar sesión")
                        }
                    }
                }
                else -> EmptyView()
            }
        }
    }
}



@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ups…", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(message, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun ContentView(user: User) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarImage(url = user.imageUrl)

        Text(
            text = "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium)
        Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AvatarImage(url: String?) {
    AsyncImage(
        model = url ?: "https://i.pravatar.cc/150?img=3",
        contentDescription = "Avatar",
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sin datos")
    }
}
