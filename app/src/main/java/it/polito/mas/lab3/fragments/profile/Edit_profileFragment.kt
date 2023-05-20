package it.polito.mas.lab3.fragments.profile

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import it.polito.mas.lab3.R
import org.json.JSONObject


class edit_profileFragment : Fragment() {

    lateinit var nameField: EditText
    lateinit var nicknameField: EditText
    lateinit var emailField: EditText
    lateinit var phoneNumberField: EditText
    lateinit var saveChanges: Button
    lateinit var imageButton: ImageButton
    lateinit var cancelButton: Button
    lateinit var imageView: ImageView

    //Camera
    var frame: ImageView? = null
    var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    val IMAGE_CAPTURE_CODE = 654


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 112)
            }
        }

        //Saved image
        loadImageFromStorage()

        imageButton = view.findViewById(R.id.imageButton)
        nameField = view.findViewById(R.id.nameField)
        nicknameField = view.findViewById(R.id.nicknameField)
        emailField = view.findViewById(R.id.emailField)
        phoneNumberField = view.findViewById(R.id.phoneNumberField)
        saveChanges=view.findViewById(R.id.saveChanges)
        frame = view.findViewById(R.id.imageView)
        cancelButton = view.findViewById(R.id.cancelButton)
        //To open a context menu by clicking on a button, you have to register the button
        registerForContextMenu(imageButton)

        //Handle event click on the button
        imageButton.setOnClickListener {
            //Open the context menu
            //openContextMenu(it)
            openContextMenu(imageButton)    //Gallery or Camera
        }

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_edit_profileFragment_to_profileFragment2)
        }


        saveChanges.setOnClickListener {

            val sharedPref = this.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
            val deserializeString = sharedPref.getString("profile", "")
            val deserializeJson = JSONObject(deserializeString?:"")

            //Edit the SharedPreferences:
            with(sharedPref.edit()){

                //Create the JSON Object:
                val jsonObj = JSONObject()
                if(nameField.text.isNotBlank()) {
                    jsonObj.put("name", nameField.text.toString())
                }else{
                    jsonObj.put("name", "${deserializeJson.optString("name")}")

                }

                if(nicknameField.text.isNotBlank()) {
                    jsonObj.put("nickname", nicknameField.text.toString())
                }else{
                    jsonObj.put("nickname", "${deserializeJson.optString("nickname")}")

                }

                if(emailField.text.isNotBlank()) {
                    jsonObj.put("email", emailField.text.toString())
                }else{
                    jsonObj.put("email", "${deserializeJson.optString("email")}")

                }

                if(phoneNumberField.text.isNotBlank()) {
                    jsonObj.put( "phoneNumber", phoneNumberField.text.toString())
                }else{
                    jsonObj.put("phoneNumber", "${deserializeJson.optString("phoneNumber")}")

                }

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

            val intent = Intent(this, ShowProfileActivity::class.java)
            //intent.putExtras(bundle)
        return view
    }


}