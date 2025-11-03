package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onLogout: () -> Unit
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "¿Qué quieres hacer hoy?",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { nav.navigate(Routes.Profile) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ver mi perfil") }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión") }
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
