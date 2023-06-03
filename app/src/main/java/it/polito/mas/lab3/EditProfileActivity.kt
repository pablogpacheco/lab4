package it.polito.mas.lab3

import android.Manifest
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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedDispatcher
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.io.*

class EditProfileActivity : AppCompatActivity() {

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

    //Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //Saved image
        loadImageFromStorage()

        imageButton = findViewById(R.id.imageButton)

        nameField = findViewById(R.id.nameField)
        usernameField = findViewById(R.id.usernameField)
        ageField = findViewById(R.id.ageField)
        genderField = findViewById(R.id.genderField)

        emailField = findViewById(R.id.emailField)
        phoneNumberField = findViewById(R.id.phoneNumberField)

        sport = findViewById(R.id.favourite_sport)
        skillsField = findViewById(R.id.skillsField)
        prevExpField = findViewById(R.id.prevExpField)

        favCity = findViewById(R.id.cityField)
        favDay = findViewById(R.id.dayField)
        favSlot = findViewById(R.id.slotField)

        saveChanges = findViewById(R.id.saveChanges)
        frame = findViewById(R.id.imageView)
        cancelButton = findViewById(R.id.cancelButton)


        registerForContextMenu(imageButton)

        imageButton.setOnClickListener{
            openContextMenu(imageButton)
        }

        cancelButton.setOnClickListener {
            onBackPressed()
        }

        saveChanges.setOnClickListener {

            val sharedPref = this.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
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
                putString("profile", jsonString)
                apply()
            }

            //val bundle = Bundle()
            //bundle.putString("name", nameField.text.toString())
            //bundle.putString("nickname", nicknameField.text.toString())
            //bundle.putString("email", emailField.text.toString())
            //bundle.putString("phoneNumber", phoneNumberField.text.toString())

            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            onBackPressed()
        }

    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menucamera, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(item?.itemId) {
            R.id.camera -> {

                //Ask for permission to open camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                        val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, 121)
                    } else {

                    }
                } else {

                }

                openCamera()

                return true
            }
            R.id.gallery -> {

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
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageProfile = findViewById<ImageView>(R.id.imageView)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            //imageView?.setImageURI(image_uri);
            val bitmap = uriToBitmap(image_uri!!)
            //image_uri = data?.data
            //imageProfile.setImageURI(image_uri)
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
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
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
    open fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        //var fos: FileOutputStream? = null
        val fos = openFileOutput("profile.jpg", Context.MODE_PRIVATE)
        try {
            //fos = FileOutputStream(mypath)
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
            val f = File(filesDir, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img: ImageView = findViewById<View>(R.id.imageView) as ImageView
            img.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val PROFILE_KEY = "profile"
    }
}