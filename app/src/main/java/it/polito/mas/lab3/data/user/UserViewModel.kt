package it.polito.mas.lab3.data.user


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mas.lab3.data.Reservation
import it.polito.mas.lab3.data.ReservationViewModel
import java.util.*



class UserViewModel (application: Application): AndroidViewModel(application) {

    //All the data from the database:


    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user


    private val currentUser = FirebaseAuth.getInstance().currentUser
    //LiveData for username selection:
    private val mutableNameList = MutableLiveData<List<Reservation>>()
    val filteredByNameData: LiveData<List<Reservation>> get() = mutableNameList

    //Firestore db
    val db = Firebase.firestore

    companion object {
        const val TAG = "FirestoreApp"
    }

    fun updateUser(user: User) {

        val documentId = user.email
        db.collection("users")
            .document(documentId!!)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User saved")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user", e)
            }

    }

    fun getUser(email: String) {

        val documentId = email
        db.collection("users")
            .document(documentId!!)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val email = document.getString("email")?:null!!
                    val username = document.getString("username")?:null!!
                    val name = document.getString("name")?:null!!
                    val age = document.getLong("age")?.toInt()?:null!!
                    val gender = document.getString("gender")?:null!!
                    val phoneNumber = document.getLong("phoneNumber")?.toInt()?:null!!
                    val sport = document.getString("sport")?:null!!
                    val level = document.getString("level")?:null!!
                    val experience = document.getString("experience")?:null!!
                    val city = document.getString("city")?:null!!
                    val weekday = document.getString("weekday")?:null!!
                    val slotfav = document.getString("slotfav")?:null!!

                    val user = User(email, username, name, age, gender, phoneNumber, sport, level,
                    experience,city, weekday,slotfav)

                    _user.value = user

                    Log.d(TAG, "User saved")
                } else {
                    Log.d(TAG, "User document not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user", e)
            }

    }







    fun checkUser(email: String): Boolean {
        val query = db.collection("users")
            .whereEqualTo("email", email)
            .get()

        val querySnapshot = Tasks.await(query)

        var aux = false
        for (document in querySnapshot) {
            if (document.contains("username")) {
                Log.d(TAG, "debo ir antes==========================================")
                aux = true
            } else {
                Log.d(TAG, "debo ir antes==========================================")
                aux = false
            }
        }
        Log.d(TAG, "Dbo ir despues==========================================")
        return aux
    }


    fun saveEmail(email: String) {
        val emailColRef = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d(TAG, "No hay coleccion==========================================")

                    val documentId = email

                    val userData = hashMapOf<String, Any>(
                        "email" to email,
                        "name" to  "",
                        "age" to 0,
                        "gender" to "",
                        "phoneNumber" to 0,
                        "sport" to "",
                        "level" to "",
                        "experience" to "",
                        "city" to "",
                        "weekday" to "",
                        "slotfav" to ""
                    )
                    db.collection("users")
                        .document(documentId)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d(ReservationViewModel.TAG, "Email guardado correctamente========================")
                        }
                        .addOnFailureListener { e ->
                            Log.e(ReservationViewModel.TAG, "Error al guardar email==================", e)
                        }


                }

            }
    }

    fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
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



    fun saveUsernameToFirebase(username: String, email: String) {

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

    fun getAll(){
        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservationsList = mutableListOf<User>()

                for (document in querySnapshot) {
                    val email = document.getString("email")?:null!!
                    val username = document.getString("username")?:null!!
                    val name = document.getString("name")?:null!!
                    val age = document.getLong("age")?.toInt()?:null!!
                    val gender = document.getString("gender")?:null!!
                    val phoneNumber = document.getLong("phoneNumber")?.toInt()?:null!!
                    val sport = document.getString("sport")?:null!!
                    val level = document.getString("level")?:null!!
                    val experience = document.getString("experience")?:null!!
                    val city = document.getString("city")?:null!!
                    val weekday = document.getString("weekday")?:null!!
                    val slotfav = document.getString("slotfav")?:null!!

                    val user = User(
                        email,
                        username,
                        name,
                        age,
                        gender,
                        phoneNumber,
                        sport,
                        level,
                        experience,
                        city,
                        weekday,
                        slotfav
                    )
                }
            }.addOnFailureListener { e ->
                Log.e(ReservationViewModel.TAG, "Error obtaining User", e)
            }
    }



    fun deleteUser(user: User){

        val collectionRef = db.collection("users")

        val documentId = user.email

        collectionRef.document(documentId!!)
            .delete()
            .addOnSuccessListener {
                // Documento eliminado exitosamente
                Log.d(TAG, "User deleted: $documentId")
            }
            .addOnFailureListener { e ->
                // Ocurrió un error al eliminar el documento
                Log.e(TAG, "Error deleting", e)
            }
    }
}