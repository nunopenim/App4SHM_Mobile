package org.app4shm.demo.ui.risk_analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.app4shm.demo.R

class RiskAnalysisFragment : Fragment() {

    private lateinit var riskAnalysisViewModel: RiskAnalysisViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        riskAnalysisViewModel =
                ViewModelProvider(this).get(RiskAnalysisViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        riskAnalysisViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}