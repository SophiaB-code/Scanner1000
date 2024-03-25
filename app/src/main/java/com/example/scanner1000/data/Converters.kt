package com.example.scanner1000.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSharedWithList(value: List<Int>): String {
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun toSharedWithList(value: String): List<Int> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toInt() }
    }
}
