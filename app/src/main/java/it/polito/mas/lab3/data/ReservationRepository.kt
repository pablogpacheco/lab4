package it.polito.mas.lab3.data

import android.app.Application
import java.util.*

class ReservationRepository(application: Application) {

    //Call the DAO of the corresponding database:
    private val reservationDao = ReservationDatabase.getDatabase(application).reservationDao()

    //Here we get all the data:
    fun getAllData() : List<Reservation> {
        return reservationDao.readAllData()
    }

    fun addReservation(reservation: Reservation){
        reservationDao.addReservation(reservation)
    }

    fun getReservationsByDateAndSport(date: Date, sport_category: String, city: String, court: String) : List<Reservation> {
         return reservationDao.getReservationsByDateAndSport(date,sport_category, city, court)
    }

    fun getReservationsByName(username: String) : List<Reservation>{
        return reservationDao.getReservationByName(username)
    }

    fun deleteReservation(id: Long) {
        reservationDao.deleteReservation(id)
    }

    fun updateData(reservation: Reservation){
        reservationDao.updateReservation(reservation.id!!,
            reservation.username!!,
            reservation.sport_category!!,
            reservation.date!!,
            reservation.slot!!,
            reservation.city!!,
            reservation.court!!,
            reservation.quality_value!!,
            reservation.service_value!!,
            reservation.review!!
            )
    }
}