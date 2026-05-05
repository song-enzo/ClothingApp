package com.example.clothingapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.clothingapp.ui.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController, viewModel: ProductViewModel) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var factoryName by remember { mutableStateOf("") }
    var fabricNames by remember { mutableStateOf(mutableStateListOf("", "", "", "")) }
    var fabricPrices by remember { mutableStateOf(mutableStateListOf("", "", "", "")) }
    var laborCost by remember { mutableStateOf("") }
    var ironingAndButtons by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(mutableStateListOf<Uri>()) }

    val availableFabrics by viewModel.availableFabrics.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages.addAll(uris)
    }

    val totalCost = (fabricPrices.map { it.toDoubleOrNull() ?: 0.0 }.sum() +
            (laborCost.toDoubleOrNull() ?: 0.0) +
            (ironingAndButtons.toDoubleOrNull() ?: 0.0) +
            (accessories.toDoubleOrNull() ?: 0.0))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加新款", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFD4A853))
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (code.isNotEmpty()) {
                                viewModel.addProduct(
                                    code = code,
                                    name = name,
                                    factoryName = factoryName,
                                    fabricNames = fabricNames.toList(),
                                    fabricPrices = fabricPrices.map { it.toDoubleOrNull() ?: 0.0 },
                                    laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                                    ironingAndButtons = ironingAndButtons.toDoubleOrNull() ?: 0.0,
                                    accessories = accessories.toDoubleOrNull() ?: 0.0,
                                    notes = notes,
                                    imagePaths = selectedImages.map { it.toString() }
                                )
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
            // 图片选择
            Text("款式图片", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedImages.forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C2C2E))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Image", tint = Color.Gray)
                }
            }

            // 基本信息
            FormField("编号 (必填)", code, placeholder = "请输入款式编号") { code = it }
            FormField("名称", name, placeholder = "请输入款式名称") { name = it }
            FormField("工厂", factoryName, placeholder = "请输入工厂名称") { factoryName = it }

            // 面料明细
            Text("面料明细", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold)
            repeat(4) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    FabricDropdown(
                        label = "面料 ${i + 1}",
                        value = fabricNames[i],
                        options = availableFabrics,
                        modifier = Modifier.weight(1f)
                    ) { fabricNames[i] = it }

                    PriceField(
                        label = "价格",
                        value = fabricPrices[i],
                        modifier = Modifier.weight(0.6f)
                    ) { fabricPrices[i] = it }
                }
            }

            // 其他成本
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceField("工价", laborCost, Modifier.weight(1f)) { laborCost = it }
                PriceField("烫/扣", ironingAndButtons, Modifier.weight(1f)) { ironingAndButtons = it }
                PriceField("辅料", accessories, Modifier.weight(1f)) { accessories = it }
            }

            // 综合成本
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("综合成本价", color = Color.White, modifier = Modifier.weight(1f))
                    Text(
                        "¥${String.format("%.2f", totalCost)}",
                        color = Color(0xFFD4A853),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            FormField("备注", notes, singleLine = false, placeholder = "添加备注信息...") { notes = it }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2E))
                .padding(12.dp),
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            singleLine = singleLine,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.Gray, fontSize = 14.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun PriceField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        BasicTextField(
            value = if (value == "0.0") "" else value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2E))
                .padding(12.dp)
                .onFocusChanged { if (it.isFocused) onValueChange("") },
            textStyle = TextStyle(color = Color(0xFFD4A853), fontSize = 14.sp, fontWeight = FontWeight.Bold),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            decorationBox = { innerTextField ->
                if (value.isEmpty() || value == "0.0") {
                    Text("0", color = Color.Gray, fontSize = 14.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun FabricDropdown(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2E))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (value.isEmpty()) "选择面料" else value,
                    color = if (value.isEmpty()) Color.Gray else Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF2C2C2E))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.White) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
