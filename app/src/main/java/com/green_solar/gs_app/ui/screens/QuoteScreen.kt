package com.green_solar.gs_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    val productQuantities = remember { mutableStateMapOf<Long, Int>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.creationSuccess, state.creationError) {
        if (state.creationSuccess) {
            scope.launch { snackbarHostState.showSnackbar("Cart created successfully!") }
            vm.resetCreationStatus()
            productQuantities.clear()
        }
        state.creationError?.let {
            scope.launch { snackbarHostState.showSnackbar("Error: $it") }
            vm.resetCreationStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Quote") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (productQuantities.any { it.value > 0 }) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Create Cart") },
                    onClick = {
                        val itemsToCreate = productQuantities.filter { it.value > 0 }
                        // CORRECTED: Removed the auto-generated description with the date.
                        vm.createCart(
                            name = "New Quote", // A more generic name
                            description = null, // Passing null is cleaner.
                            items = itemsToCreate
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
                ProductListWithCounter(
                    products = state.products,
                    quantities = productQuantities,
                    onQuantityChanged = { product, newQuantity ->
                        if (newQuantity > 0) {
                            productQuantities[product.id] = newQuantity
                        } else {
                            productQuantities.remove(product.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductListWithCounter(
    products: List<Product>,
    quantities: SnapshotStateMap<Long, Int>,
    onQuantityChanged: (Product, Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                quantity = quantities.getOrDefault(product.id, 0),
                onQuantityChanged = { newQuantity -> onQuantityChanged(product, newQuantity) }
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imgUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            QuantityCounter(
                quantity = quantity,
                onIncrement = { onQuantityChanged(quantity + 1) },
                onDecrement = { if (quantity > 0) onQuantityChanged(quantity - 1) }
            )
        }
    }
}

@Composable
private fun QuantityCounter(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onDecrement, enabled = quantity > 0, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
        }
        Text(text = quantity.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Increase quantity")
        }
    }
}