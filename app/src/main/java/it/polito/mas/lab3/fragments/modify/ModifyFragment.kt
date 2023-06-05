package it.polito.mas.lab3.fragments.modify

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class ModifyFragment : Fragment() {

    private val vm by viewModels<ReservationViewModel>()

    //Variable for our EditText:
    private lateinit var reservationUser: EditText
    private lateinit var reservationSport: Spinner
    private lateinit var reservationDate: EditText
    private lateinit var reservationSlot: Spinner
    private lateinit var reservationCity: Spinner
    private lateinit var reservationCourt: Spinner

    //Buttons:
    private lateinit var saveChange: Button
    private lateinit var deleteChange: Button
    private lateinit var backFromChange: Button

    private val courtList = listOf("Court 1", "Court 2", "Court 3", "Court 4")
    private val cityList = listOf("Turin", "Milan", "Rome", "Naples", "Florence")
    private val sportList=listOf("Football", "Basketball", "Tennis", "Volleyball", "Padel")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_modify, container, false)

        vm.getAll()

        //Take back your reservation:
        val myUsername = arguments?.getString("reservation_username") ?: ""
        //val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        //val mySlot = arguments?.getInt("reservation_slot") ?: 0
        //val myCity = arguments?.getString("reservation_city") ?: ""
        //val myCourt = arguments?.getString("reservation_court") ?: ""

        //Link the views:
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
        reservationUser.setText(myUsername)
        //reservationSport.(mySport)
        reservationDate.setText(myDate)
        //reservationSlot.setText(slotsList[mySlot-1])
        //reservationCourt.setText(myCourt)

        return view
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //Take back your reservation:
        val myUsername = arguments?.getString("reservation_username") ?: ""
        val mySport = arguments?.getString("reservation_sport") ?: ""
        val myDate = arguments?.getString("reservation_date") ?: ""
        val mySlot = arguments?.getInt("reservation_slot") ?: 0
        val myCity = arguments?.getString("reservation_city") ?: ""
        val myCourt = arguments?.getString("reservation_court") ?: ""
        val myQuality = arguments?.getInt("reservation_quality") ?: 0
        val myService = arguments?.getInt("reservation_service") ?: 0
        val myReview = arguments?.getString("reservation_review") ?: ""

        //look for the position in the spinner of the previous selection,
        //this way you can display the previous selected option on the spinner
        val courtPosition=courtList.indexOf(myCourt)
        val cityPosition=cityList.indexOf(myCity)
        val sportPosition=sportList.indexOf(mySport)

        //displaying on the spinner the previous selection of the user
        reservationSlot.setSelection(mySlot-1)
        reservationCity.setSelection(cityPosition)
        reservationCourt.setSelection(courtPosition)
        reservationSport.setSelection(sportPosition)

        val reservationOld = Reservation(
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

        saveChange.setOnClickListener {
            val selectedSlotPosition = reservationSlot.selectedItemPosition
            val selectedCourtPosition = reservationCourt.selectedItemPosition

            if (checkDate()) {

                var checkValidUpdate = true
                var checkValidCity = false
                var checkValidCourt = false

                /*
               for ((index, element) in slotsList.withIndex()) {
                    if (element == reservationSlot.selectedItem.toString()) {
                        newSlot = index + 1
                        checkValidSlot = true
                    }
                }
                 */


                for (element in courtList) {
                    if(element == reservationCourt.selectedItem.toString()) {
                        checkValidCourt = true
                    }
                }

                for (element in cityList){
                    if (element == reservationCity.selectedItem.toString()){
                        checkValidCity = true
                    }
                }

                if (vm.everyData.value != null) {
                    for (element in vm.everyData.value!!) {
                        if (element.date == dateFormat.parse(reservationDate.text.toString()) &&
                            element.slot == selectedSlotPosition &&
                            element.sport_category == reservationSport.selectedItem.toString() &&
                            element.city == reservationCity.selectedItem.toString() &&
                            element.court == reservationCourt.selectedItem.toString()

                        ) {
                            checkValidUpdate = false
                        }
                    }
                }

                if (reservationSport.selectedItem.toString() != "Football" &&
                    reservationSport.selectedItem.toString() != "Basketball" &&
                    reservationSport.selectedItem.toString() != "Tennis" &&
                    reservationSport.selectedItem.toString() != "Padel" &&
                    reservationSport.selectedItem.toString() != "Volleyball"
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
                } else if (!checkValidCity) {
                    Toast.makeText(
                        requireContext(),
                        "Error. City not valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (checkValidUpdate) {
                    vm.deleteReservation(reservationOld)
                    vm.addReservation( Reservation(
                            reservationUser.text.toString(),
                            reservationSport.selectedItem.toString(),
                            dateFormat.parse(reservationDate.text.toString()),
                            selectedSlotPosition + 1,
                            reservationCity.selectedItem.toString(),
                            courtList[selectedCourtPosition],
                            myQuality,
                            myService,
                            myReview
                        )
                    )
                    vm.getNameBased(myUsername)
                    vm.getSportBased(
                        dateFormat.parse(reservationDate.text.toString())!!,
                        reservationSport.selectedItem.toString(),
                        reservationCity.selectedItem.toString(),
                        reservationCourt.selectedItem.toString()
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
                        "Error. Update stopped because of the slot is already occupied.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "Date chosen is invalid.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteChange.setOnClickListener {

            //If you click on a reservation, you go in editing the details:
            val args = bundleOf(
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
        val today = Date()
        var check = false

        try {
            val selDate = dateFormat.parse(reservationDate.text.toString())
            if (selDate != null) {
                check = !selDate.before(today)
            }
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