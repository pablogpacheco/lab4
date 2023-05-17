package it.polito.mas.lab3.fragments.list

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import it.polito.mas.lab3.MainActivity
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.ReservationViewModel
import it.polito.mas.lab3.fragments.add.AddFragment
import java.text.SimpleDateFormat
import java.util.*

//Home fragment of our application:
class ListFragment : Fragment() {

    private lateinit var calendarView: CustomCalendarView
    private lateinit var sportSelected : Spinner
    //private lateinit var currentCalendar: Calendar

    //The fragment has to return its view tree:
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        sportSelected = view.findViewById(R.id.deporte)
        calendarView = view.findViewById(R.id.calendar_view)
        //currentCalendar = Calendar.getInstance(Locale.getDefault())

        /*
        // Observar los cambios en el ciclo de vida de la vista
        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { lifecycleOwner ->
            // Cuando la vista esté creada
            lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun onCreate() {
                    // Obtener los argumentos de navegación
                    val selectedDateLong = arguments?.getLong("selected_date_res")
                    val selectedDateColor = arguments?.getInt("selected_date_color")
                    val selectedDate = selectedDateLong?.let { Date(it) }
                    val reservedDates = arguments?.getSerializable(ARG_RESERVED_DATES) as? List<Date>
                    //para todas las fechas de reservedDates agregar un decorator o para cadda selectedDay

                    // Si hay una fecha y un color válidos
                    if (selectedDate != null && selectedDateColor != null) {

                        val decorators = mutableListOf<DayDecorator>()
                        // Crear un decorador de día con el color correspondiente
                        reservedDates?.forEach { date ->
                            decorators.add(object: DayDecorator {
                                override fun decorate(view: DayView?) {
                                if (view?.date == date) {
                                    view.setBackgroundColor(selectedDateColor)
                                }
                            }
                            })
                    }

                        calendarView.setDecorators(decorators)
                        calendarView.refreshCalendar(currentCalendar)

                        // Borrar los argumentos de navegación para que no se vuelvan a utilizar
                        arguments?.remove("selected_date_res")
                        arguments?.remove("selected_date_color")
                    }
                }
            })
        }
         */

        return view
    }

    //Function to specify action when the view has been created:
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        sport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                //val sportName = parent.getItemAtPosition(position).toString()
                selectedSport = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
         */

        //When we press a date, we going to the add reservation fragment:
        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {

                //When we select a sport, we need to save our choice:
                val sportChosen = sportSelected.selectedItem.toString()

                //Trick to parse the date for the check:
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val current = formatter.format(Date())
                val today = formatter.parse(current)

                //If we select a date previous of today, we throw an exception
                if (date != null && date.before(today)){
                    Toast.makeText( requireContext(), "Error: Date is not valid. Try Again.", Toast.LENGTH_SHORT).show()
                }
                else {
                    date?.let {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        val dateString = dateFormat.format(date)

                        val args = bundleOf(
                            "selected_date" to dateString,
                            "chosen_sport" to sportChosen
                        )
                        findNavController().navigate(R.id.action_listFragment_to_addFragment, args)
                    }
                }
            }

            //Do we need this?
            override fun onMonthChanged(monthStartDate: Date?) {
                // Do something when the month changes
                }
            }
        )

    }
}



