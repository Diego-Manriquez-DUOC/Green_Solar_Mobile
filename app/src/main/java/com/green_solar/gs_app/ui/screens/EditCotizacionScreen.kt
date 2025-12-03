package com.green_solar.gs_app.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import com.green_solar.gs_app.domain.model.ProductCategory
import com.green_solar.gs_app.ui.components.cart.CartViewModel
import com.green_solar.gs_app.ui.components.cart.CartViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun EditCotizacionScreen(
    nav: NavController,
    cotizacionId: Long,
    vm: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()
    val productQuantities = remember { mutableStateMapOf<Long, Int>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var filterName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var filtersVisible by remember { mutableStateOf(false) }

    LaunchedEffect(cotizacionId) {
        vm.getCartById(cotizacionId)
    }

    LaunchedEffect(state.selectedCart) {
        state.selectedCart?.let { cart ->
            name = cart.name
            description = cart.description ?: ""
            productQuantities.clear()
            cart.cartItems.forEach { item ->
                productQuantities[item.product.id] = item.quantity
            }
        }
    }

    LaunchedEffect(filterName, selectedCategory) {
        delay(300)
        vm.searchProducts(filterName.takeIf { it.isNotBlank() }, selectedCategory)
    }

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            // Reseteamos el estado para que este bloque no se vuelva a ejecutar
            vm.resetUpdateStatus()
            scope.launch {
                snackbarHostState.showSnackbar("Espere porfavor....")
                // Esperamos un poco para que el mensaje sea visible

                delay(1500)
                snackbarHostState.showSnackbar("Producto Actualizado.")

                // Navegamos a la lista de cotizaciones, limpiando el historial hasta la
                // pantalla "main". Esto fuerza el refresco de la lista.
                nav.navigate("projects")
                {

                    popUpTo("main") {

                        inclusive = false
                    }
                }

            }
        }
    }

    LaunchedEffect(state.updateError) {
        state.updateError?.let {
            scope.launch { snackbarHostState.showSnackbar("Error: $it") }
            vm.resetUpdateStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar cotizacion") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            val isEnabled = !state.isUpdating
            ExtendedFloatingActionButton(
                onClick = {
                    if (!isEnabled) return@ExtendedFloatingActionButton

                    val itemsToUpdate = productQuantities.filter { it.value > 0 }
                    if (itemsToUpdate.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please select at least one product.")
                        }
                        return@ExtendedFloatingActionButton
                    }

                    val productIds = itemsToUpdate.flatMap { (productId, quantity) ->
                        List(quantity) { productId }
                    }

                    vm.updateCart(
                        cartId = cotizacionId,
                        name = name.ifBlank { "Updated Quote" },
                        description = description.takeIf { it.isNotBlank() },
                        items = productIds
                    )
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Update Quote") },
                text = { Text("Update Quote") },
                containerColor = if (isEnabled) {
                    FloatingActionButtonDefaults.containerColor
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                },
                contentColor = if (isEnabled) {
                    contentColorFor(FloatingActionButtonDefaults.containerColor)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la cotizacion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Products",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { filtersVisible = !filtersVisible }
                ) {
                    Text("Filters", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (filtersVisible) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Toggle filters",
                        modifier = Modifier.padding(4.dp)
                    )
                }

                if (filtersVisible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = filterName,
                        onValueChange = { filterName = it },
                        label = { Text("Search by name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryDropdown(selectedCategory) { selectedCategory = it }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoadingProducts) {
                    CircularProgressIndicator()
                } else if (state.productsError != null) {
                    Text(
                        text = "Error loading products: ${state.productsError}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = ProductCategory.values()

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "All Categories",
            onValueChange = {}, // readOnly
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("All Categories") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
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
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products found.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
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
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Text(product.category.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
