package org.app4shm.demo.ui.risk_analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RiskAnalysisViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Coming soon!"
    }
    val text: LiveData<String> = _text
}