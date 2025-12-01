package com.green_solar.gs_app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.green_solar.gs_app.domain.model.Cart
import com.green_solar.gs_app.domain.model.CartItem
import com.green_solar.gs_app.ui.components.cart.CartViewModel
import com.green_solar.gs_app.ui.components.cart.CartViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    nav: NavController,
    vm: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- State for Delete Dialog ---
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cartToDelete by remember { mutableStateOf<Cart?>(null) }

    // --- LaunchedEffect for Delete Operation ---
    LaunchedEffect(state.deleteSuccess, state.deleteError) {
        if (state.deleteSuccess) {
            scope.launch { snackbarHostState.showSnackbar("Quote deleted successfully!") }
            vm.resetDeleteStatus()
        }
        state.deleteError?.let {
            scope.launch { snackbarHostState.showSnackbar("Error deleting quote: $it") }
            vm.resetDeleteStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Quotes") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoadingCarts || state.isDeleting) {
                CircularProgressIndicator()
            } else if (state.cartsError != null) {
                Text("Error: ${state.cartsError}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            } else if (state.carts.isEmpty()) {
                Text("You don't have any quotes yet.", style = MaterialTheme.typography.bodyLarge)
            } else {
                CartsList(
                    carts = state.carts,
                    onDeleteClick = {
                        cartToDelete = it
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    // --- Confirmation Dialog for Deletion ---
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            cartName = cartToDelete?.name ?: "",
            onConfirm = {
                cartToDelete?.let { vm.deleteCart(it.id.toLong()) }
                showDeleteDialog = false
                cartToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                cartToDelete = null
            }
        )
    }
}

@Composable
private fun CartsList(carts: List<Cart>, onDeleteClick: (Cart) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(carts) { cart ->
            ExpandableCartCard(cart = cart, onDeleteClick = { onDeleteClick(cart) })
        }
    }
}

@Composable
private fun ExpandableCartCard(cart: Cart, onDeleteClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }.padding(16.dp)) {
            Text(text = cart.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            cart.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(text = "Productos distintos: ${cart.cartItems.size}", style = MaterialTheme.typography.bodySmall)

            if (isExpanded) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(text = "Productos:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    cart.cartItems.forEach { CartProductItem(it) }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { /* TODO: Handle edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartProductItem(item: CartItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item.product.imgUrl,
            contentDescription = item.product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text("$${item.product.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(cartName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete the quote \"$cartName\"? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun ProjectsScreenPreview() {
    ProjectsScreen(nav = rememberNavController())
}
