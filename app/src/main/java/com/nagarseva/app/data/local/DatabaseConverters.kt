package com.nagarseva.app.data.local

import androidx.room.TypeConverter
import java.util.Date

class DatabaseConverters {
    
    // Convert Long timestamp to Date and back
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    // Convert List<String> to JSON string and back
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList()
        else value.split(",")
    }
}
