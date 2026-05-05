package com.example.clothingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FabricItem(
    val name: String = "",
    val meters: Double = 0.0,
    val pricePerMeter: Double = 0.0
) {
    val total: Double get() = meters * pricePerMeter
}

data class ProcessItem(
    val name: String = "",
    val cost: Double = 0.0
)

@Entity(tableName = "products")
@TypeConverters(Converters::class)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String = "",
    val factoryName: String = "",
    val fabrics: List<FabricItem> = emptyList(),
    val processes: List<ProcessItem> = emptyList(),
    val laborCost: Double = 0.0,
    val ironingAndButtons: Double = 0.0,
    val accessories: List<ProcessItem> = emptyList(), // 辅料也改为动态列表
    val notes: String = "",
    val imagePaths: List<String> = emptyList(),
    val mainImageIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTotalCost(): Double {
        val fabricTotal = fabrics.sumOf { it.total }
        val processTotal = processes.sumOf { it.cost }
        val accessoryTotal = accessories.sumOf { it.cost }
        return fabricTotal + processTotal + accessoryTotal + laborCost + ironingAndButtons
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(createdAt))
    }

    fun getMainImagePath(): String? {
        return if (imagePaths.isNotEmpty()) {
            imagePaths.getOrNull(mainImageIndex) ?: imagePaths[0]
        } else null
    }
}
