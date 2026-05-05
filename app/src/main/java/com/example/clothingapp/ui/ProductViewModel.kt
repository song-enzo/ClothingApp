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

    // 分类管理数据
    private val _fabrics = MutableStateFlow(listOf("棉", "麻", "丝", "毛", "涤纶", "雪纺"))
    val fabrics: StateFlow<List<String>> = _fabrics.asStateFlow()

    private val _factories = MutableStateFlow(listOf("一号工厂", "二号工厂", "外协A", "外协B"))
    val factories: StateFlow<List<String>> = _factories.asStateFlow()

    private val _accessories = MutableStateFlow(listOf("拉链", "纽扣", "织带", "标牌"))
    val accessories: StateFlow<List<String>> = _accessories.asStateFlow()

    private val _processes = MutableStateFlow(listOf("印花", "绣花", "洗水", "压褶"))
    val processes: StateFlow<List<String>> = _processes.asStateFlow()

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

    // 通用分类管理方法
    fun addItem(category: String, name: String) {
        if (name.isBlank()) return
        when (category) {
            "fabric" -> if (!_fabrics.value.contains(name)) _fabrics.value = _fabrics.value + name
            "factory" -> if (!_factories.value.contains(name)) _factories.value = _factories.value + name
            "accessory" -> if (!_accessories.value.contains(name)) _accessories.value = _accessories.value + name
            "process" -> if (!_processes.value.contains(name)) _processes.value = _processes.value + name
        }
    }

    fun removeItem(category: String, name: String) {
        when (category) {
            "fabric" -> _fabrics.value = _fabrics.value - name
            "factory" -> _factories.value = _factories.value - name
            "accessory" -> _accessories.value = _accessories.value - name
            "process" -> _processes.value = _processes.value - name
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (product.id == 0) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
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
