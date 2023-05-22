package it.polito.mas.lab3.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "reservation_table")
data class Reservation (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long ?,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "sport_category") val sport_category: String?,
    @ColumnInfo(name = "date") @TypeConverters(Converters::class) val date: Date?,
    @ColumnInfo(name = "slot") val slot: Int?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "court") val court: String?
)