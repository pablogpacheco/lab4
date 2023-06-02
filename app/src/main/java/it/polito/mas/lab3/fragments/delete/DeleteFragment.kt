package it.polito.mas.lab3.fragments.delete

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

class DeleteFragment : Fragment() {

    private lateinit var officialDelete: Button
    private lateinit var rollBack: Button

    private val vm by viewModels<ReservationViewModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_delete, container, false)

        officialDelete = view.findViewById(R.id.button_ok)
        rollBack = view.findViewById(R.id.button_no)

        return view
    }

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

        val myReservation = Reservation(
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

        officialDelete.setOnClickListener {
            vm.deleteReservation(myReservation)
            vm.getNameBased(myUsername)
            vm.getSportBased(dateFormat.parse(myDate)!!, mySport, myCity, myCourt)
            val args = bundleOf(
                "my_username" to myUsername,
            )
            findNavController().navigate(R.id.action_deleteFragment_to_calendarFragment, args)
        }

        rollBack.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val dateReser = formatter.parse(myDate)

            val today = Date()
            if (dateReser.before(today)) {
                val args = bundleOf(
                    "my_username" to myUsername,
                )
                findNavController().navigate(R.id.action_deleteFragment_to_calendarFragment, args)
            } else {
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
                findNavController().navigate(R.id.action_deleteFragment_to_modifyFragment, args)
            }
        }
    }
}