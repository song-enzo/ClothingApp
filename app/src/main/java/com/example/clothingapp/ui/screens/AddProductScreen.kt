package com.example.clothingapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clothingapp.data.FabricItem
import com.example.clothingapp.data.ProcessItem
import com.example.clothingapp.data.Product
import com.example.clothingapp.ui.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController, viewModel: ProductViewModel, productId: Int? = null) {
    val products by viewModel.products.collectAsState()
    val existingProduct = remember(productId) { products.find { it.id == productId } }

    // 状态初始化
    var code by remember { mutableStateOf(existingProduct?.code ?: "") }
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var factoryName by remember { mutableStateOf(existingProduct?.factoryName ?: "") }
    
    val fabrics = remember { mutableStateListOf<FabricItem>().apply { 
        if (existingProduct != null) addAll(existingProduct.fabrics) else add(FabricItem()) 
    } }
    val processes = remember { mutableStateListOf<ProcessItem>().apply { 
        if (existingProduct != null) addAll(existingProduct.processes) else add(ProcessItem()) 
    } }
    val accessories = remember { mutableStateListOf<ProcessItem>().apply { 
        if (existingProduct != null) addAll(existingProduct.accessories) else add(ProcessItem()) 
    } }

    var laborCost by remember { mutableStateOf(existingProduct?.laborCost?.toString() ?: "") }
    var ironingAndButtons by remember { mutableStateOf(existingProduct?.ironingAndButtons?.toString() ?: "") }
    var notes by remember { mutableStateOf(existingProduct?.notes ?: "") }
    
    val selectedImages = remember { mutableStateListOf<String>().apply { 
        if (existingProduct != null) addAll(existingProduct.imagePaths) 
    } }
    var mainImageIndex by remember { mutableStateOf(existingProduct?.mainImageIndex ?: 0) }

    // 下拉数据
    val availableFabrics by viewModel.fabrics.collectAsState()
    val availableFactories by viewModel.factories.collectAsState()
    val availableAccessories by viewModel.accessories.collectAsState()
    val availableProcesses by viewModel.processes.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages.addAll(uris.map { it.toString() })
    }

    val totalCost = (fabrics.sumOf { it.total } +
            processes.sumOf { it.cost } +
            accessories.sumOf { it.cost } +
            (laborCost.toDoubleOrNull() ?: 0.0) +
            (ironingAndButtons.toDoubleOrNull() ?: 0.0))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "添加新款" else "编辑款式", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFD4A853))
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (code.isNotEmpty()) {
                                val product = Product(
                                    id = productId ?: 0,
                                    code = code,
                                    name = name,
                                    factoryName = factoryName,
                                    fabrics = fabrics.toList(),
                                    processes = processes.toList(),
                                    accessories = accessories.toList(),
                                    laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                                    ironingAndButtons = ironingAndButtons.toDoubleOrNull() ?: 0.0,
                                    notes = notes,
                                    imagePaths = selectedImages.toList(),
                                    mainImageIndex = mainImageIndex
                                )
                                viewModel.saveProduct(product)
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A853)),
                        enabled = code.isNotEmpty()
                    ) {
                        Text("保存", color = Color(0xFF1A1A1E), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F12))
            )
        },
        containerColor = Color(0xFF1A1A1E)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 图片管理
            SectionHeader("款式图片 (点击设为主图，长按删除)")
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedImages.forEachIndexed { index, path ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = path,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (mainImageIndex == index) 2.dp else 0.dp,
                                    color = if (mainImageIndex == index) Color(0xFFD4A853) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { mainImageIndex = index },
                            contentScale = ContentScale.Crop
                        )
                        // 删除按钮
                        IconButton(
                            onClick = { 
                                selectedImages.removeAt(index)
                                if (mainImageIndex >= selectedImages.size) mainImageIndex = 0
                            },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        if (mainImageIndex == index) {
                            Box(modifier = Modifier.align(Alignment.BottomCenter).background(Color(0xFFD4A853)).padding(horizontal = 4.dp)) {
                                Text("主图", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2C2C2E)).clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Image", tint = Color.Gray)
                }
            }

            // 基本信息
            FormField("编号 (必填)", code, placeholder = "请输入款式编号") { code = it }
            FormField("名称", name, placeholder = "请输入款式名称") { name = it }
            DropdownField("生产工厂", factoryName, availableFactories) { factoryName = it }

            // 面料动态行
            SectionHeader("面料明细", onAdd = { fabrics.add(FabricItem()) })
            fabrics.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                    DropdownField("面料名称", item.name, availableFabrics, Modifier.weight(1.2f)) { fabrics[index] = item.copy(name = it) }
                    PriceField("米数", item.meters.toString(), Modifier.weight(0.8f)) { fabrics[index] = item.copy(meters = it.toDoubleOrNull() ?: 0.0) }
                    PriceField("单价", item.pricePerMeter.toString(), Modifier.weight(0.8f)) { fabrics[index] = item.copy(pricePerMeter = it.toDoubleOrNull() ?: 0.0) }
                    IconButton(onClick = { if (fabrics.size > 1) fabrics.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f))
                    }
                }
            }

            // 加工工艺动态行
            SectionHeader("加工工艺", onAdd = { processes.add(ProcessItem()) })
            processes.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                    DropdownField("工艺名称", item.name, availableProcesses, Modifier.weight(1.5f)) { processes[index] = item.copy(name = it) }
                    PriceField("费用", item.cost.toString(), Modifier.weight(1f)) { processes[index] = item.copy(cost = it.toDoubleOrNull() ?: 0.0) }
                    IconButton(onClick = { if (processes.size > 1) processes.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f))
                    }
                }
            }

            // 辅料动态行
            SectionHeader("辅料明细", onAdd = { accessories.add(ProcessItem()) })
            accessories.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                    DropdownField("辅料名称", item.name, availableAccessories, Modifier.weight(1.5f)) { accessories[index] = item.copy(name = it) }
                    PriceField("费用", item.cost.toString(), Modifier.weight(1f)) { accessories[index] = item.copy(cost = it.toDoubleOrNull() ?: 0.0) }
                    IconButton(onClick = { if (accessories.size > 1) accessories.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f))
                    }
                }
            }

            // 固定费用
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceField("工价", laborCost, Modifier.weight(1f)) { laborCost = it }
                PriceField("烫/扣", ironingAndButtons, Modifier.weight(1f)) { ironingAndButtons = it }
            }

            // 综合成本
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("综合成本价", color = Color.White, modifier = Modifier.weight(1f))
                    Text("¥${String.format("%.2f", totalCost)}", color = Color(0xFFD4A853), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            FormField("备注", notes, singleLine = false, placeholder = "添加备注信息...") { notes = it }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, onAdd: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (onAdd != null) {
            IconButton(onClick = onAdd, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFD4A853))
            }
        }
    }
}

@Composable
fun DropdownField(label: String, value: String, options: List<String>, modifier: Modifier = Modifier, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF2C2C2E)).clickable { expanded = true }.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (value.isEmpty()) "请选择" else value, color = if (value.isEmpty()) Color.Gray else Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF2C2C2E))) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option, color = Color.White) }, onClick = { onSelect(option); expanded = false })
                }
            }
        }
    }
}

@Composable
fun PriceField(label: String, value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = if (value == "0.0" || value == "0") "" else value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF2C2C2E)).padding(12.dp).onFocusChanged { if (it.isFocused && (value == "0.0" || value == "0")) onValueChange("") },
            textStyle = TextStyle(color = Color(0xFFD4A853), fontSize = 14.sp, fontWeight = FontWeight.Bold),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            decorationBox = { innerTextField ->
                if (value.isEmpty() || value == "0.0" || value == "0") Text("0", color = Color.Gray, fontSize = 14.sp)
                innerTextField()
            }
        )
    }
}

@Composable
fun FormField(label: String, value: String, modifier: Modifier = Modifier, singleLine: Boolean = true, placeholder: String = "", onValueChange: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF2C2C2E)).padding(12.dp),
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            singleLine = singleLine,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) Text(placeholder, color = Color.Gray, fontSize = 14.sp)
                innerTextField()
            }
        )
    }
}
