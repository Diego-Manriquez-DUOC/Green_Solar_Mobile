package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.green_solar.gs_app.ui.navigation.Routes
import com.green_solar.gs_app.ui.theme.GsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    nav: NavHostController,
    onLogout: () -> Unit // <-- ¡CORREGIDO! Ya no es @Composable
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Green Solar") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¡Bienvenido!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Gestiona tus proyectos de energía solar de forma simple.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(32.dp))

            // --- Tarjetas de Navegación ---
            NavCard(
                title = "Mis Proyectos",
                subtitle = "Visualiza tus instalaciones y su estado.",
                onClick = { /* TODO: nav.navigate(Routes.Projects) */ },
                icon = { Icon(Icons.Default.WbSunny, null, tint = MaterialTheme.colorScheme.primary) }
            )

            NavCard(
                title = "Monitoreo y Ahorro",
                subtitle = "Revisa tu consumo y ahorro energético.",
                onClick = { /* TODO: nav.navigate(Routes.Monitoring) */ },
                icon = { Icon(Icons.Default.BarChart, null, tint = MaterialTheme.colorScheme.primary) }
            )

            NavCard(
                title = "Mi Perfil",
                subtitle = "Administra tus datos y configuración.",
                onClick = { nav.navigate(Routes.Profile) },
                icon = { Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.primary) }
            )

            Spacer(Modifier.weight(1f)) // Empuja el botón de logout hacia abajo

            OutlinedButton(
                onClick = onLogout, // Ahora coincide perfectamente
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp)) { icon() }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    val navController = rememberNavController()
    GsTheme {
        MainScreen(
            nav = navController,
            onLogout = {}
        )
    }
}
