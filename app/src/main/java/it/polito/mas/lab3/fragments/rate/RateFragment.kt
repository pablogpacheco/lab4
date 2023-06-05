package it.polito.mas.lab3.fragments.rate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.SimpleDateFormat

class RateFragment : Fragment() {
    //variable
    private lateinit var qualityBar: RatingBar
    private lateinit var serviceBar: RatingBar
    private lateinit var review: EditText

    //buttons
    private lateinit var rateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button
    //private  var qualityValue: Int = 0
    //private  var serviceValue: Int = 0

    private val vm by viewModels<ReservationViewModel>()


   override fun onCreateView(inflater: LayoutInflater,
                             container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

       val view = inflater.inflate(R.layout.rate_fragment, container, false)

       vm.getAll()


       //Take back the values of your reservation:

       val myQuality = arguments?.getInt("reservation_quality") ?: 0
       val myService = arguments?.getInt("reservation_service") ?: 0
       val myReview = arguments?.getString("reservation_review") ?: ""

       qualityBar = view.findViewById(R.id.ratingBar)
       serviceBar = view.findViewById(R.id.ratingBar2)
       rateButton = view.findViewById(R.id.rate_ok)
       deleteButton = view.findViewById(R.id.rate_delete)
       backButton = view.findViewById(R.id.rate_back)


       qualityBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
           Toast.makeText(requireContext(), "Calification: $rating", Toast.LENGTH_SHORT).show()
       }
       serviceBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
           Toast.makeText(requireContext(), "Calification: $rating", Toast.LENGTH_SHORT).show()
       }



       review = view.findViewById(R.id.review)
       if (myReview != ""){
           review.setText(myReview)
       }
       if (myQuality != 0){
           qualityBar.rating = myQuality.toFloat()
       }
       if (myService != 0){
           serviceBar.rating = myService.toFloat()
       }





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


        val myReservation = Reservation(
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



        backButton.setOnClickListener {

            val args = bundleOf(
                "my_username" to myUsername,
               )
            findNavController().navigate(R.id.action_rateFragment_to_calendarFragment, args)
        }


        rateButton.setOnClickListener {


            val qualityValue = qualityBar.rating.toInt()
            val serviceValue = serviceBar.rating.toInt()
            val reviews = review.text.toString()

            vm.deleteReservation(myReservation)
            vm.addReservation(Reservation(
                myUsername, mySport, dateFormat.parse(myDate), mySlot,
                myCity, myCourt, qualityValue, serviceValue, reviews
            ))


            vm.getNameBased(myUsername)
            vm.getSportBased(
                dateFormat.parse(myDate)!!,
                mySport,
                myCity,
                myCourt
            )
            val args = bundleOf(
                "my_username" to myUsername,
            )
            Toast.makeText(
                requireContext(),
                "Review added correctly.",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(
                R.id.action_rateFragment_to_calendarFragment,
                 args
            )
        }

        deleteButton.setOnClickListener {

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
            findNavController().navigate(R.id.action_rateFragment_to_deleteFragment, args)
        }

    }
}