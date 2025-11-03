package com.green_solar.gs_app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import java.io.File
import androidx.compose.runtime.saveable.rememberSaveable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    // Avatar local (string con uri) para no tocar tu UiState/ViewModel
    var localAvatar by rememberSaveable { mutableStateOf<String?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) { viewModel.loadMe() }

    // ---------- Helpers ----------
    fun newTempCameraUri(context: Context): Uri {
        val dir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File.createTempFile("avatar_", ".jpg", dir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // ---------- Gallery (Photo Picker) ----------
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) localAvatar = uri.toString()
    }

    // ---------- Camera (TakePicture) ----------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok) localAvatar = cameraUri?.toString()
    }

    // ---------- Camera permission ----------
    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraUri = newTempCameraUri(ctx)
            cameraLauncher.launch(cameraUri)
        } else {
            Toast.makeText(ctx, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Mi perfil") }) }
    ) { padding ->
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
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ------- contenido arriba -------
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val avatarModel = localAvatar
                                ?: state.user!!.imageUrl
                                ?: "https://i.pravatar.cc/150?img=3"

                            AsyncImage(
                                model = avatarModel,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape)
                                    .clickable { showPicker = true } // tocar avatar abre opciones
                            )

                            Text(
                                text = "${state.user!!.firstName} ${state.user!!.lastName}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(text = "@${state.user!!.username}")
                            Text(text = state.user!!.email, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // ------- botones abajo -------
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPicker = true },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Cambiar foto") }

                            Button(
                                onClick = {
                                    viewModel.logout()
                                    onLogout()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Cerrar sesión") }
                        }
                    }
                }
                else -> EmptyView()
            }
        }
    }

    // ---------- Diálogo: elegir Cámara / Galería ----------
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Actualizar foto de perfil") },
            text = { Text("Elige una opción para tu nueva foto:") },
            confirmButton = {
                TextButton(onClick = {
                    showPicker = false
                    val granted = ContextCompat.checkSelfPermission(
                        ctx, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (granted) {
                        cameraUri = newTempCameraUri(ctx)
                        cameraLauncher.launch(cameraUri)
                    } else {
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                    }
                }) { Text("Cámara") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPicker = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) { Text("Galería") }
            }
        )
    }
}

// ---------- Views auxiliares (tus mismas firmas) ----------
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
private fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sin datos")
    }
}
