package com.example.clothingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clothingapp.ui.ProductViewModel

@Composable
fun AddProductScreen(navController: NavController, viewModel: ProductViewModel) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var factoryName by remember { mutableStateOf("") }
    var fabricNames by remember { mutableStateOf(listOf("", "", "", "")) }
    var fabricPrices by remember { mutableStateOf(listOf(0.0, 0.0, 0.0, 0.0)) }
    var laborCost by remember { mutableStateOf("") }
    var ironingAndButtons by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var imagePaths by remember { mutableStateOf(listOf("")) }

    val totalCost = (fabricPrices.sum() +
            (laborCost.toDoubleOrNull() ?: 0.0) +
            (ironingAndButtons.toDoubleOrNull() ?: 0.0) +
            (accessories.toDoubleOrNull() ?: 0.0))

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
            Text("添加新款", color = Color(0xFFEBEBF0), fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    if (code.isNotEmpty() && name.isNotEmpty() && factoryName.isNotEmpty()) {
                        viewModel.addProduct(
                            code = code,
                            name = name,
                            factoryName = factoryName,
                            fabricNames = fabricNames,
                            fabricPrices = fabricPrices,
                            laborCost = laborCost.toDoubleOrNull() ?: 0.0,
                            ironingAndButtons = ironingAndButtons.toDoubleOrNull() ?: 0.0,
                            accessories = accessories.toDoubleOrNull() ?: 0.0,
                            notes = notes,
                            imagePaths = imagePaths.filter { it.isNotEmpty() }
                        )
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A853))
            ) {
                Text("保存", color = Color(0xFF1A1A1E), fontWeight = FontWeight.Bold)
            }
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Basic info
            FormField("编号", code) { code = it }
            FormField("名称", name) { name = it }
            FormField("工厂", factoryName) { factoryName = it }

            // Fabrics
            Text("面料明细", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            repeat(4) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormField(
                        "面料${i + 1}名称",
                        fabricNames.getOrNull(i) ?: "",
                        modifier = Modifier.weight(1f)
                    ) { value ->
                        fabricNames = fabricNames.toMutableList().apply { set(i, value) }
                    }
                    FormField(
                        "价格",
                        fabricPrices.getOrNull(i)?.toString() ?: "",
                        modifier = Modifier.weight(0.5f)
                    ) { value ->
                        fabricPrices = fabricPrices.toMutableList().apply {
                            set(i, value.toDoubleOrNull() ?: 0.0)
                        }
                    }
                }
            }

            // Costs
            FormField("工价", laborCost) { laborCost = it }
            FormField("烫和纽扣", ironingAndButtons) { ironingAndButtons = it }
            FormField("辅料", accessories) { accessories = it }

            // Total cost display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2C2C2E))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("综合成本价", color = Color(0xFFEBEBF0))
                Text(
                    "¥${String.format("%.2f", totalCost)}",
                    color = Color(0xFFD4A853),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Notes
            FormField("备注", notes, singleLine = false) { notes = it }

            // Image URLs
            Text("图片链接", color = Color(0xFFD4A853), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            imagePaths.forEachIndexed { i, path ->
                FormField(
                    "图片${i + 1}",
                    path
                ) { value ->
                    imagePaths = imagePaths.toMutableList().apply { set(i, value) }
                }
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, color = Color(0xFF8E8E93), fontSize = 10.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E))
                .padding(8.dp),
            textStyle = TextStyle(color = Color(0xFFEBEBF0), fontSize = 12.sp),
            singleLine = singleLine
        )
    }
}
