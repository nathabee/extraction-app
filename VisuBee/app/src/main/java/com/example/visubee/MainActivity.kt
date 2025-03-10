package com.example.visubee

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import org.opencv.android.OpenCVLoader
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)

        // ✅ Set Toolbar as ActionBar (Required for Hamburger Menu)
        setSupportActionBar(toolbar)

        // ✅ Find NavController (Handles Navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController: NavController? = navHostFragment?.navController

        if (navController != null) {
            // ✅ Define Top-Level Destinations (No Back Button Needed)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.processingFragment,
                    R.id.settingsFragment,
                    R.id.galleryFragment,
                    R.id.aboutFragment
                ), drawerLayout
            )

            // ✅ Enable Hamburger Button
            val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            // ✅ Connect ActionBar to NavController
            setupActionBarWithNavController(navController, appBarConfiguration)

            // ✅ Connect Navigation Drawer to NavController
            navView.setupWithNavController(navController)
        }

        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Initialization successful")
        } else {
            Log.e("OpenCV", "Initialization failed")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController
        return navController?.navigateUp() ?: super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        System.gc() // Helps in freeing OpenGL resources
    }


}
