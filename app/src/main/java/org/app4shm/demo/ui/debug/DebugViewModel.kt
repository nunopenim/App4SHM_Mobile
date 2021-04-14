package org.app4shm.demo.ui.damage_detection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DebugViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Coming soon!"
    }
    val text: LiveData<String> = _text
}