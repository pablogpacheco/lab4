package it.polito.mas.lab3.fragments.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polito.mas.lab3.R
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class profileFragment : Fragment() {

    lateinit var fullName: TextView
    lateinit var nickname: TextView
    lateinit var email: TextView
    lateinit var phoneNumber: TextView


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

        //Set Image
        loadImageFromStorage()

        val sharedPref = getSharedPreferences("app_pref", Context.MODE_PRIVATE)

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

        return view
    }

    private fun loadImageFromStorage() {
        try {
            val f = File(filesDir, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img: ImageView = findViewById<View>(R.id.imageView) as ImageView
            img.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}