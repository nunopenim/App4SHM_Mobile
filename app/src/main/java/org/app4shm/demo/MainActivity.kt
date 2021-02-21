
package org.app4shm.demo

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.graphics.Color
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.lang.Math.sqrt

// Variaveis Globais
var isReading = false
var readings = arrayListOf<String>()
var time = 0.0
var startTime = System.currentTimeMillis()
var series1 = LineGraphSeries<DataPoint>();
var series2 = LineGraphSeries<DataPoint>();
var series3 = LineGraphSeries<DataPoint>();



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
        var graph: GraphView = findViewById(R.id.graph)
        if (isReading) {
            //val values = findViewById<TextView>(R.id.values)
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
            //val lstPrint = listStringificator(readings)
            //values.setText(lstPrint)
            time = (((System.currentTimeMillis() - startTime).toDouble())/1000)

            series1.appendData(DataPoint(time,x.toDouble()), true, 100)
            series2.appendData(DataPoint(time,y.toDouble()), true, 100)
            series3.appendData(DataPoint(time,z.toDouble()), true, 100)

            series1.setThickness(8) //Thicc series
            series2.setThickness(8)
            series3.setThickness(8)
            series2.setColor(Color.RED)
            series3.setColor(Color.GREEN)

            graph.addSeries(series1)
            graph.addSeries(series2)
            graph.addSeries(series3)

            Thread.sleep(20) //50 hz
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

