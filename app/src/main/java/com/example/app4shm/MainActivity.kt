package com.example.app4shm

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView

// Variaveis Globais
var isReading = false
var readings = arrayListOf<String>()
var time = 0.0
var startTime = System.currentTimeMillis()

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val button = findViewById<Button>(R.id.startMeasuring)
        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                button.setText("Stop Measuring")
                startTime = System.currentTimeMillis()
            }
            else {
                time = 0.0
                button.setText("Start Measuring")
            }
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (isReading) {
            val values = findViewById<TextView>(R.id.values)
            val x = String.format("%.2f", event.values[0])
            val y = String.format("%.2f", event.values[1])
            val z = String.format("%.2f", event.values[2])
            val timestr = String.format("%.2f", time)
            val readout = "time=$timestr s | x=$x (m/s^2) | y=$y (m/s^2) | z=$z (m/s^2)\n"
            if (readings.size < 25) {
                readings.add(readout)
            }
            else {
                readings.removeAt(0)
                readings.add(readout)
            }
            val lstPrint = listStringificator(readings)
            values.setText(lstPrint)
            time = (((System.currentTimeMillis() - startTime).toDouble())/1000)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

