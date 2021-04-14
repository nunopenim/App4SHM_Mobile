package org.app4shm.demo.ui.damage_detection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.app4shm.demo.R

class DamageDetectionFragment : Fragment() {

    private lateinit var debugViewModel: DebugViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        debugViewModel =
                ViewModelProvider(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        debugViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}