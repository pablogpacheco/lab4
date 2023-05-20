package it.polito.mas.lab3.fragments.category

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import it.polito.mas.lab3.R.*
import it.polito.mas.lab3.data.ReservationViewModel
import java.util.*


class CategoryFragment : Fragment() {

    private lateinit var enterButton : Button
    private lateinit var myUsername: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_category, container, false)

        enterButton = view.findViewById(R.id.enter_button)
        myUsername = view.findViewById(R.id.edit_username)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vm by viewModels<ReservationViewModel>()

        enterButton.setOnClickListener {

            //Check if the username is valid, then proceed to the ModifyFragment:
            val userString = myUsername.text.toString();

            if (userString != ""){

                //Prepare the bundle:
                val args = bundleOf(
                    "my_username" to userString,
                )

                findNavController().navigate(R.id.action_categoryFragment_to_calendarFragment, args)
            }
            else{
                Toast.makeText( requireContext(),
                    "Error: You must enter a Username.",
                    Toast.LENGTH_SHORT).show()
            }

        }

    }
}