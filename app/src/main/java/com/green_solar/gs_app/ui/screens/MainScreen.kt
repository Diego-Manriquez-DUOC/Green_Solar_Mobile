package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.green_solar.gs_app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(nav: NavController, onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Green Solar") },
                actions = {
                    IconButton(onClick = { nav.navigate(Routes.Profile) }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil")
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
