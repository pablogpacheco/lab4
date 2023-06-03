package it.polito.mas.lab3.data.user

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "phonenumber") val phoneNumber: Int,
    @ColumnInfo(name = "sport") val sport: String,
    @ColumnInfo(name = "level") val level: String,
    @ColumnInfo(name = "experience") val experience: String,
    @ColumnInfo(name = "cityfav") val city: String,
    @ColumnInfo(name = "weekdayfav") val weekday: String,
    @ColumnInfo(name = "slotfav") val slotfav: String
    )
