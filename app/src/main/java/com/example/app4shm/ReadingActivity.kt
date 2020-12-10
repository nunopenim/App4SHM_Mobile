package com.example.app4shm

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ReadingActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        var Button = findViewById(R.id.goToMainActivity) as Button

        Button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        val x = findViewById<TextView>(R.id.x)
        val y = findViewById<TextView>(R.id.y)
        val z = findViewById<TextView>(R.id.z)
        x.setText(event.values[0].toString())
        y.setText(event.values[1].toString())
        z.setText(event.values[2].toString())
    }
}