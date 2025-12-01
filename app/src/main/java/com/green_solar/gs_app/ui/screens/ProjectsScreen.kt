package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.ui.components.cotizacion.CotizacionViewModel
import com.green_solar.gs_app.ui.components.cotizacion.CotizacionViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    nav: NavController,
    // Usamos el CotizacionViewModel para obtener la lista de cotizaciones
    vm: CotizacionViewModel = viewModel(factory = CotizacionViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()

    // Cuando la pantalla se muestre, cargamos la lista de cotizaciones
    LaunchedEffect(Unit) {
        vm.loadCotizaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Cotizaciones") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoadingCotizaciones) {
                CircularProgressIndicator()
            } else if (state.cotizacionesError != null) {
                Text(
                    text = "Error: ${state.cotizacionesError}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else if (state.cotizaciones.isEmpty()) {
                Text("Aún no tienes cotizaciones.")
            } else {
                CotizacionesList(cotizaciones = state.cotizaciones)
            }
        }
    }
}

@Composable
private fun CotizacionesList(cotizaciones: List<Cotizacion>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(cotizaciones) {
            CotizacionItem(cotizacion = it)
        }
    }
}

@Composable
private fun CotizacionItem(cotizacion: Cotizacion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cotizacion.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Productos: ${cotizacion.productos.size}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProjectsScreenPreview() {
    ProjectsScreen(nav = rememberNavController())
}
