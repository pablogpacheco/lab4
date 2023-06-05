package it.polito.mas.lab3.data

import java.util.Date

data class Reservation (
    val username: String?,
    val sport_category: String?,
    val date: Date?,
    val slot: Int?,
    val city: String?,
    val court: String?,
    val quality_value: Int?,
    val service_value: Int?,
    val review: String?,
)