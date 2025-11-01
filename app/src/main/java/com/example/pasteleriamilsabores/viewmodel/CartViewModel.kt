package com.example.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pasteleriamilsabores.data.local.entity.CartItemEntity
import com.example.pasteleriamilsabores.data.model.Cart
import com.example.pasteleriamilsabores.data.model.CartItem
import com.example.pasteleriamilsabores.data.model.Product
import com.example.pasteleriamilsabores.data.repo.CartRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class CartUiState {
    object Loading : CartUiState()
    data class Success(val cart: Cart) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            try {
                repository.getCartFlow().collect { items ->
                    val total = items.sumOf { it.precio * it.cantidad }
                    _uiState.value = CartUiState.Success(
                        Cart(
                            items = items.map {
                                CartItem(
                                    productId = it.productId,
                                    cantidad = it.cantidad,
                                    precio = it.precio,
                                    titulo = it.titulo,
                                    imagen = it.imagen
                                )
                            },
                            total = total
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(e.message ?: "Error al cargar el carrito")
            }
        }
    }

    fun addToCart(product: Product, cantidad: Int = 1) {
        viewModelScope.launch {
            try {
                repository.addToCart(
                    CartItemEntity(
                        productId = product.id,
                        cantidad = cantidad,
                        precio = product.precio,
                        titulo = product.titulo,
                        imagen = product.imagen
                    )
                )
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error("Error al agregar al carrito")
            }
        }
    }

    fun updateCantidad(productId: Int, cantidad: Int) {
        viewModelScope.launch {
            try {
                repository.updateCantidad(productId, cantidad)
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error("Error al actualizar cantidad")
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(
                    CartItemEntity(
                        productId = item.productId,
                        cantidad = item.cantidad,
                        precio = item.precio,
                        titulo = item.titulo,
                        imagen = item.imagen
                    )
                )
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error("Error al eliminar del carrito")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart()
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error("Error al limpiar el carrito")
            }
        }
    }

    companion object {
        fun factory(repository: CartRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CartViewModel(repository)
            }
        }
    }
}
