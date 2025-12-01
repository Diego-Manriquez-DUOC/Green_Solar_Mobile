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
import com.green_solar.gs_app.domain.model.Cart
// CORRECTED IMPORTS
import com.green_solar.gs_app.ui.components.cart.CartViewModelFactory
import com.green_solar.gs_app.ui.components.cart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    nav: NavController,
    vm: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current))
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Carts") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            if (state.isLoadingCarts) {
                CircularProgressIndicator()
            } else if (state.cartsError != null) {
                Text(
                    text = "Error: ${state.cartsError}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else if (state.carts.isEmpty()) {
                Text("You don't have any carts yet.")
            } else {
                CartsList(carts = state.carts)
            }
        }
    }
}

@Composable
private fun CartsList(carts: List<Cart>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(carts) { cart ->
            CartListItem(cart = cart)
        }
    }
}

@Composable
private fun CartListItem(cart: Cart) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cart.name, style = MaterialTheme.typography.titleMedium)
            cart.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Items: ${cart.cartItems.size}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProjectsScreenPreview() {
    ProjectsScreen(nav = rememberNavController())
}
