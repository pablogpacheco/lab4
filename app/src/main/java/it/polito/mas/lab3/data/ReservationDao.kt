package it.polito.mas.lab3.data

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface ReservationDao {

    //Method for the addition in the database:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addReservation(reservation: Reservation)

    //Retrieve all the data:
    @Query( "SELECT * FROM reservation_table ORDER BY id ASC" )
    fun readAllData(): List<Reservation>

    //Get the reservation based on the sport:
    @Query("SELECT * FROM reservation_table WHERE date = :date AND sport_category = :sport_category")
    fun getReservationsByDateAndSport(date: Date, sport_category: String): List<Reservation>

    //Get reservations based on the current user:
    @Query("SELECT * FROM reservation_table WHERE username= :username")
    fun getReservationByName(username: String) : List<Reservation>

    @Query("UPDATE reservation_table " +
            "SET username = :username," +
            "sport_category = :sport_category," +
            "date = :date," +
            "slot = :slot " +
            "WHERE id = :id")
    fun updateReservation(id:Long, username: String, sport_category: String,date: Date, slot: Int)

    //Delete a reservation:
    @Query("DELETE FROM reservation_table WHERE id = :id")
    fun deleteReservation(id: Long)

}