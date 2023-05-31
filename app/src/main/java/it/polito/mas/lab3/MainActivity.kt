package it.polito.mas.lab3

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var profile = false
    private var returnID = R.id.listFragment
    private var args: Bundle? = null

    //val db = Firebase.firestore

    companion object {
        //const val TAG = "FirestoreApp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navController = Navigation.findNavController(this, R.id.frag_host)

        NavigationUI.setupWithNavController(bottomNavigation,navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //When we select the user icon, we should move from one fragment to the other:
        return when(item.itemId) {
            R.id.user_menu ->{
                if (!profile) {

                    returnID = navController.currentDestination?.id!!
                    args = supportFragmentManager.fragments.last().childFragmentManager.fragments[0].arguments

                    navController.navigate(R.id.profileFragment)
                    profile = true
                }
                else{
                    navController.navigate(returnID, args)
                    profile = false
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
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

}



