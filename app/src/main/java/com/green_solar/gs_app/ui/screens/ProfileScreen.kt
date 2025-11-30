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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    nav: NavController,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { viewModel.onAvatarChange(it) }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onAvatarChange(it) }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // TODO: Agregar funcion en caso de no tener permisos de camara.
        }
    }

    LaunchedEffect(Unit) { viewModel.loadMe() }

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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (nav.previousBackStackEntry != null) {
                            nav.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    //Diseño de color TopBar pero nos gusto mas el color en negro sencillo
                )
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            contentAlignment = Alignment.TopCenter,
            label = "ProfileContentAnimation"
        ) { targetState ->
            when {
                targetState.isLoading && targetState.user == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
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
                        val avatarModel = user.img_url?.takeIf { it.isNotBlank() }
                            ?: "https://i.pravatar.cc/150?img=3"

                        AsyncImage(
                            model = avatarModel,
                            contentDescription = "Avatar de usuario",
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                                .clickable { showImageSourceDialog = true },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Presiona para cambiar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                        Spacer(Modifier.height(32.dp))

                        OutlinedTextField(
                            value = user.name,
                            onValueChange = {},
                            label = { Text("Nombre") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = user.email,
                            onValueChange = {},
                            label = { Text("Correo") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = "******",
                            onValueChange = {},
                            label = { Text("Contraseña") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                            )
                        )
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
