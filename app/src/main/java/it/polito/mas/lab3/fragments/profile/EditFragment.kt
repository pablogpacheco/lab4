package it.polito.mas.lab3.fragments.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.ReservationViewModel
import it.polito.mas.lab3.data.user.User
import it.polito.mas.lab3.data.user.UserViewModel
import org.json.JSONObject
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class EditFragment : Fragment() {

    private lateinit var nameField: EditText
    private lateinit var usernameField: EditText
    private lateinit var ageField: EditText
    private lateinit var genderField: Spinner

    lateinit var phoneNumberField: EditText

    lateinit var sport: Spinner
    private lateinit var skillsField: Spinner
    private lateinit var prevExpField: EditText

    private lateinit var favCity: Spinner
    private lateinit var favDay: Spinner
    private lateinit var favSlot: Spinner

    lateinit var saveChanges: Button
    lateinit var imageButton: ImageButton
    lateinit var cancelButton: Button

    //Camera
    private var frame: ImageView? = null
    var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    val IMAGE_CAPTURE_CODE = 654

    val db = Firebase.firestore

    companion object {
        const val TAG = "FirestoreApp"
    }

    //Let's declare the viewModel:
    private val vm by viewModels<UserViewModel>()

    private val currentUser = FirebaseAuth.getInstance().currentUser

    //SPINNERS
    private val genderList=listOf("Male", "Female", "--")
    private val cityList = listOf("Turin", "Milan", "Rome", "Naples", "Florence")
    private val sportList=listOf("Football", "Basketball", "Tennis", "Volleyball", "Padel")
    private val levelList=listOf("1° level", "2° level", "3° level", "4° level", "5° level")
    private val dayList=listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    private val slotsList = listOf("1->8:00-9:00",
        "2->9:00-10:00",
        "3->10:00-11:00",
        "4->11:00-12:00",
        "5->12:00-13:00",
        "6->13:00-14:00",
        "7->14:00-15:00",
        "8->15:00-16:00",
        "9->16:00-17:00",
        "10->17:00-18:00",
        "11->18:00-19:00",
        "12->19:00-20:00",
        "13->20:00-21:00",
    )

    @SuppressLint("ObsoleteSdkInt", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val emailAddress = currentUser?.email

        //Ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(permission, 112)
            }
        }

        imageButton = view.findViewById(R.id.imageButton)

        nameField = view.findViewById(R.id.nameField)
        usernameField = view.findViewById(R.id.usernameField)
        ageField = view.findViewById(R.id.ageField)
        genderField = view.findViewById(R.id.genderField)

        val emailField = view.findViewById<TextView>(R.id.emailField)
        emailField.text = currentUser?.email
        phoneNumberField = view.findViewById(R.id.phoneNumberField)

        sport = view.findViewById(R.id.favourite_sport)
        skillsField = view.findViewById(R.id.skillsField)
        prevExpField = view.findViewById(R.id.prevExpField)

        favCity = view.findViewById(R.id.cityField)
        favDay = view.findViewById(R.id.dayField)
        favSlot = view.findViewById(R.id.slotField)

        saveChanges = view.findViewById(R.id.saveChanges)
        frame = view.findViewById(R.id.imageView)
        cancelButton = view.findViewById(R.id.cancelButton)

        //Get from firebase User
        vm.getUser(emailAddress!!)

        vm.user.observe(viewLifecycleOwner) { user ->
            nameField .setText(user?.name?:"")
            usernameField.setText(user?.username?:"")
            if(user?.age==0) {
                ageField.setText("")
            }else {
                ageField.setText("${user?.age?:""}")
            }
            if(user?.phoneNumber==0) {
                phoneNumberField.setText("")
            }else {
                phoneNumberField.setText("${user?.phoneNumber ?: ""}")
            }
            emailField.text = user?.email?:""
            prevExpField.setText(user?.experience?:"")


            val genderPosition=genderList.indexOf("${user?.gender}")
            val cityPosition=cityList.indexOf("${user?.city}")
            val sportPosition=sportList.indexOf("${user?.sport}")
            val levelPosition=levelList.indexOf("${user?.level}")
            val slotPosition=slotsList.indexOf("${user?.slotfav}")
            val dayPosition=dayList.indexOf("${user?.weekday}")

            Log.d(TAG, genderPosition.toString())

            genderField.setSelection(genderPosition)
            favCity.setSelection(cityPosition)
            sport.setSelection(sportPosition)
            skillsField.setSelection(levelPosition)
            favSlot.setSelection(slotPosition-1)
            favDay.setSelection(dayPosition)
        }

        //Saved image
        loadImageFromStorage()

        /*
        val sharedPref = activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

        //Check if the precences contain the "profile" key:
        if (sharedPref?.contains("profile") == true){

            val deserializeString = sharedPref.getString("profile", "")
            val deserializeJson = JSONObject(deserializeString?:"")

            nameField.setText(deserializeJson.optString("name"))
            usernameField.setText(deserializeJson.optString("username"))
            ageField.setText(deserializeJson.optString("age"))

            emailField.setText(deserializeJson.optString("email"))
            phoneNumberField.setText(deserializeJson.optString("phoneNumber"))

            prevExpField.setText(deserializeJson.optString("prev_exp"))
        }

         */

        //To open a context menu by clicking on a button, you have to register the button
        registerForContextMenu(imageButton)

        //Handle event click on the button
        imageButton.setOnClickListener {
            //Open the context menu
            //openContextMenu(it)
            activity?.openContextMenu(imageButton)    //Gallery or Camera
        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_edit_profileFragment_to_profileFragment)
        }

        saveChanges.setOnClickListener {

            val email = emailField.text.toString()?:null
            val username = usernameField.text.toString()?:null
            val name = nameField.text.toString()?:null
            val age = if(ageField.text.toString() != "")
                ageField.text.toString().toInt()
            else
                null
            val gender = genderField.selectedItem.toString()?:null
            val phoneNumber = if (phoneNumberField.text.toString() != "")
                phoneNumberField.text.toString().toInt()
            else
                null
            val sport = sport.selectedItem.toString()?:null
            val level = skillsField.selectedItem.toString()?:null
            val experience = prevExpField.text.toString()?:null
            val city = favCity.selectedItem.toString()?:null
            val weekday = favDay.selectedItem.toString()?:null
            val slotfav = favSlot.selectedItem.toString()?:null

            if (age != null && age >= 80) {
                Toast.makeText(
                    requireContext(),
                    "Error. Age must be below 80.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                if (username != null) {
                    vm.checkUsernameAvailability(username) { isAvailable ->
                        if (isAvailable) {
                            val user = User(
                                email, username, name, age, gender, phoneNumber, sport, level,
                                experience, city, weekday, slotfav
                            )
                            vm.updateUser(user)

                            findNavController().navigate(R.id.action_edit_profileFragment_to_profileFragment)
                        } else {
                            val context: Context = requireContext()
                            Toast.makeText(
                                context,
                                "This username is not present in our database, please choose another.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }
        return view
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.camera -> {

                //Ask for permission to open camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                        val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, 121)
                    }
                }
                openCamera()

                return true
            }
            R.id.gallery -> {

                //Choose an image from the gallery:
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
                return true
            }

            else -> return super.onContextItemSelected(item)

        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            //imageView.setImageURI(image_uri);
            val bitmap = uriToBitmap(image_uri!!)
            frame?.setImageBitmap(bitmap)
            saveToInternalStorage(bitmap!!)
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            image_uri = data.data
            //imageView.setImageURI(image_uri);
            val bitmap = uriToBitmap(image_uri!!)
            frame?.setImageBitmap(bitmap)
            saveToInternalStorage(bitmap!!)
        }
    }

    //TODO takes URI of the image and returns bitmap
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //TODO save image to local filesystem
    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(context?.applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        println(directory.absolutePath)
        return directory.absolutePath
    }

    private fun loadImageFromStorage() {
        try {
            val cw = ContextWrapper(context?.applicationContext)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val f = File(directory, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            frame?.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}