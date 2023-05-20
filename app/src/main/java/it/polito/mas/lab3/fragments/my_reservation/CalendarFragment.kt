package it.polito.mas.lab3.fragments.my_reservation

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationAdapter
import it.polito.mas.lab3.data.ReservationViewModel
import it.polito.mas.lab3.models.Slot
import it.polito.mas.lab3.models.SlotAdapter
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment(), ReservationAdapter.OnItemClickListener {

    private lateinit var calendarView: CustomCalendarView
    private lateinit var displayUser : TextView
    private lateinit var backToUser: Button
    private lateinit var recyclerView : RecyclerView
    private lateinit var reservationAdapter: ReservationAdapter

    private val vm by viewModels<ReservationViewModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        //Display the user:
        val myUser = arguments?.getString("my_username") ?: ""
        displayUser = view.findViewById(R.id.display_user)
        displayUser.text = getString(R.string.display_user, myUser)

        //Call the function to update the LiveData in the viewModel:
        vm.getNameBased(myUser)

        //Set the calendar and color the days where there's a reservation:
        val decorators : MutableList<DayDecorator> = mutableListOf()
        val currentCalendar = Calendar.getInstance(Locale.getDefault())

        calendarView = view.findViewById(R.id.calendar_view)

        //Define the RecyclerView:
        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        reservationAdapter = ReservationAdapter()
        reservationAdapter.setOnItemClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        vm.filteredByNameData.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                decorators.add(ValidDate(it))
                calendarView.decorators = decorators
                calendarView.refreshCalendar(currentCalendar)
            }
        }

        backToUser = view.findViewById(R.id.backToUser)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myUser = arguments?.getString("my_username") ?: ""

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val actualDate = dateFormat.parse(dateFormat.format(date))
                val actualList = mutableListOf<Reservation>()

                //Check if the list of the user for this date contains reservations:
                if (vm.filteredByNameData.value != null) {
                    if (vm.filteredByNameData.value!!.isNotEmpty()) {
                        for (element in vm.filteredByNameData.value!!) {
                            if(element.date == actualDate){
                                actualList.add(element)
                            }
                        }

                        //Set the adapter:
                        if (actualList.isEmpty()){
                            Toast.makeText(
                                requireContext(),
                                "No reservation present in this date.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else{
                            reservationAdapter.setData(actualList)
                            recyclerView.adapter = reservationAdapter
                        }
                    }
                }
            }

            override fun onMonthChanged(monthStartDate: Date?) {
                // Do something when the month changes
            }
        })

        backToUser.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_categoryFragment)
        }

    }

    override fun onItemClick(reservation: Reservation) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //If you click on a reservation, you go in editing the details:
        val args = bundleOf(
            "reservation_id" to reservation.id,
            "reservation_username" to reservation.username,
            "reservation_sport" to reservation.sport_category,
            "reservation_date" to dateFormat.format(reservation.date),
            "reservation_slot" to reservation.slot,
        )
        findNavController().navigate(R.id.action_calendarFragment_to_modifyFragment, args)
    }

}

private class ValidDate (val list: List<Reservation>): DayDecorator {
    override fun decorate(p0: DayView?) {

        //Parse the date:
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateString = dateFormat.format(p0?.date)
        val realDate = dateFormat.parse(dateString)
        val color_text = Color.BLACK

        for (element in list){
            if (element.date == realDate){
                val color = Color.GREEN
                p0?.setBackgroundColor(color)
                p0?.setTextColor(color_text)
            }
            else{
                val color = Color.RED
                p0?.setBackgroundColor(color)
                p0?.setTextColor(color_text)
            }
        }
    }
}