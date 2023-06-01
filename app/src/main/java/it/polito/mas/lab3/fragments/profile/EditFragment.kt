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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
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

    lateinit var emailField: EditText
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


    @SuppressLint("ObsoleteSdkInt", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

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

        emailField = view.findViewById(R.id.emailField)
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

        //Saved image
        loadImageFromStorage()

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

            val deserializeString = sharedPref?.getString("profile", "")
            val deserializeJson = if (deserializeString != ""){
                JSONObject(deserializeString!!)
            } else{
                JSONObject()
            }


            //Edit the SharedPreferences:
            with(sharedPref.edit()) {

                //Create the JSON Object:
                val jsonObj = JSONObject()

                if (nameField.text.isNotBlank()) {
                    jsonObj.put("name", nameField.text.toString())
                } else {
                    jsonObj.put("name", deserializeJson.optString("name"))
                }
                if (usernameField.text.isNotBlank()) {
                    jsonObj.put("username", usernameField.text.toString())
                } else {
                    jsonObj.put("username", deserializeJson.optString("username"))

                }
                if (ageField.text.isNotBlank()) {
                    jsonObj.put("age", ageField.text.toString())
                } else {
                    jsonObj.put("age", deserializeJson.optString("age"))
                }
                val genderChosen = genderField.selectedItem.toString()
                jsonObj.put("gender", genderChosen)

                if (emailField.text.isNotBlank()) {
                    jsonObj.put("email", emailField.text.toString())
                } else {
                    jsonObj.put("email", deserializeJson.optString("email"))

                }
                if (phoneNumberField.text.isNotBlank()) {
                    jsonObj.put("phoneNumber", phoneNumberField.text.toString())
                } else {
                    jsonObj.put("phoneNumber", deserializeJson.optString("phoneNumber"))

                }

                val sportChosen = sport.selectedItem.toString()
                jsonObj.put("favourite_sport", sportChosen)
                val skillChosen = skillsField.selectedItem.toString()
                jsonObj.put("skills", skillChosen)
                if (prevExpField.text.isNotBlank()) {
                    jsonObj.put("prev_exp", prevExpField.text.toString())
                } else {
                    jsonObj.put("prev_exp", deserializeJson.optString("prev_exp"))
                }

                val cityChosen = favCity.selectedItem.toString()
                jsonObj.put("fav_city", cityChosen)
                val dayChosen = favDay.selectedItem.toString()
                jsonObj.put("fav_day", dayChosen)
                val slotChosen = favSlot.selectedItem.toString()
                jsonObj.put("fav_slot", slotChosen)

                //Serialize the JSON:
                val jsonString = jsonObj.toString()

                //Put the string in the sharedPref:
                this?.putString("profile", jsonString)
                this?.apply()
            }

            //val bundle = Bundle()
            //bundle.putString("name", nameField.text.toString())
            //bundle.putString("nickname", nicknameField.text.toString())
            //bundle.putString("email", emailField.text.toString())
            //bundle.putString("phoneNumber", phoneNumberField.text.toString())

            //val intent = Intent(this, ShowProfileActivity::class.java)
            //intent.putExtras(bundle)
            findNavController().navigate(R.id.action_edit_profileFragment_to_profileFragment)

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