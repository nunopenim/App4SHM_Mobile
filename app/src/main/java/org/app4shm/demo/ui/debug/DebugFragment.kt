package org.app4shm.demo.ui.debug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import org.app4shm.demo.InfoSingleton
import org.app4shm.demo.MainActivity
import org.app4shm.demo.R


class DebugFragment : Fragment() {

    private lateinit var debugViewModel: DebugViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        debugViewModel =
            ViewModelProvider(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debug, container, false)

        val buttonSave: Button = root.findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {

            val group: TextView = root.findViewById(R.id.groupText)
            InfoSingleton.changeGroup(group.text.toString())


            Toast.makeText(activity, getString(R.string.changes_saved), Toast.LENGTH_SHORT)
                .show()

        }

        return root
    }
}