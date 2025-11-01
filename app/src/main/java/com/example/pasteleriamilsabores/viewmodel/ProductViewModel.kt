package com.example.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.data.model.Product
import com.example.pasteleriamilsabores.data.repo.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

sealed class UiState {
    object Loading: UiState()
    data class Success(
        val products: List<Product>,
        val searchQuery: String = "",
        val filteredProducts: List<Product> = products
    ): UiState()
    sealed class Error: UiState() {
        data class DatabaseError(val message: String): Error()
        data class AssetError(val message: String): Error()
        data class JsonError(val message: String): Error()
        data class UnknownError(val message: String): Error()
    }
}

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadProducts()
        viewModelScope.launch {
            _searchQuery.collect { query ->
                updateFilteredProducts(query)
            }
        }
    }

    private fun updateFilteredProducts(query: String) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredProducts = if (query.isBlank()) {
                    currentState.products
                } else {
                    currentState.products.filter { product ->
                        product.titulo.contains(query, ignoreCase = true) ||
                        product.descripcion.contains(query, ignoreCase = true)
                    }
                }
            )
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadProducts(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                repository.initializeIfNeeded(forceUpdate)
                repository.getProductsFlow().collectLatest { products ->
                    _uiState.value = UiState.Success(
                        products = products,
                        searchQuery = _searchQuery.value,
                        filteredProducts = if (_searchQuery.value.isBlank()) {
                            products
                        } else {
                            products.filter { product ->
                                product.titulo.contains(_searchQuery.value, ignoreCase = true) ||
                                product.descripcion.contains(_searchQuery.value, ignoreCase = true)
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                val error = when(e) {
                    is ProductRepository.Error.DatabaseError ->
                        UiState.Error.DatabaseError("Error al acceder a la base de datos")
                    is ProductRepository.Error.AssetLoadError ->
                        UiState.Error.AssetError("Error al cargar los datos iniciales")
                    is ProductRepository.Error.JsonParseError ->
                        UiState.Error.JsonError("Error al procesar los datos")
                    else -> UiState.Error.UnknownError(e.message ?: "Error desconocido")
                }
                _uiState.value = error
            }
        }
    }

    suspend fun getProductById(id: Int): Product? {
        return repository.getProductById(id)
    }

    companion object {
        fun factory(repository: ProductRepository) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductViewModel(repository) as T
            }
        }
    }
}
