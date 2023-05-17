package it.polito.mas.lab3.data

import android.content.Context
import androidx.room.*


@Database (entities = [Reservation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReservationDatabase: RoomDatabase () {

    abstract fun reservationDao() : ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: ReservationDatabase? = null

        //checking if instance already exists
        fun getDatabase (context: Context): ReservationDatabase = (

            //Check if database exists, else create a new instance:
            INSTANCE ?: synchronized(this){

                //Check if after acquiring the lock the database was created by another thread.
                //Else, build it.
                val instance = INSTANCE?:Room.databaseBuilder (
                    context.applicationContext,
                    ReservationDatabase::class.java,
                    "reservation_database"
                        ).build()
                INSTANCE = instance
                INSTANCE
            }
                )!!
    }
}