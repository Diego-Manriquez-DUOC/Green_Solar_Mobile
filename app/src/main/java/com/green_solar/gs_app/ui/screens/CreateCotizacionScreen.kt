package com.green_solar.gs_app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.green_solar.gs_app.ui.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CreateCotizacionScreen(
    nav: NavController,
    vm: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()
    val productQuantities = remember { mutableStateMapOf<Long, Int>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State for filters
    var filterName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null)
    }
    var filtersVisible by remember { mutableStateOf(false) }

    // Trigger search when filters change
    LaunchedEffect(filterName, selectedCategory) {
        // A small delay to avoid spamming the API while typing
        delay(300)
        vm.searchProducts(filterName.takeIf { it.isNotBlank() }, selectedCategory)
    }

    LaunchedEffect(state.creationSuccess) {
        if (state.creationSuccess) {
            val newCartId = state.newlyCreatedCart?.id
            if (newCartId != null) {
                scope.launch { snackbarHostState.showSnackbar("Cotizacion creada correctamente.") }
                nav.navigate("${Routes.Projects}/$newCartId") {
                    popUpTo(Routes.Main) // Pop back to the main screen, not inclusive
                }
                vm.resetCreationStatus()
            }
        }
    }

    LaunchedEffect(state.creationError) {
        state.creationError?.let {
            scope.launch { snackbarHostState.showSnackbar("Error: $it") }
            vm.resetCreationStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva cotizacion") },
                navigationIcon = {
                    IconButton(onClick = { 
                        // CORRECTED: Use the safe back navigation pattern
                        if (nav.previousBackStackEntry != null) {
                            nav.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atras")
                    }
                }
            )
        },
        floatingActionButton = {
            val isEnabled = !state.isCreating
            ExtendedFloatingActionButton(
                onClick = {
                    if (!isEnabled) return@ExtendedFloatingActionButton

                    val itemsToCreate = productQuantities.filter { it.value > 0 }
                    if (itemsToCreate.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor introduzca un producto al carrito.")
                        }
                        return@ExtendedFloatingActionButton
                    }
                    vm.createCart(
                        name = name.ifBlank { "Nueva Cotizacion" },
                        description = description.takeIf { it.isNotBlank() },
                        items = itemsToCreate
                    )
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Crear carrito") },
                text = { Text("Crear carrito") },
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
            // Name and Description Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la Cotizacion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Products Section
            Text(
                text = "Productos",
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
                        label = { Text("Buscar por nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryDropdown(selectedCategory) { selectedCategory = it }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Product List
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoadingProducts) {
                    CircularProgressIndicator()
                } else if (state.productsError != null) {
                    Text(
                        text = "Error al cargar productos: ${state.productsError}",
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
            value = selectedCategory?.name ?: "Todas las categorias",
            onValueChange = {}, // readOnly
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Todas las categorias") },
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
            Text("No se ha encontrado productos.", style = MaterialTheme.typography.bodyLarge)
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