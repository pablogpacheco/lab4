package it.polito.mas.lab3.fragments.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ProfileFragment : Fragment() {

    private lateinit var image: ImageView
    private lateinit var fullName: TextView
    private lateinit var nickname: TextView
    private lateinit var email: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var editButton: Button

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        fullName = view.findViewById(R.id.name)
        nickname = view.findViewById(R.id.nickname)
        email = view.findViewById(R.id.email)
        phoneNumber= view.findViewById(R.id.phoneNumber)
        image = view.findViewById(R.id.imageView)
        editButton = view.findViewById(R.id.editButton)

        //Set Image
        loadImageFromStorage()

        val sharedPref = activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

        //Check if the precences contain the "profile" key:
        if (sharedPref?.contains("profile") == true){

            val deserializeString = sharedPref.getString("profile", "")
            val deserializeJson = JSONObject(deserializeString?:"")

            fullName.text = "Full name: ${deserializeJson.optString("name")}"
            nickname.text = "Nickname: ${deserializeJson.optString("nickname")}"
            email.text = "Email: ${deserializeJson.optString("email")}"
            phoneNumber.text = "Phone number: ${deserializeJson.optString("phoneNumber")}"
        }
        else{
            fullName.text = "Full name: none"
            nickname.text = "Nickname: none"
            email.text = "Email: none"
            phoneNumber.text = "Phone number: none"
        }

        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_edit_profileFragment)
        }

        return view
    }

    private fun loadImageFromStorage() {
        try {
            val cw = ContextWrapper(context?.applicationContext)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val f = File(directory, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            image.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}