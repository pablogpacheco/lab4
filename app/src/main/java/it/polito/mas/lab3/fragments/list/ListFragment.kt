package it.polito.mas.lab3.fragments.list

import android.annotation.SuppressLint
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
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.DayDecorator
import com.stacktips.view.DayView
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

//Home fragment of our application:
class ListFragment : Fragment() {

    private lateinit var calendarView: CustomCalendarView
    private lateinit var sportSelected : Spinner
    private lateinit var currentCalendar: Calendar
    private lateinit var selectedSport: String

    //private var control: Int = 2 //All
    private val modifyReservationViewModel by viewModels<ReservationViewModel>()
    private lateinit var reservedDates: List<Reservation>

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
        currentCalendar = Calendar.getInstance(Locale.getDefault())
        //val selectedDate = Date(4/5/2023)
        reservedDates = listOf()
        selectedSport ="Football" //Default

        modifyReservationViewModel.getAll()

        // Observar los cambios en el ciclo de vida de la vista
        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { lifecycleOwner ->
            // Cuando la vista esté creada
            // Obtener los argumentos de navegación

            //val selectedDateLong = arguments?.getLong("selected_date_res")
            //val selectedDateColor = arguments?.getInt("selected_date_color")
            //val selectedDate = selectedDateLong?.let { Date(it) }
            if (arguments?.getSerializable("reserved_dates") != null)
                reservedDates = arguments?.getSerializable("reserved_dates") as List<Reservation>

            //para todas las fechas de reservedDates agregar un decorator o para cadda selectedDay

            /*
            modifyReservationViewModel = ViewModelProvider(this,
                ModifyFragment.ViewModelFactory(
                    requireActivity().application,
                    requireContext(),
                    control,
                    "user",
                    selectedDate
                )
            ).get(ReservationViewModel::class.java
             */

            modifyReservationViewModel.everyData.observe(
                viewLifecycleOwner
            ) { reservation ->
                reservedDates = reservation
                val numSlotsMax = 7

                // Crear un mapa que asocie cada fecha con el número de slots reservados
                val reservedColor = Color.parseColor("#FFFF00")
                val allReservedColor = Color.parseColor("#FF0000")

                val decorators = mutableListOf<DayDecorator>()
                // Si hay una fecha y un color válidos

                // Crear un decorador de día con el color correspondiente
                decorators.add(object : DayDecorator {
                    override fun decorate(view: DayView?) {
                        if (reservedDates.isNotEmpty()) {
                            for (reservedDate in reservedDates) {
                                val reservedCalendar = Calendar.getInstance().apply {
                                    time = reservedDate.date!!
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val viewCalendar = Calendar.getInstance().apply {
                                    time = view?.date!!
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val numReservedSlots = getNumReservedSlotsByDateAndSport(
                                    reservedDates, selectedSport,viewCalendar.time)

                                if (reservedCalendar.compareTo(viewCalendar) == 0 && reservedDate.sport_category == selectedSport ) {
                                    if (numReservedSlots>=numSlotsMax){
                                        view?.setBackgroundColor(allReservedColor)
                                    }else {
                                        view?.setBackgroundColor(reservedColor)
                                    }
                                }
                            }

                        }
                    }


                })

                calendarView.decorators = decorators
                calendarView.refreshCalendar(currentCalendar)
            }


            /*GlobalScope.launch {
            reservedDates =
                ReservationDatabase.getDatabase(requireContext()).reservationDao()
                    .getAllReservation()

            }

             */


            // Borrar los argumentos de navegación para que no se vuelvan a utilizar
            arguments?.remove("selected_date_res")
            arguments?.remove("selected_date_color")


        }

        return view
    }

    fun getNumReservedSlotsByDateAndSport(reservedDates: List<Reservation>, sport: String, date: Date): Int {
        // Obtener todas las reservas para la fecha dada
        val reservasEnFecha = reservedDates.filter { it.date == date }
        // Filtrar las reservas según el deporte seleccionado
        val reservasDelDeporte = reservasEnFecha.filter { it.sport_category == sport }
        // Contar el número de slots reservados
        val numSlotsReservados = reservasDelDeporte.count()
        return numSlotsReservados
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
            @SuppressLint("SimpleDateFormat")
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



