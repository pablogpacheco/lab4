package it.polito.mas.lab3.fragments.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mas.lab3.EditProfileActivity
import it.polito.mas.lab3.LoginActivity
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.ReservationViewModel
import it.polito.mas.lab3.data.user.UserViewModel
import it.polito.mas.lab3.fragments.my_reservation.CalendarFragment
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ProfileFragment : Fragment() {

    private lateinit var image: ImageView

    private lateinit var userName: TextView
    private lateinit var fullName: TextView
    private lateinit var age: TextView
    private lateinit var gender: TextView

    private lateinit var email: TextView
    private lateinit var phoneNumber: TextView

    private lateinit var sport: TextView
    private lateinit var skills: TextView
    private lateinit var prevExp: TextView

    private lateinit var favCity: TextView
    private lateinit var favDay: TextView
    private lateinit var favSlot: TextView


    private lateinit var editButton: Button

    private lateinit var auth : FirebaseAuth
    private lateinit var logoutButton: Button

    private val vm by viewModels<UserViewModel>()

    val db = Firebase.firestore

    companion object {
        const val TAG = "FirestoreApp"
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser


    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val emailAddress = currentUser?.email

        vm.getUser(emailAddress!!)

        vm.user.observe(viewLifecycleOwner) { user ->
            fullName.text = "Full name: ${user?.name?:""}"
            if(user?.age==0) {
                age.text = ""
            }else {
                age.text = "Age: ${user?.age?:""}"
            }
            gender.text = "Gender: ${user?.gender?:""}"
            userName.text = "Username: ${user?.username?:""}"


            email.text = "Email: ${user?.email?:""}"
            if(user?.phoneNumber==0){
                phoneNumber.text = ""
            }else {
                phoneNumber.text = "Phone number: ${user?.phoneNumber ?: ""}"
            }

            skills.text = "Skills level: ${user?.level?:""}"
            sport.text = "Favourite sport: ${user?.sport?:""}"
            prevExp.text = "Previous Experience: ${user?.experience?:""}"

            favCity.text = "City for playing: ${user?.city?:""}"
            favDay.text = "Favourite day for a game: ${user?.weekday?:""}"
            favSlot.text = "Favourite hour for a game: ${user?.slotfav?:""}"
        }

        fullName = view.findViewById(R.id.name)
        age = view.findViewById(R.id.age)
        gender = view.findViewById(R.id.gender)
        userName = view.findViewById(R.id.username)


        email = view.findViewById(R.id.email)
        phoneNumber= view.findViewById(R.id.phoneNumber)

        sport= view.findViewById(R.id.fav_sport)
        skills = view.findViewById(R.id.skills)
        prevExp = view.findViewById(R.id.previous_exp)

        favCity = view.findViewById(R.id.city)
        favDay = view.findViewById(R.id.day)
        favSlot = view.findViewById(R.id.timeslot)

        image = view.findViewById(R.id.imageView)
        editButton = view.findViewById(R.id.editButton)

        auth = FirebaseAuth.getInstance()
        logoutButton = view.findViewById(R.id.signOutBtn)


        //Set Image
        loadImageFromStorage()

        /*
        val sharedPref = activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

        //Check if the precences contain the "profile" key:
        if (sharedPref?.contains("profile") == true){

            val deserializeString = sharedPref.getString("profile", "")
            val deserializeJson = JSONObject(deserializeString?:"")

            fullName.text = "Full name: ${deserializeJson.optString("name")}"
            userName.text = "Username: ${deserializeJson.optString("username")}"
            age.text = "Age: ${deserializeJson.optString("age")}"
            gender.text = "Gender: ${deserializeJson.optString("gender")}"

            email.text = "Email: ${deserializeJson.optString("email")}"
            phoneNumber.text = "Phone number: ${deserializeJson.optString("phoneNumber")}"

            skills.text = "Skills level: ${deserializeJson.optString("skills")}"
            sport.text = "Favourite sport: ${deserializeJson.optString("favourite_sport")}"
            prevExp.text = "Previous Experience: ${deserializeJson.optString("prev_exp")}"

            favCity.text = "City for playing: ${deserializeJson.optString("fav_city")}"
            favDay.text = "Favourite day for a game: ${deserializeJson.optString("fav_day")}"
            favSlot.text = "Favourite hour for a game: ${deserializeJson.optString("fav_slot")}"
        }
        else{
            fullName.text = "Full name: none"
            userName.text = "Username: none"
            age.text = "Age: none"
            gender.text = "Gender: none"

            email.text = "Email: none"
            phoneNumber.text = "Phone number: none"

            skills.text = "Skills level: none"
            sport.text = "Favourite sport: none"
            prevExp.text = "Previous Experience: none"

            favCity.text = "City for playing: none"
            favDay.text = "Favourite day for a game: none"
            favSlot.text = "Favourite hour for a game: none"
        }


         */
        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_edit_profileFragment)
            //startActivity(Intent(requireContext(), EditProfileActivity::class.java))

        }

        logoutButton.setOnClickListener{
            Firebase.auth.signOut();
            startActivity(Intent(requireContext(), LoginActivity::class.java))
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