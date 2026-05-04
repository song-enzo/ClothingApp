package com.example.clothingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.clothingapp.data.Converters

@Entity(tableName = "products")
@TypeConverters(Converters::class)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String,
    val factoryName: String,
    val fabricNames: List<String>,
    val fabricPrices: List<Double>,
    val laborCost: Double,
    val ironingAndButtons: Double,
    val accessories: Double,
    val notes: String = "",
    val imagePaths: List<String> = emptyList(),
    val createdAt: String = ""
) {
    fun getTotalCost(): Double {
        val fabricTotal = fabricPrices.sum()
        return fabricTotal + laborCost + ironingAndButtons + accessories
    }

    fun getFabricTotal(): Double = fabricPrices.sum()
}
