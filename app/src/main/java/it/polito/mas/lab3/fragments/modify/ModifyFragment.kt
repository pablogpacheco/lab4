package it.polito.mas.lab3.fragments.modify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationAdapter
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.SimpleDateFormat

class ModifyFragment : Fragment() {

    private val vm by viewModels<ReservationViewModel>()

    //Variable for our EditText:
    private lateinit var reservationID : TextView
    private lateinit var reservationUser: EditText
    private lateinit var reservationSport: EditText
    private lateinit var reservationDate: EditText
    private lateinit var reservationSlot: EditText

    //Buttons:
    private lateinit var saveChange: Button
    private lateinit var deleteChange: Button
    private lateinit var backFromChange: Button

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_modify, container, false)

        //Take back your reservation:
        val myID = arguments?.getLong("reservation_id") ?: 0
        val myUsername = arguments?.getString("reservation_username") ?: ""
        val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        val mySlot = arguments?.getInt("reservation_slot") ?: 0

        //Link the views:
        reservationID = view.findViewById(R.id.detail_id)
        reservationUser = view.findViewById(R.id.detail_user)
        reservationSport = view.findViewById(R.id.detail_sport)
        reservationDate = view.findViewById(R.id.detail_date)
        reservationSlot = view.findViewById(R.id.detail_slot)

        //Link the buttons:
        saveChange = view.findViewById(R.id.save_change)
        deleteChange = view.findViewById(R.id.delete)
        backFromChange = view.findViewById(R.id.backToRes)

        //Change the appearance of the views:
        reservationID.text = myID.toString()
        reservationUser.setText(myUsername)
        reservationSport.setText(mySport)
        reservationDate.setText(myDate)
        reservationSlot.setText(slotsList[mySlot-1])

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vm by viewModels<ReservationViewModel>()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //Take back your reservation:
        val myID = arguments?.getLong("reservation_id") ?: 0
        val myUsername = arguments?.getString("reservation_username") ?: ""
        val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        val mySlot = arguments?.getInt("reservation_slot") ?: 0

        var newSlot = 0

        for ((index, element) in slotsList.withIndex()) {
            if (element == reservationSlot.text.toString())
                newSlot = index
        }

        saveChange.setOnClickListener {

            vm.readAll()

            val check = vm.updateReservation(Reservation(myID,
                reservationUser.text.toString(),
                reservationSport.text.toString(),
                dateFormat.parse(reservationDate.text.toString()),
                newSlot))
            if (check) {
                vm.getNameBased(myUsername)
                vm.getSportBased(dateFormat.parse(reservationDate.text.toString()), reservationSport.text.toString())
                vm.getSportBased(dateFormat.parse(myDate), mySport)
                val args = bundleOf(
                    "my_username" to myUsername,
                )
                findNavController().navigate(R.id.action_modifyFragment_to_calendarFragment, args)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "Error. Update stopped because of date/slot conflict.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteChange.setOnClickListener {

            //If you click on a reservation, you go in editing the details:
            val args = bundleOf(
                "reservation_id" to myID,
                "reservation_username" to myUsername,
                "reservation_sport" to mySport,
                "reservation_date" to myDate,
                "reservation_slot" to mySlot
            )
            findNavController().navigate(R.id.action_modifyFragment_to_deleteFragment, args)
        }

        backFromChange.setOnClickListener {
            val args = bundleOf(
                "my_username" to myUsername,
            )
            findNavController().navigate(R.id.action_modifyFragment_to_calendarFragment, args)
        }
    }

}