package com.example.clothingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "products")
@TypeConverters(Converters::class)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String = "",
    val factoryName: String = "",
    val fabricNames: List<String> = emptyList(),
    val fabricPrices: List<Double> = emptyList(),
    val laborCost: Double = 0.0,
    val ironingAndButtons: Double = 0.0,
    val accessories: Double = 0.0,
    val notes: String = "",
    val imagePaths: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTotalCost(): Double {
        val fabricTotal = fabricPrices.sum()
        return fabricTotal + laborCost + ironingAndButtons + accessories
    }

    fun getFabricTotal(): Double = fabricPrices.sum()

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(createdAt))
    }
}
