//ackage com.example.parttracker.ui
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.NavigationUI
//import com.example.parttracker.R
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
//
//
//
//
//        // Ensure navHostFragment works
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
//        val navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
//        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
//    }
//}
//


package com.example.parttracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.findNavController
import com.example.parttracker.R
import com.example.parttracker.firebase.FirebaseSyncManager


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Set up toolbar as action bar
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        // Set up Navigation Controller
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
//}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        FirebaseSyncManager.startListening(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
