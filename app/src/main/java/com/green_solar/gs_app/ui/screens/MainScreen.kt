package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.navigation.Routes
import com.green_solar.gs_app.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    nav: NavController,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val profileState by profileViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.loadMe()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout() // Acción real de logout
                    }
                ) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
            }
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet {
                        Spacer(Modifier.height(12.dp))
                        NavigationDrawerItem(
                            label = { Text("Configurar cuenta") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                nav.navigate(Routes.Profile)
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Cerrar sesión") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showLogoutDialog = true
                            }
                        )
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Green Solar", color = White) },
                            actions = {
                                val onProfileClick: () -> Unit = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }

                                IconButton(onClick = onProfileClick) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Perfil",
                                        tint = White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Bienvenido a Green Solar",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Card(onClick = { nav.navigate(Routes.Projects) }) {
                            ListItem(
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Outlined.HomeWork,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                },
                                headlineContent = { Text("Mis Proyectos") },
                                supportingContent = { Text("Visualiza y gestiona tus instalaciones solares.") }
                            )
                        }

                        Card(onClick = { nav.navigate(Routes.Monitoring) }) {
                            ListItem(
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Outlined.ShowChart,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                },
                                headlineContent = { Text("Monitoreo y Ahorro") },
                                supportingContent = { Text("Consulta tu producción y ahorro en tiempo real.") }
                            )
                        }
                    }
                }
            }
        }
    }
}