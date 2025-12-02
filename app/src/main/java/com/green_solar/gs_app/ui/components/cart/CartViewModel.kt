package com.green_solar.gs_app.ui.components.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.model.ProductCategory
import com.green_solar.gs_app.domain.repository.CartRepository
import com.green_solar.gs_app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadAvailableProducts()
        loadUserCarts()
    }

    fun getCartById(cartId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCarts = true, selectedCart = null) }
            cartRepository.getCartById(cartId)
                .onSuccess { cart -> _state.update { it.copy(isLoadingCarts = false, selectedCart = cart) } }
                .onFailure { error -> _state.update { it.copy(isLoadingCarts = false, cartsError = error.message) } }
        }
    }

    fun updateCart(cartId: Long, name: String, description: String?, items: List<Long>) {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true, updateSuccess = false, updateError = null) }
            cartRepository.updateCart(cartId, name, description, items)
                .onSuccess {
                    _state.update { it.copy(isUpdating = false, updateSuccess = true) }
                    loadUserCarts()
                }
                .onFailure { error -> _state.update { it.copy(isUpdating = false, updateError = error.message) } }
        }
    }

    fun loadAvailableProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProducts = true) }
            productRepository.getAllProducts()
                .onSuccess { products -> _state.update { it.copy(isLoadingProducts = false, products = products) } }
                .onFailure { error -> _state.update { it.copy(isLoadingProducts = false, productsError = error.message) } }
        }
    }

    fun searchProducts(name: String?, category: ProductCategory?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProducts = true, productsError = null) }
            cartRepository.searchProducts(name, category)
                .onSuccess { products -> _state.update { it.copy(isLoadingProducts = false, products = products) } }
                .onFailure { error -> _state.update { it.copy(isLoadingProducts = false, productsError = error.message) } }
        }
    }

    fun loadUserCarts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCarts = true) }
            cartRepository.getUserCarts()
                .onSuccess { carts -> _state.update { it.copy(isLoadingCarts = false, carts = carts) } }
                .onFailure { error -> _state.update { it.copy(isLoadingCarts = false, cartsError = error.message) } }
        }
    }

    fun createCart(name: String, description: String?, items: Map<Long, Int>) {
        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, creationSuccess = false, creationError = null, newlyCreatedCart = null) }
            cartRepository.createCart(name, description, items)
                .onSuccess { newCart ->
                    _state.update { it.copy(isCreating = false, creationSuccess = true, newlyCreatedCart = newCart) }
                    loadUserCarts() // Refresh the list after creation
                }
                .onFailure { error -> _state.update { it.copy(isCreating = false, creationError = error.message) } }
        }
    }

    fun deleteCart(cartId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, deleteSuccess = false, deleteError = null) }
            cartRepository.deleteCart(cartId)
                .onSuccess {
                    _state.update { it.copy(isDeleting = false, deleteSuccess = true) }
                    loadUserCarts() // Refresh the list to remove the deleted item
                }
                .onFailure { error ->
                    _state.update { it.copy(isDeleting = false, deleteError = error.message) }
                }
        }
    }

    fun resetCreationStatus() {
        _state.update { it.copy(creationSuccess = false, creationError = null, newlyCreatedCart = null) }
    }

    fun resetDeleteStatus() {
        _state.update { it.copy(deleteSuccess = false, deleteError = null) }
    }

    fun resetUpdateStatus() {
        _state.update { it.copy(updateSuccess = false, updateError = null) }
    }
}