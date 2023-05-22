package it.polito.mas.lab3.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mas.lab3.R

class ReservationAdapter : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    var reservations: List<Reservation> = emptyList()
    private var listener: OnItemClickListener? = null

    private val slotsList = listOf("8:00-9:00",
        "9:00-10:00",
        "10:00-11:00",
        "11:00-12:00",
        "12:00-13:00",
        "13:00-14:00",
        "14:00-15:00",
        "15:00-16:00",
        "16:00-17:00",
        "17:00-18:00",
        "18:00-19:00",
        "19:00-20:00",
        "20:00-21:00",
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_list_item, parent, false)
        return ReservationViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(reservation: Reservation)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val currentReservation = reservations[position]
        holder.bind(currentReservation)

        //Set a OnClickListener to pass to the other fragment:
        holder.itemView.setOnClickListener{
            listener?.onItemClick(currentReservation)
        }
    }

    fun setData(reservation: List<Reservation>){
        this.reservations = reservation
    }

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //val reservationDate: TextView = itemView.findViewById(R.id.reservation_date)
        val reservationSport: TextView = itemView.findViewById(R.id.reservation_sport)
        val reservationSlot: TextView = itemView.findViewById(R.id.reservation_slot)
        val reservationCity: TextView = itemView.findViewById(R.id.reservation_city)
        val reservationCourt: TextView = itemView.findViewById(R.id.reservation_court)

        @SuppressLint("SetTextI18n")
        fun bind(reservation:Reservation){

            //reservationDate.text=reservation.date.toString()
            reservationSport.text=reservation.sport_category
            reservationCity.text = reservation.city
            reservationCourt.text=reservation.court

            if(reservation.slot == null){
                reservationSlot.text="null"
            }
            else {
                reservationSlot.text = slotsList[reservation.slot-1]
            }
        }
    }

    override fun getItemCount()=reservations.size
}

