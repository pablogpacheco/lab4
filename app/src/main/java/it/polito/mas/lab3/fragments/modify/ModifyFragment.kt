package it.polito.mas.lab3.fragments.modify

import android.annotation.SuppressLint
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
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.ParseException
import java.text.SimpleDateFormat

class ModifyFragment : Fragment() {

    private val vm by viewModels<ReservationViewModel>()

    //Variable for our EditText:
    private lateinit var reservationID : TextView
    private lateinit var reservationUser: EditText
    private lateinit var reservationSport: EditText
    private lateinit var reservationDate: EditText
    private lateinit var reservationSlot: EditText
    private lateinit var reservationCity: EditText
    private lateinit var reservationCourt: EditText

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

    private val courtList = listOf("Court 1", "Court 2", "Court 3", "Court 4")
    private val cityList = listOf("Turin", "Milan", "Rome", "Naples", "Florence")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_modify, container, false)

        vm.getAll()

        //Take back your reservation:
        val myID = arguments?.getLong("reservation_id") ?: 0
        val myUsername = arguments?.getString("reservation_username") ?: ""
        val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        val mySlot = arguments?.getInt("reservation_slot") ?: 0
        val myCity = arguments?.getString("reservation_city") ?: ""
        val myCourt = arguments?.getString("reservation_court") ?: ""

        //Link the views:
        reservationID = view.findViewById(R.id.detail_id)
        reservationUser = view.findViewById(R.id.detail_user)
        reservationSport = view.findViewById(R.id.detail_sport)
        reservationDate = view.findViewById(R.id.detail_date)
        reservationSlot = view.findViewById(R.id.detail_slot)
        reservationCity = view.findViewById(R.id.detail_city)
        reservationCourt = view.findViewById(R.id.detail_court)

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
        reservationCity.setText(myCity)
        reservationCourt.setText(myCourt)

        return view
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //Take back your reservation:
        val myID = arguments?.getLong("reservation_id") ?: 0
        val myUsername = arguments?.getString("reservation_username") ?: ""
        val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        val mySlot = arguments?.getInt("reservation_slot") ?: 0
        val myCity = arguments?.getString("reservation_city") ?: ""
        val myCourt = arguments?.getString("reservation_court") ?: ""
        val myQuality = arguments?.getInt("reservation_quality") ?: 0
        val myService = arguments?.getInt("reservation_service") ?: 0
        val myReview = arguments?.getString("reservation_review") ?: ""

        val reservationOld = Reservation(
            myID,
            myUsername,
            mySport,
            dateFormat.parse(myDate)!!,
            mySlot,
            myCity,
            myCourt,
            myQuality,
            myService,
            myReview
        )
        var newSlot = 0

        saveChange.setOnClickListener {

            if (checkDate()) {

                var checkValidUpdate = true
                var checkValidSlot = false
                var checkValidCity = false
                var checkValidCourt = false

                for ((index, element) in slotsList.withIndex()) {
                    if (element == reservationSlot.text.toString()) {
                        newSlot = index + 1
                        checkValidSlot = true
                    }
                }

                for (element in courtList) {
                    if(element == reservationCourt.text.toString()) {
                        checkValidCourt = true
                    }
                }

                for (element in cityList){
                    if (element == reservationCity.text.toString()){
                        checkValidCity = true
                    }
                }

                if (vm.everyData.value != null) {
                    for (element in vm.everyData.value!!) {
                        if (element.date == dateFormat.parse(reservationDate.text.toString()) &&
                            element.slot == newSlot &&
                            element.sport_category == reservationSport.text.toString() &&
                            element.city == reservationCity.text.toString() &&
                            element.court == reservationCourt.text.toString()
                        ) {
                            checkValidUpdate = false
                        }
                    }
                }

                if (reservationSport.text.toString() != "Football" &&
                    reservationSport.text.toString() != "Basketball" &&
                    reservationSport.text.toString() != "Tennis" &&
                    reservationSport.text.toString() != "Padel" &&
                    reservationSport.text.toString() != "Volleyball"
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Error. Sport not valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!checkValidCourt) {
                    Toast.makeText(
                        requireContext(),
                        "Error. Court not valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (!checkValidSlot) {
                    Toast.makeText(
                        requireContext(),
                        "Error. Slot not valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (!checkValidCity) {
                    Toast.makeText(
                        requireContext(),
                        "Error. City not valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (checkValidUpdate) {
                    vm.updateReservation(reservationOld,
                        Reservation(
                            myID,
                            reservationUser.text.toString(),
                            reservationSport.text.toString(),
                            dateFormat.parse(reservationDate.text.toString()),
                            newSlot,
                            reservationCity.text.toString(),
                            reservationCourt.text.toString(),
                            myQuality,
                            myService,
                            myReview
                        )
                    )
                    vm.getNameBased(myUsername)
                    vm.getSportBased(
                        dateFormat.parse(reservationDate.text.toString())!!,
                        reservationSport.text.toString(),
                        reservationCity.text.toString(),
                        reservationCourt.text.toString()
                    )
                    //vm.getSportBased(dateFormat.parse(myDate)!!, mySport)
                    val args = bundleOf(
                        "my_username" to myUsername,
                    )
                    Toast.makeText(
                        requireContext(),
                        "Data updated correctly.",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(
                        R.id.action_modifyFragment_to_calendarFragment,
                        args
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error. Update stopped because of date/slot conflict.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        deleteChange.setOnClickListener {

            //If you click on a reservation, you go in editing the details:
            val args = bundleOf(
                "reservation_id" to myID,
                "reservation_username" to myUsername,
                "reservation_sport" to mySport,
                "reservation_date" to myDate,
                "reservation_slot" to mySlot,
                "reservation_city" to myCity,
                "reservation_court" to myCourt,
                "reservation_quality" to myQuality,
                "reservation_service" to myService,
                "reservation_review" to myReview
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

    @SuppressLint("SimpleDateFormat")
    private fun checkDate() : Boolean{

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var check = false

        try {
            dateFormat.parse(reservationDate.text.toString())
            check = true
        } catch (pe : ParseException){
            Toast.makeText(
                requireContext(),
                "Error. Invalid Date.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return check
    }

}