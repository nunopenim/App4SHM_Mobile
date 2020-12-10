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

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        var button = findViewById(R.id.startMeasuring) as Button
        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                button.setText("Stop Measuring")
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
        val values = findViewById<TextView>(R.id.values)
        val x = event.values[0].toString()
        val y = event.values[1].toString()
        val z = event.values[2].toString()
        var readout = "   $time    |    $x    |    $y    |    $z    \n"
        if (isReading) {
            if (readings.size < 20) {
                readings.add(readout)
            }
            else {
                readings.removeAt(0)
                readings.add(readout)
            }
            val lstPrint = listStringificator(readings)
            values.setText(lstPrint)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

