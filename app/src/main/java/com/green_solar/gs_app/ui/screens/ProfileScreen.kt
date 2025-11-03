package com.green_solar.gs_app.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Diálogo para elegir cámara/galería
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // AQUI SOLO DISPARAS LÓGICA DEL VM SI QUIERES SUBIRLA AL BACKEND.
            // En modo “antiguo”, la UI no cambia hasta que imageUrl remoto cambie.
            tempCameraUri?.let { viewModel.onAvatarChange(it) }
        }
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Igual que arriba: esto no se verá en UI si no actualizas imageUrl remoto.
        uri?.let { viewModel.onAvatarChange(it) }
    }

    // Permiso cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // podrías mostrar un rationale/snackbar
        }
    }

    LaunchedEffect(Unit) { viewModel.loadMe() }

    // Diálogo cámara/galería
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("Elige una fuente para la imagen") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Cámara") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galería") }
            }
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Mi perfil") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading && state.user == null -> {
                    CircularProgressIndicator()
                }

                state.error != null && state.user == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ups…", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        state.error?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) { Text("Reintentar") }
                    }
                }

                state.user != null -> {
                    val user = state.user!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 🔹 MODO ANTIGUO: solo URL remota o placeholder
                            val avatarModel: String = user.imageUrl.takeIf { it.isNotBlank() }
                                ?: "https://i.pravatar.cc/150?img=3"

                            AsyncImage(
                                model = avatarModel,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape)
                                    .clickable { showImageSourceDialog = true }
                            )

                            Spacer(Modifier.height(24.dp))

                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = "@${user.username}")
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = user.email,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Cerrar sesión") }
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text("Sin datos de usuario")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.retry() }) { Text("Reintentar") }
                    }
                }
            }
        }
    }
}

// FileProvider con autoridad ".fileprovider" (debe coincidir con tu Manifest)
private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_camera_image_${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
