package org.app4shm.demo

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.app4shm.demo.ui.settings.SettingsActivity

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //rotation
        if (Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME) == 0) {
            Log.e("Time", "Auto-time is off!")
            val toast = Toast.makeText(this, getString(R.string.no_auto_time), Toast.LENGTH_LONG)
            toast.show()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_debug
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun  onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.settings_nav_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean{
        val id = item.itemId
        when (id) {
            R.id.settings_button -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        return true
    }
}