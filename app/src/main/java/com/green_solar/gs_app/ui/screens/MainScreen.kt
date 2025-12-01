package com.green_solar.gs_app.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.green_solar.gs_app.ui.components.profile.ProfileViewModel
import com.green_solar.gs_app.ui.navigation.Routes
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
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadMe()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Sí") }
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
                            onClick = { scope.launch { drawerState.close() }; nav.navigate(Routes.Profile) }
                        )
                        NavigationDrawerItem(
                            label = { Text("Cerrar sesión") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() }; showLogoutDialog = true }
                        )
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Green Solar") },
                            actions = {
                                IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                                    Icon(Icons.Default.Person, contentDescription = "Perfil")
                                }
                            }
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
                        profileState.user?.let {
                            Text(
                                text = "Bienvenido, ${it.name}",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        val userRole = profileState.user?.role

                        if (userRole == "USER") {
                            // --- Tarjetas para el rol CLIENTE ---
                            Card(onClick = { nav.navigate(Routes.Quote) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.RequestQuote, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Nueva Cotización") },
                                    supportingContent = { Text("Explora productos y crea una nueva cotización.") }
                                )
                            }
                            Card(onClick = { nav.navigate(Routes.Projects) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.HomeWork, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Mis Cotizaciones") },
                                    supportingContent = { Text("Visualiza tus cotizaciones guardadas.") }
                                )
                            }
                            Card(onClick = { nav.navigate(Routes.Monitoring) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.ShowChart, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Monitoreo y Ahorro") },
                                    supportingContent = { Text("Consulta tu producción y ahorro en tiempo real.") }
                                )
                            }
                        } else if (userRole == "ADMIN") {
                            // --- Tarjetas para el rol ADMIN ---
                            Card(onClick = { nav.navigate(Routes.ManageProducts) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.Category, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Gestionar Productos") },
                                    supportingContent = { Text("Añadir, editar o eliminar productos.") }
                                )
                            }
                            Card(onClick = { nav.navigate(Routes.Quote) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.RequestQuote, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Nueva Cotización") },
                                    supportingContent = { Text("Explora productos y crea una nueva cotización.") }
                                )
                            }
                            Card(onClick = { nav.navigate(Routes.Projects) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.HomeWork, null, modifier = Modifier.size(40.dp)) },
                                    headlineContent = { Text("Mis Cotizaciones") },
                                    supportingContent = { Text("Visualiza tus cotizaciones guardadas.") }
                                )
                            }
                            Card(onClick = { nav.navigate(Routes.Monitoring) }) {
                                ListItem(
                                    leadingContent = { Icon(Icons.Outlined.ShowChart, null, modifier = Modifier.size(40.dp)) },
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
}
