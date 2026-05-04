package com.example.clothingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clothingapp.ui.ProductViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: ProductViewModel) {
    val products = viewModel.products.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1E))
    ) {
        // Header with search and add button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F0F12))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFD4A853), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "FC",
                    color = Color(0xFF1A1A1E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Search bar
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF2C2C2E), RoundedCornerShape(2.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF8E8E93),
                    modifier = Modifier.size(16.dp)
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f),
                    textStyle = TextStyle(
                        color = Color(0xFFEBEBF0),
                        fontSize = 12.sp
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                "搜索...",
                                color = Color(0xFF8E8E93),
                                fontSize = 12.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Add button
            IconButton(
                onClick = { navController.navigate("add") },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFD4A853), RoundedCornerShape(2.dp))
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF1A1A1E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Product grid
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无产品",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(product, navController)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: com.example.clothingapp.data.Product, navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color(0xFF2C2C2E), RoundedCornerShape(2.dp))
            .clickable { navController.navigate("detail/${product.id}") }
    ) {
        // Image
        if (product.imagePaths.isNotEmpty()) {
            AsyncImage(
                model = product.imagePaths[0],
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
                    .background(Color(0xFF1A1A1E)),
                contentAlignment = Alignment.Center
            ) {
                Text("无图片", color = Color(0xFF8E8E93), fontSize = 12.sp)
            }
        }

        // Info
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                product.code,
                color = Color(0xFFD4A853),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                product.name,
                color = Color(0xFFEBEBF0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                product.factoryName,
                color = Color(0xFF8E8E93),
                fontSize = 10.sp,
                maxLines = 1
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "成本价",
                    color = Color(0xFF8E8E93),
                    fontSize = 10.sp
                )
                Text(
                    "¥${String.format("%.2f", product.getTotalCost())}",
                    color = Color(0xFFD4A853),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
