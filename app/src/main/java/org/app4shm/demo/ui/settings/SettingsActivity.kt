package org.app4shm.demo.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import org.app4shm.demo.InfoSingleton
import org.app4shm.demo.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
            val name = findPreference<EditTextPreference>("Name")
            val ip = findPreference<EditTextPreference>("IP")
            name!!.text = InfoSingleton.username
            ip!!.text = InfoSingleton.IP
            name.setOnPreferenceChangeListener { preference, newValue -> InfoSingleton.changeName(newValue as String)}
            ip.setOnPreferenceChangeListener { preference, newValue -> InfoSingleton.changeIP(newValue as String)}
        }
    }
}

