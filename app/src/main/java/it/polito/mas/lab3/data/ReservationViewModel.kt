package it.polito.mas.lab3.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
//import kotlin.concurrent.thread

class ReservationViewModel(application: Application): AndroidViewModel(application) {

    //All the data from the database:
    private val mutableEveryData = MutableLiveData<List<Reservation>>()
    val everyData: LiveData<List<Reservation>> get() = mutableEveryData

    //LiveData for Date and Sport selection:
    private val mutableFilteredList = MutableLiveData<List<Reservation>>()
    val filteredData : LiveData<List<Reservation>> get() = mutableFilteredList

    //LiveData for username selection:
    private val mutableNameList = MutableLiveData<List<Reservation>>()
    val filteredByNameData : LiveData<List<Reservation>> get() = mutableNameList

    //Firestore db
    private val db = Firebase.firestore

    companion object {
        const val TAG = "FirestoreApp"
    }

    fun addReservation(reservation: Reservation){

        val documentId = reservation.username + reservation.sport_category + reservation.city + reservation.date.toString() + reservation.slot.toString()
        db.collection("reservations")
            .document(documentId)
            .set(reservation)
            .addOnSuccessListener {
                Log.d(TAG, "Reserva guardada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al guardar la reserva", e)
            }

    }

    fun getAll(){

        db.collection("reservations")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservationsList = mutableListOf<Reservation>()

                for (document in querySnapshot) {
                    //Take the fields from firebase db
                    val username = document.getString("username")
                    val sportCategory = document.getString("sport_category")
                    val date = document.getDate("date")
                    val slot = document.getLong("slot")?.toInt()
                    val city = document.getString("city")
                    val court = document.getString("court")
                    val qualityValue = document.getLong("quality_value")?.toInt()
                    val serviceValue = document.getLong("service_value")?.toInt()
                    val review = document.getString("review")

                    val reservation = Reservation(
                        username,
                        sportCategory,
                        date,
                        slot,
                        city,
                        court,
                        qualityValue,
                        serviceValue,
                        review
                    )
                    //add the reservation to the list
                    reservationsList.add(reservation)
                }
                mutableEveryData.postValue(reservationsList)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener las reservas", e)
            }
    }


    fun getSportBased(date: Date, sport: String, city: String, court: String){

        db.collection("reservations")
            .whereEqualTo("date", date)
            .whereEqualTo("sport_category", sport)
            .whereEqualTo("city", city)
            .whereEqualTo("court", court)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservationsList = mutableListOf<Reservation>()

                for (document in querySnapshot) {
                    //Take the fields from firebase db
                    val username = document.getString("username")
                    val sportCategory = document.getString("sport_category")
                    val newDate = document.getDate("date")
                    val slot = document.getLong("slot")?.toInt()
                    val newCity = document.getString("city")
                    val newCourt = document.getString("court")
                    val qualityValue = document.getLong("quality_value")?.toInt()
                    val serviceValue = document.getLong("service_value")?.toInt()
                    val review = document.getString("review")

                    val reservation = Reservation(
                        username,
                        sportCategory,
                        newDate,
                        slot,
                        newCity,
                        newCourt,
                        qualityValue,
                        serviceValue,
                        review
                    )
                    //add the reservation to the list
                    reservationsList.add(reservation)
                }
                mutableFilteredList.postValue(reservationsList)
                Log.d(TAG,"Reservations retrieved successfully: ${reservationsList.size} reservations")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener las reservas del usuario", e)
            }
    }

    fun getNameBased(username: String){

        db.collection("reservations")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservationsListOfUser = mutableListOf<Reservation>()

                for (document in querySnapshot) {
                    //Take the fields from firebase db
                    val newUsername = document.getString("username")
                    val sportCategory = document.getString("sport_category")
                    val date = document.getDate("date")
                    val slot = document.getLong("slot")?.toInt()
                    val city = document.getString("city")
                    val court = document.getString("court")
                    val qualityValue = document.getLong("quality_value")?.toInt()
                    val serviceValue = document.getLong("service_value")?.toInt()
                    val review = document.getString("review")

                    val reservation = Reservation(
                        newUsername,
                        sportCategory,
                        date,
                        slot,
                        city,
                        court,
                        qualityValue,
                        serviceValue,
                        review
                    )
                    //add the reservation to the list
                    reservationsListOfUser.add(reservation)
                }
                mutableNameList.postValue(reservationsListOfUser)
                Log.d(TAG, "Reservations retrieved successfully: ${reservationsListOfUser.size} reservations")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener las reservas del usuario", e)
            }
    }

    fun deleteReservation(reservation: Reservation){

        val collectionRef = db.collection("reservations")

        val documentId = reservation.username + reservation.sport_category + reservation.city + reservation.date.toString() + reservation.slot.toString()

        collectionRef.document(documentId)
            .delete()
            .addOnSuccessListener {
                // Documento eliminado exitosamente
                Log.d(TAG, "Documento eliminado: $documentId")
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al eliminar el documento
                Log.e(TAG, "Error al eliminar el documento", e)
            }
    }

}