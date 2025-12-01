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
import com.green_solar.gs_app.domain.model.Product
import com.green_solar.gs_app.ui.components.cart.CartViewModel
import com.green_solar.gs_app.ui.components.cart.CartViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    nav: NavController,
    vm: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()
    var selectedProducts by remember { mutableStateOf<Set<Product>>(emptySet()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.creationSuccess, state.creationError) {
        if (state.creationSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Cart created successfully!")
            }
            vm.resetCreationStatus()
            selectedProducts = emptySet() // Clear selection
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
                title = { Text("Create New Cart") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedProducts.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Create Cart") },
                    onClick = {
                        val productIds = selectedProducts.map { it.id }
                        vm.createCart(
                            name = "New Cart from App", // A dialog could be shown for this
                            description = "Created on ${java.util.Date()}",
                            productIds = productIds
                        )
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoadingProducts) {
                CircularProgressIndicator()
            } else if (state.productsError != null) {
                Text(
                    text = "Error loading products: ${state.productsError}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else {
                ProductList(
                    products = state.products,
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
    products: List<Product>,
    selectedProducts: Set<Product>,
    onProductSelected: (Product) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(products) {
            ProductItem(it, selectedProducts.contains(it), onProductSelected)
        }
    }
}

@Composable
private fun ProductItem(product: Product, isSelected: Boolean, onProductSelected: (Product) -> Unit) {
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
            Text(product.name, style = MaterialTheme.typography.bodyLarge)
            product.desc?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
