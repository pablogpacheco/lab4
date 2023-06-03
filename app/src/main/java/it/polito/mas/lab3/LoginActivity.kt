package it.polito.mas.lab3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mas.lab3.data.ReservationViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var email: String

    //Firestore db
    val db = Firebase.firestore


    companion object {
        const val TAG = "FirestoreApp"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth//FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            .requestIdToken("403230751594-ckki5eactv8eqngmqgnlg8cgop9gr6uq.apps.googleusercontent.com")
            //it should be
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.gSignInBtn).setOnClickListener {
            signInGoogle()

        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        email=account.email.toString()
        saveEmail(email)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    db.collection("users")
                        .whereEqualTo("email", account.email.toString())
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot) {
                                if (document.contains("username")) {
                                    // El usuario ya tiene un nombre de usuario guardado, ir directamente a MainActivity
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // El usuario no tiene un nombre de usuario guardado, mostrar campo de nombre de usuario
                                    val usernameEditText =
                                        findViewById<EditText>(R.id.usernameEditText)
                                    usernameEditText.visibility = View.VISIBLE

                                    val saveButton = findViewById<Button>(R.id.saveButton)
                                    saveButton.visibility = View.VISIBLE

                                    saveButton.setOnClickListener {
                                        val username = usernameEditText.text.toString()
                                        if (username.isNotEmpty()) {
                                            checkUsernameAvailability(username) { isAvailable ->
                                                if (isAvailable) {
                                                    saveUsernameToFirebase(username)
                                                    val intent =
                                                        Intent(this, MainActivity::class.java)
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "El nombre de usuario ya está registrado. Por favor, elige otro.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Ingresa un nombre de usuario",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                            }
                        }
                } else {
                    Log.d(
                        TAG,
                        "No hay current user ======================================================================"
                    )
                    // Manejo del error
                }
            }
        }
    }


    private fun saveUsernameToFirebase(username: String) {

        // Crea un objeto Map para almacenar los datos a actualizar en la base de datos
        val userData = hashMapOf<String, Any>(
            "username" to username
        )

        val documentId = email
        db.collection("users")
            .document(documentId)
            .update(userData)
            .addOnSuccessListener {
                Log.d(ReservationViewModel.TAG, "Username saved")
            }
            .addOnFailureListener { e ->
                Log.e(ReservationViewModel.TAG, "Error in username save", e)
            }
    }

    private fun saveEmail(email: String) {
        val emailColRef = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d(TAG, "No hay coleccion==========================================")

                    val documentId = email
                    val userData = hashMapOf<String, Any>(
                        "email" to email
                    )
                    db.collection("users")
                        .document(documentId)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d(ReservationViewModel.TAG, "Email guardado correctamente")
                        }
                        .addOnFailureListener { e ->
                            Log.e(ReservationViewModel.TAG, "Error al guardar email", e)
                        }


                }

            }
    }
    private fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        val usersCollection = db.collection("users")
        val usernameQuery = usersCollection.whereEqualTo("username", username)

        usernameQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                val isAvailable = querySnapshot.isEmpty
                callback(isAvailable)
            } else {
                // Manejo del error
                callback(false)
            }
        }
    }

}