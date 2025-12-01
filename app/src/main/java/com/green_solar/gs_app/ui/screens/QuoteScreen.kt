package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.green_solar.gs_app.domain.model.Cotizacion
import com.green_solar.gs_app.domain.model.Producto
import com.green_solar.gs_app.ui.components.cotizacion.CotizacionViewModel
import com.green_solar.gs_app.ui.components.cotizacion.CotizacionViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteScreen(
    nav: NavController,
    vm: CotizacionViewModel = viewModel(factory = CotizacionViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()
    var selectedProducts by remember { mutableStateOf<Set<Producto>>(emptySet()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efecto para mostrar el Snackbar cuando la creación es exitosa o falla
    LaunchedEffect(state.creationSuccess, state.creationError) {
        if (state.creationSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("¡Cotización creada con éxito!")
            }
            vm.resetCreationStatus()
            selectedProducts = emptySet() // Limpiar selección
        }
        state.creationError?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Error: $it")
            }
            vm.resetCreationStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Cotización") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedProducts.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Crear Cotización") },
                    onClick = {
                        val newCotizacion = Cotizacion(
                            id = UUID.randomUUID().toString(), // ID temporal, el backend lo genera
                            nombre = "Nueva Cotización", // Podríamos pedirlo en un dialogo
                            descripcion = "Creada desde la app",
                            productos = selectedProducts.toList()
                        )
                        vm.createCotizacion(newCotizacion)
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoadingProductos) {
                CircularProgressIndicator()
            } else if (state.productosError != null) {
                Text(
                    text = "Error al cargar productos: ${state.productosError}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else {
                ProductList(
                    products = state.productos,
                    selectedProducts = selectedProducts,
                    onProductSelected = {
                        selectedProducts = if (it in selectedProducts) {
                            selectedProducts - it
                        } else {
                            selectedProducts + it
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductList(
    products: List<Producto>,
    selectedProducts: Set<Producto>,
    onProductSelected: (Producto) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(products) {
            ProductItem(it, selectedProducts.contains(it), onProductSelected)
        }
    }
}

@Composable
private fun ProductItem(product: Producto, isSelected: Boolean, onProductSelected: (Producto) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onProductSelected(product) }
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(product.nombre, style = MaterialTheme.typography.bodyLarge)
            Text(product.descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}
