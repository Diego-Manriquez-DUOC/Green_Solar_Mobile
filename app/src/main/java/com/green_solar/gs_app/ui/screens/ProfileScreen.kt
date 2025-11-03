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

    // 1. Estado para controlar la visibilidad del diálogo de selección de imagen
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // 2. Lógica para la cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { viewModel.onAvatarChange(it) }
            }
        }
    )

    // 3. Lógica para la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.onAvatarChange(it) }
        }
    )

    // 4. Lógica para el permiso de la cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permiso concedido, lanzar la cámara
                val uri = createImageUri(context)
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                // TODO: En una app real, mostraríamos un mensaje al usuario explicando por qué el permiso es necesario.
            }
        }
    )

    // Carga los datos del usuario la primera vez que se muestra la pantalla
    LaunchedEffect(Unit) { viewModel.loadMe() }

    // Diálogo para elegir entre cámara y galería
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("Elige una fuente para la imagen") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galería")
                }
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
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ups…", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(state.error!!, maxLines = 3, overflow = TextOverflow.Ellipsis)
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
                            // --- IMAGEN CLICABLE ---
                            AsyncImage(
                                model = user.imageUrl.ifBlank { "https://i.pravatar.cc/150?img=3" },
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape)
                                    .clickable { showImageSourceDialog = true } // <-- Hace la imagen clicable
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = "@${user.username}")
                            Spacer(Modifier.height(4.dp))
                            Text(text = user.email, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Button(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Cerrar sesión") }
                    }
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sin datos de usuario")
                    }
                }
            }
        }
    }
}

/**
 * Crea una URI de archivo temporal para que la cámara guarde la foto.
 * Es crucial que el "authority" coincida con el definido en el AndroidManifest.xml y el file_paths.xml
 */
private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_camera_image_${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // <-- Authority
        file
    )
}
