package com.green_solar.gs_app.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.green_solar.gs_app.ui.components.profile.ProfileUiState
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import java.io.File
import java.util.*

/**
 * Muestra la pantalla de perfil del usuario, gestionando la carga de datos,
 * errores, y la interacción para cambiar la foto de perfil y cerrar sesión.
 *
 * @param viewModel El ViewModel que gestiona el estado y la lógica de esta pantalla.
 * @param onLogout La acción a ejecutar cuando el usuario decide cerrar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showImageSourceDialog by remember { mutableStateOf(false) }
    // ✅ 1. Añadido estado para el diálogo de logout
    var showLogoutDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para iniciar la cámara y recibir la foto capturada.
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { viewModel.onAvatarChange(it) }
    }
    // Launcher para abrir la galería y recibir la imagen seleccionada.
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onAvatarChange(it) }
    }
    // Launcher para solicitar el permiso de la cámara. Si se concede, abre la cámara.
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // TODO: En una app real, mostrar un mensaje explicando por qué el permiso es necesario.
        }
    }

    // Carga los datos del usuario una única vez cuando el Composable entra en la composición.
    LaunchedEffect(Unit) { viewModel.loadMe() }

    // Muestra un diálogo de alerta para que el usuario elija entre Cámara o Galería.
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

    // ✅ 2. Añadido el diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout() // Aquí se ejecuta la acción real
                    }
                ) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
            }
        )
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Mi perfil") }) }
    ) { padding ->
        AnimatedContent(
            targetState = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            contentAlignment = Alignment.Center,
            label = "ProfileContentAnimation"
        ) { targetState ->
            when {
                targetState.isLoading && targetState.user == null -> {
                    CircularProgressIndicator()
                }
                targetState.error != null && targetState.user == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ups…", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        targetState.error?.let { err ->
                            Text(err, textAlign = TextAlign.Center, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) { Text("Reintentar") }
                    }
                }
                targetState.user != null -> {
                    val user = targetState.user
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
                            val avatarModel = user.imageUrl.takeIf { it.isNotBlank() }
                                ?: "https://i.pravatar.cc/150?img=3"

                            AsyncImage(
                                model = avatarModel,
                                contentDescription = "Avatar de usuario",
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
                            Text(text = user.email, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // ✅ 3. Modificado el onClick para mostrar el diálogo
                        Button(
                            onClick = { showLogoutDialog = true },
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

private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_camera_image_${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
