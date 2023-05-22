package it.polito.mas.lab3.fragments.add

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import it.polito.mas.lab3.models.Slot
import it.polito.mas.lab3.models.SlotAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

//Fragment to add a reservation or see the details:
class AddFragment : Fragment() {

    //private var control: Int = 1 //All
    private var reservedDates = listOf<Reservation>()
    //private lateinit var selectedSport: String
    //var nameFull: Boolean = false
    //var slotTouched: Boolean = false

    //Let's declare the viewModel:
    private val vm by viewModels<ReservationViewModel>()

    private lateinit var fechaReserva: TextView
    private lateinit var sport_selected: TextView
    private lateinit var nombreUsuario: EditText
    private lateinit var city: Spinner
    private lateinit var court: Spinner
    private lateinit var botonAgregarReserva: Button
    private lateinit var backButton : Button

    private val slotsList = listOf(
        Slot(1, "8:00", "9:00"),
        Slot(2, "9:00", "10:00"),
        Slot(3, "10:00", "11:00"),
        Slot(4, "11:00", "12:00"),
        Slot(5, "12:00", "13:00"),
        Slot(6, "13:00", "14:00"),
        Slot(7, "14:00", "15:00"),
        Slot(8, "15:00", "16:00"),
        Slot(9, "16:00", "17:00"),
        Slot(10, "17:00", "18:00"),
        Slot(11, "18:00", "19:00"),
        Slot(12, "19:00", "20:00"),
        Slot(13, "20:00", "21:00"),
    )
    private lateinit var recyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter
    private var selectedItem: Int? = null

    //private lateinit var reservedDates: List<Date>

    //Function to display our data:
    @SuppressLint("MissingInflatedId", "FragmentLiveDataObserve", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        //Spinner for the court and the city:
        city = view.findViewById(R.id.city)
        court = view.findViewById(R.id.court)

        val cityString = city.selectedItem.toString()
        val courtString = court.selectedItem.toString()

        val selectedDateString = arguments?.getString("selected_date") ?: ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val selectedDate = dateFormat.parse(selectedDateString)
        val selectedSportString = arguments?.getString("chosen_sport") ?: ""
        vm.getSportBased(selectedDate!!, selectedSportString, cityString, courtString)

        //A TextView for the date and one for the selected sport:
        fechaReserva = view.findViewById(R.id.fecha_reserva)
        sport_selected = view.findViewById(R.id.sport_selected)

        //Name of the user that wants to reserve the court:
        nombreUsuario = view.findViewById(R.id.nombre_usuario)

        //Button to add the reservation and one to go back:
        botonAgregarReserva = view.findViewById(R.id.boton_agregar_reserva)
        backButton = view.findViewById(R.id.back_button)

        //Where we display the time slots:
        recyclerView = view.findViewById(R.id.recyclerView)

        slotAdapter = SlotAdapter(viewLifecycleOwner, slotsList, vm)
        recyclerView.adapter = slotAdapter

        // Establecer un LayoutManager para el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        vm.getAll()

        vm.everyData.observe(this){
            reservedDates = it
        }

        return view
    }

    //When the view tree has been created...
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get the string of the date and put in the TextView:
        val selectedDateString = arguments?.getString("selected_date") ?: ""
        selectedDateString.let {
            //val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            //val selectedDate = dateFormat.parse(selectedDateString)
            fechaReserva.text = getString(R.string.fecha_reserva, selectedDateString)
        }

        //Get the selected sport and put it in the TextView:
        val selectedSportString = arguments?.getString("chosen_sport") ?: ""
        selectedSportString.let {
            sport_selected.text = getString(R.string.selected_sport, selectedSportString)
        }

        //Change immediately the slots in case of a change on the city or the court:
        city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val selectedDate = dateFormat.parse(selectedDateString)

                val cityChosen = parent.getItemAtPosition(position).toString()
                val courtChosen = court.selectedItem.toString()

                vm.getSportBased(selectedDate!!, selectedSportString, cityChosen, courtChosen)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        court.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val selectedDate = dateFormat.parse(selectedDateString)

                val courtChosen = parent.getItemAtPosition(position).toString()
                val cityChosen = city.selectedItem.toString()

                vm.getSportBased(selectedDate!!, selectedSportString, cityChosen, courtChosen)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //Slot for the RecyclerView: we save the slot id.
        slotAdapter.setOnItemClickListener(object : SlotAdapter.OnItemClickListener {
            override fun onItemClick(slot: Slot) {
                val selectedItemId = slot.id
                // Hacer lo que necesites con el ID del elemento seleccionado
                // Crear una reserva con los valores del formulario Agragar más
                selectedItem = selectedItemId
            }
        })

        //In the add button we save everything and pass it to the ViewModel:
        val agregarReservaButton = view.findViewById<Button>(R.id.boton_agregar_reserva)
        agregarReservaButton.setOnClickListener {

            // Obtener los valores de los campos del formulario
            val nombre = nombreUsuario.text.toString()
            val cityChosen = city.selectedItem.toString()
            val court = court.selectedItem.toString()

            //Date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val selectedDate = dateFormat.parse(selectedDateString)

            //Añadir a la bbdd
            val reserva = Reservation(null, nombre, selectedSportString, selectedDate, selectedItem, cityChosen, court)

            if (reserva.username == ""){
                Toast.makeText(
                    requireContext(),
                    "Error. Must Insert a name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(reserva.city == null) {
                Toast.makeText(
                    requireContext(),
                    "Error. Must select a city.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(reserva.court == null) {
                Toast.makeText(
                    requireContext(),
                    "Error. Must select a court.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (reserva.slot == null){
                Toast.makeText(
                    requireContext(),
                    "Error. Must Select a slot.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if (vm.filteredData.value?.any{
                    it.slot == reserva.slot
                } == true){
                Toast.makeText(
                    requireContext(),
                    "Error. Slot already occupied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                // Crear un bloque de código coroutine
                lifecycleScope.launch {

                    // Llamar a la función addReservation y pasar la reserva como argumento
                    vm.addReservation(reserva)
                }

                // Mostrar un mensaje de éxito
                Toast.makeText(
                    requireContext(),
                    "Reservation successfully added",
                    Toast.LENGTH_SHORT
                ).show()

                // Calcular el color que quieres asignarle al día correspondiente
                //val color = Color.parseColor("#02FF00")
                //val selectedDateLong= selectedDate!!.time

                val args = Bundle().apply {
                    //putLong("selected_date_res", selectedDateLong)
                    //putInt("selected_date_color", color)
                    putSerializable("reserved_dates", ArrayList(reservedDates))
                }

                findNavController().navigate(R.id.action_addFragment_to_listFragment, args)
            }

        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }

        // Resto del código para el fragmento
    }
}