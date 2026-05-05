package com.example.clothingapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.clothingapp.data.Product
import com.example.clothingapp.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // 面料名称列表，可以从设置中添加
    private val _availableFabrics = MutableStateFlow<List<String>>(listOf("棉", "麻", "丝", "毛", "涤纶", "雪纺"))
    val availableFabrics: StateFlow<List<String>> = _availableFabrics.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.allProducts.collect { products ->
                _products.value = products
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchProducts(query).collect { products ->
                _products.value = products
            }
        }
    }

    fun addFabric(name: String) {
        if (name.isNotBlank() && !_availableFabrics.value.contains(name)) {
            _availableFabrics.value = _availableFabrics.value + name
        }
    }

    fun removeFabric(name: String) {
        _availableFabrics.value = _availableFabrics.value - name
    }

    fun addProduct(
        code: String,
        name: String = "",
        factoryName: String = "",
        fabricNames: List<String> = emptyList(),
        fabricPrices: List<Double> = emptyList(),
        laborCost: Double = 0.0,
        ironingAndButtons: Double = 0.0,
        accessories: Double = 0.0,
        notes: String = "",
        imagePaths: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val product = Product(
                code = code,
                name = name,
                factoryName = factoryName,
                fabricNames = fabricNames,
                fabricPrices = fabricPrices,
                laborCost = laborCost,
                ironingAndButtons = ironingAndButtons,
                accessories = accessories,
                notes = notes,
                imagePaths = imagePaths
            )
            repository.insertProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
