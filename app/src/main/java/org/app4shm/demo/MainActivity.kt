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
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

//eu sei que com variáveis globais, o código fica meio sujo, mas ainda não repensei de uma
//maneira melhor de fazer, temos de pensar nisso depois, para já não há bugs, mas de futuro...

//leituras
var isReading = false
var readings = arrayListOf<String>()

//medições
var time = 0.0
var startTime = System.currentTimeMillis()

//gráficos
var series1 = LineGraphSeries<DataPoint>()
var series2 = LineGraphSeries<DataPoint>()
var series3 = LineGraphSeries<DataPoint>()
lateinit var graph: GraphView;

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Sensor stuff
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    private val executor: ExecutorService = Executors.newFixedThreadPool(4)
    private var semaphore : Semaphore = Semaphore(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        val button = findViewById<Button>(R.id.startMeasuring)
        graph = findViewById(R.id.graph)
        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                button.setText("Stop Measuring")
                startTime = System.currentTimeMillis()
            }
            else {
                button.setText("Start Measuring")
            }
        }
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        series1.setThickness(8)
        series2.setThickness(8)
        series3.setThickness(8)
        series2.setColor(Color.RED)
        series3.setColor(Color.GREEN)
        graph.addSeries(series1)
        graph.addSeries(series2)
        graph.addSeries(series3)
    }

    override fun onSensorChanged(event: SensorEvent) {
        //val values = findViewById<TextView>(R.id.values)
        if (isReading) {
            time = (((System.currentTimeMillis() - startTime).toDouble())/1000)

            //val timestr = String.format("%.2f", time)
            //val readout = "time=$timestr s | x=$x (m/s^2) | y=$y (m/s^2) | z=$z (m/s^2)\n"
            //if (readings.size < 25) {
            //    readings.add(readout)
            //}
            //else {
            //    readings.removeAt(0)
            //    readings.add(readout)
            //}
            //val lstPrint = listStringificator(readings)
            //values.setText(lstPrint)

            executor.execute {
                semaphore.acquire()
                series1.appendData(DataPoint(time, event.values[0].toDouble()), false, 50)
                series2.appendData(DataPoint(time, event.values[1].toDouble()), false, 50)
                series3.appendData(DataPoint(time, event.values[2].toDouble()), false, 50)
                semaphore.release()
                Thread.sleep(20) //50 hz
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

