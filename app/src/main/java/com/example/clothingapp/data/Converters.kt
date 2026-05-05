package com.example.clothingapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromFabricList(value: List<FabricItem>?): String = gson.toJson(value)

    @TypeConverter
    fun toFabricList(value: String): List<FabricItem> {
        val listType = object : TypeToken<List<FabricItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromProcessList(value: List<ProcessItem>?): String = gson.toJson(value)

    @TypeConverter
    fun toProcessList(value: String): List<ProcessItem> {
        val listType = object : TypeToken<List<ProcessItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
