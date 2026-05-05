package com.example.clothingapp.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.clothingapp.data.Product
import com.example.clothingapp.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMonth = MutableStateFlow<Int?>(null) // null 表示全部，1-12 表示月份
    val selectedMonth: StateFlow<Int?> = _selectedMonth.asStateFlow()

    // 分类管理数据
    private val _fabrics = MutableStateFlow(listOf("棉", "麻", "丝", "毛", "涤纶", "雪纺"))
    val fabrics: StateFlow<List<String>> = _fabrics.asStateFlow()

    private val _factories = MutableStateFlow(listOf("一号工厂", "二号工厂", "外协A", "外协B"))
    val factories: StateFlow<List<String>> = _factories.asStateFlow()

    private val _accessories = MutableStateFlow(listOf("拉链", "纽扣", "织带", "标牌"))
    val accessories: StateFlow<List<String>> = _accessories.asStateFlow()

    private val _processes = MutableStateFlow(listOf("印花", "绣花", "洗水", "压褶"))
    val processes: StateFlow<List<String>> = _processes.asStateFlow()

    private val _names = MutableStateFlow(listOf("连衣裙", "衬衫", "外套", "长裤", "半身裙"))
    val names: StateFlow<List<String>> = _names.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.allProducts.collectLatest { products ->
                filterAndSetProducts(products)
            }
        }
    }

    private fun filterAndSetProducts(allProducts: List<Product>) {
        val query = _searchQuery.value.lowercase()
        val month = _selectedMonth.value
        
        _products.value = allProducts.filter { product ->
            val matchesQuery = query.isEmpty() || 
                product.code.lowercase().contains(query) || 
                product.fabrics.any { it.name.lowercase().contains(query) }
            
            val matchesMonth = if (month == null) true else {
                val cal = Calendar.getInstance().apply { timeInMillis = product.createdAt }
                cal.get(Calendar.MONTH) + 1 == month
            }
            
            matchesQuery && matchesMonth
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadProducts()
    }

    fun setSelectedMonth(month: Int?) {
        _selectedMonth.value = month
        loadProducts()
    }

    fun addItem(category: String, name: String) {
        if (name.isBlank()) return
        when (category) {
            "fabric" -> if (!_fabrics.value.contains(name)) _fabrics.value = _fabrics.value + name
            "factory" -> if (!_factories.value.contains(name)) _factories.value = _factories.value + name
            "accessory" -> if (!_accessories.value.contains(name)) _accessories.value = _accessories.value + name
            "process" -> if (!_processes.value.contains(name)) _processes.value = _processes.value + name
            "name" -> if (!_names.value.contains(name)) _names.value = _names.value + name
        }
    }

    fun removeItem(category: String, name: String) {
        when (category) {
            "fabric" -> _fabrics.value = _fabrics.value - name
            "factory" -> _factories.value = _factories.value - name
            "accessory" -> _accessories.value = _accessories.value - name
            "process" -> _processes.value = _processes.value - name
            "name" -> _names.value = _names.value - name
        }
    }

    fun saveImageToInternal(context: Context, uriString: String): String {
        if (uriString.startsWith("file:///data/user/0/")) return uriString
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
            Uri.fromFile(file).toString()
        } catch (e: Exception) { uriString }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (product.id == 0) repository.insertProduct(product)
            else repository.updateProduct(product)
            loadProducts()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            loadProducts()
        }
    }

    // 备份功能：打包数据库和照片
    fun exportBackup(context: Context): File? {
        val backupFile = File(context.cacheDir, "ClothingApp_Backup_${System.currentTimeMillis()}.zip")
        return try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(backupFile))).use { zos ->
                // 1. 备份数据库文件
                val dbFile = context.getDatabasePath("clothing_database")
                if (dbFile.exists()) {
                    addToZip(zos, dbFile, "database/clothing_database")
                }
                // 2. 备份照片文件夹
                val filesDir = context.filesDir
                filesDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("img_")) {
                        addToZip(zos, file, "images/${file.name}")
                    }
                }
            }
            backupFile
        } catch (e: Exception) {
            null
        }
    }

    private fun addToZip(zos: ZipOutputStream, file: File, path: String) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        file.inputStream().use { it.copyTo(zos) }
        zos.closeEntry()
    }

    // 恢复功能：解压并替换
    fun importBackup(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        if (entry.name.startsWith("database/")) {
                            val dbFile = context.getDatabasePath("clothing_database")
                            dbFile.parentFile?.mkdirs()
                            FileOutputStream(dbFile).use { zis.copyTo(it) }
                        } else if (entry.name.startsWith("images/")) {
                            val fileName = entry.name.substringAfter("images/")
                            val destFile = File(context.filesDir, fileName)
                            FileOutputStream(destFile).use { zis.copyTo(it) }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
                loadProducts()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
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
