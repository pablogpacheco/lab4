package it.polito.mas.lab3.data

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

class Converters {
    @TypeConverter fun dateToTimestamp(date: Date?): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }

    @TypeConverter fun fromTimestamp(value: String?): Date? {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(value)
    }
}