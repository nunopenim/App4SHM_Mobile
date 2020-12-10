package com.example.app4shm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)


        var button = findViewById(R.id.goToReadingActivity) as Button

        button.setText("Start Measuring")

        button.setOnClickListener {
            startActivity(Intent(this, ReadingActivity::class.java))
        }
    }
}