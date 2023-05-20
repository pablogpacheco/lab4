package it.polito.mas.lab3.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Date
import kotlin.concurrent.thread

class ReservationViewModel(application: Application): AndroidViewModel(application) {

    //Value for the repo in order to call the APIs:
    private val repository = ReservationRepository(application)

    //All the data from the database:
    private val mutableEveryData = MutableLiveData<List<Reservation>>()
    val everyData: LiveData<List<Reservation>> get() = mutableEveryData

    //LiveData for Date and Sport selection:
    private val mutableFilteredList = MutableLiveData<List<Reservation>>()
    val filteredData : LiveData<List<Reservation>> get() = mutableFilteredList

    //LiveData for username selection:
    private val mutableNameList = MutableLiveData<List<Reservation>>()
    val filteredByNameData : LiveData<List<Reservation>> get() = mutableNameList

    fun addReservation(reservation: Reservation){
        thread {
            repository.addReservation(reservation)
        }
    }

    fun getAll(){
        thread {
            mutableEveryData.postValue(repository.getAllData())
        }
    }

    fun getSportBased(date: Date, sport: String){
        thread {
            mutableFilteredList.postValue(repository.getReservationsByDateAndSport(date, sport))
        }
    }

    fun getNameBased(username: String){
        thread {
            mutableNameList.postValue(repository.getReservationsByName(username))
        }
    }

    fun updateReservation(reservation: Reservation){
        thread {
            repository.updateData(reservation)
        }
    }

    fun deleteReservation(id: Long){
        thread {
            repository.deleteReservation(id)
        }
    }

}