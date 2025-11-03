package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.green_solar.gs_app.ui.components.auth.SignupViewModel  // <-- ajusta si tu VM está en otro paquete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: SignupViewModel,
    onRegistered: () -> Unit
) {
    val state by viewModel.ui.collectAsState()

    // Navega una sola vez cuando el registro termina OK
    LaunchedEffect(state.done) {
        if (state.done) {
            viewModel.resetDone()
            onRegistered()
        }
    }

    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Crear cuenta") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nombre
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onName,
                label = { Text("Nombre") },
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Email
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmail,
                label = { Text("Email") },
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Contraseña
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPass,
                label = { Text("Contraseña") },
                isError = state.passwordError != null,
                supportingText = {
                    state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass = !showPass }) {
                        Text(if (showPass) "Ocultar" else "Ver")
                    }
                }
            )

            // Confirmar contraseña
            OutlinedTextField(
                value = state.confirm,
                onValueChange = viewModel::onConfirm,
                label = { Text("Repite contraseña") },
                isError = state.confirmError != null,
                supportingText = {
                    state.confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showConfirm = !showConfirm }) {
                        Text(if (showConfirm) "Ocultar" else "Ver")
                    }
                }
            )

            // Error general de API/red
            state.generalError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.submit() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text("Registrarme")
                }
            }
        }
    }
}
