package com.example.clothingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clothingapp.ui.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, viewModel: ProductViewModel, productId: Int) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }
    var showFullScreenImage by remember { mutableStateOf(false) }
    var initialImageIndex by remember { mutableStateOf(0) }

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
                        IconButton(onClick = { navController.navigate("add?productId=${product.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFD4A853))
                        }
                        IconButton(onClick = {
                            viewModel.deleteProduct(product)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
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
                // 图片展示（点击放大）
                if (product.imagePaths.isNotEmpty()) {
                    AsyncImage(
                        model = product.getMainImagePath(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { 
                                initialImageIndex = product.mainImageIndex
                                showFullScreenImage = true 
                            },
                        contentScale = ContentScale.Crop
                    )
                    Text("点击图片查看全部照片 (${product.imagePaths.size}张)", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // 基本信息
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("款式编号", product.code, Color(0xFFD4A853))
                        DetailRow("款式名称", product.name.ifEmpty { "未命名" })
                        DetailRow("生产工厂", product.factoryName.ifEmpty { "未填写" })
                        DetailRow("添加时间", product.getFormattedDate())
                    }
                }

                // 面料明细
                if (product.fabrics.isNotEmpty()) {
                    SectionTitle("面料明细")
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.fabrics.forEach { fabric ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(fabric.name, color = Color.White, modifier = Modifier.weight(1f))
                                    Text("${fabric.meters}米 × ¥${fabric.pricePerMeter}", color = Color.Gray, fontSize = 12.sp)
                                    Text("¥${String.format("%.2f", fabric.total)}", color = Color.White, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }
                }

                // 工艺与辅料
                if (product.processes.isNotEmpty() || product.accessories.isNotEmpty() || product.laborCost > 0 || product.ironingAndButtons > 0) {
                    SectionTitle("工艺与费用")
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.processes.forEach { DetailRow(it.name, "¥${String.format("%.2f", it.cost)}") }
                            product.accessories.forEach { DetailRow(it.name, "¥${String.format("%.2f", it.cost)}") }
                            if (product.laborCost > 0) DetailRow("工价", "¥${String.format("%.2f", product.laborCost)}")
                            if (product.ironingAndButtons > 0) DetailRow("烫和纽扣", "¥${String.format("%.2f", product.ironingAndButtons)}")
                            
                            Divider(color = Color(0xFF3A3A3C), modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("综合成本价", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("¥${String.format("%.2f", product.getTotalCost())}", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                }

                // 备注
                if (product.notes.isNotEmpty()) {
                    SectionTitle("备注")
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))) {
                        Text(product.notes, color = Color.White, modifier = Modifier.padding(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // 全屏图片查看器
    if (showFullScreenImage && product != null) {
        FullScreenImagePager(
            images = product.imagePaths,
            initialIndex = initialImageIndex,
            onDismiss = { showFullScreenImage = false }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenImagePager(images: List<String>, initialIndex: Int, onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // 关闭按钮
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            // 页码指示
            Text(
                "${pagerState.currentPage + 1} / ${images.size}",
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
            )
        }
    }
}
