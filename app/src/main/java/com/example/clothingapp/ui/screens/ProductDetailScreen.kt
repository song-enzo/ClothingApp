package com.example.clothingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clothingapp.ui.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, viewModel: ProductViewModel, productId: Int) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("款式详情", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFD4A853))
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = {
                            viewModel.deleteProduct(product)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F12))
            )
        },
        containerColor = Color(0xFF1A1A1E)
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("未找到该款式", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 图片展示
                if (product.imagePaths.isNotEmpty()) {
                    AsyncImage(
                        model = product.imagePaths[0],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // 基本信息卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("款式编号", product.code, Color(0xFFD4A853))
                        DetailRow("款式名称", product.name.ifEmpty { "未命名" })
                        DetailRow("生产工厂", product.factoryName.ifEmpty { "未填写" })
                        DetailRow("添加时间", product.getFormattedDate())
                    }
                }

                // 面料明细
                Text("面料明细", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.fabricNames.forEachIndexed { index, name ->
                            if (name.isNotEmpty()) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(name, color = Color.White)
                                    Text("¥${String.format("%.2f", product.fabricPrices.getOrNull(index) ?: 0.0)}", color = Color.White)
                                }
                            }
                        }
                    }
                }

                // 成本明细
                Text("成本明细", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("工价", "¥${String.format("%.2f", product.laborCost)}")
                        DetailRow("烫和纽扣", "¥${String.format("%.2f", product.ironingAndButtons)}")
                        DetailRow("辅料费用", "¥${String.format("%.2f", product.accessories)}")
                        Divider(color = Color(0xFF3A3A3C), modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("综合成本价", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("¥${String.format("%.2f", product.getTotalCost())}", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                // 备注
                if (product.notes.isNotEmpty()) {
                    Text("备注", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
                    ) {
                        Text(product.notes, color = Color.White, modifier = Modifier.padding(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
