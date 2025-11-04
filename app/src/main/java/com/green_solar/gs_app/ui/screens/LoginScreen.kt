package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.green_solar.gs_app.ui.components.login.LoginViewModel
import com.green_solar.gs_app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val state by vm.ui.collectAsState()

    // cuando el login termina OK, navega
    LaunchedEffect(state.done) {
        if (state.done) onLoggedIn()
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text("Iniciar sesión", color = White) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

            }
            Text(
                text = "Bienvenido a Green Solar",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tu energía limpia comienza aquí",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = White
                )
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = White
                )
            )

            Button(
                onClick = { vm.login() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isLoading) "Entrando..." else "Entrar")
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Aún no tienes cuenta?")
                TextButton(onClick = onRegisterClick ) {
                    Text("Regístrate")
                }
            }
        }
    }
}