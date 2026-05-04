package com.example.clothingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clothingapp.data.Product
import com.example.clothingapp.ui.ProductViewModel

@Composable
fun ProductDetailScreen(navController: NavController, viewModel: ProductViewModel, productId: Int) {
    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(productId) {
        // In a real app, you would fetch the product from the repository
        // For now, we'll get it from the products list
        val products = viewModel.products.collectAsState().value
        product = products.find { it.id == productId }
    }

    if (product == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1E)),
            contentAlignment = Alignment.Center
        ) {
            Text("加载中...", color = Color(0xFF8E8E93))
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1E))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F0F12))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFD4A853))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    product!!.code,
                    color = Color(0xFFD4A853),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(product!!.name, color = Color(0xFFEBEBF0), fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = {
                viewModel.deleteProduct(product!!)
                navController.popBackStack()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF3B30))
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            if (product!!.imagePaths.isNotEmpty()) {
                AsyncImage(
                    model = product!!.imagePaths[0],
                    contentDescription = product!!.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Basic info
            DetailSection("基本信息") {
                DetailRow("编号", product!!.code)
                DetailRow("名称", product!!.name)
                DetailRow("工厂", product!!.factoryName)
            }

            // Fabrics
            DetailSection("面料明细") {
                product!!.fabricNames.forEachIndexed { i, name ->
                    DetailRow(
                        "面料${i + 1}: $name",
                        "¥${String.format("%.2f", product!!.fabricPrices.getOrNull(i) ?: 0.0)}"
                    )
                }
                DetailRow("小计", "¥${String.format("%.2f", product!!.getFabricTotal())}")
            }

            // Costs
            DetailSection("工艺费用") {
                DetailRow("工价", "¥${String.format("%.2f", product!!.laborCost)}")
                DetailRow("烫和纽扣", "¥${String.format("%.2f", product!!.ironingAndButtons)}")
                DetailRow("辅料", "¥${String.format("%.2f", product!!.accessories)}")
            }

            // Total cost
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2C2C2E))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("综合成本价", color = Color(0xFFEBEBF0))
                    Text(
                        "¥${String.format("%.2f", product!!.getTotalCost())}",
                        color = Color(0xFFD4A853),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Notes
            if (product!!.notes.isNotEmpty()) {
                DetailSection("备注") {
                    Text(product!!.notes, color = Color(0xFFEBEBF0), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C2C2E))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        content()
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 12.sp)
        Text(
            value,
            color = Color(0xFFEBEBF0),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
