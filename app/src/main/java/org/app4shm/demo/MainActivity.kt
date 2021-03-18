package org.app4shm.demo

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app4shm.server.Data
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.properties.Delegates


//eu sei que com variáveis globais, o código fica meio sujo, mas ainda não repensei de uma
//maneira melhor de fazer, temos de pensar nisso depois, para já não há bugs, mas de futuro...

//Server shtuff
val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
var httpClient : OkHttpClient = OkHttpClient()
val serverURL = "http://nunopenim.ddns.net/data/reading"

//leituras
var isReading = false
var readings = arrayListOf<Data>()

//medições
var time = 0.0

//GPS and ID stuff
//var requestingLocationUpdates = true
//lateinit var location : Location
var offset : Long= 0
var startTime = System.currentTimeMillis() + offset
var id = Build.DEVICE

//gráficos
var series1 = LineGraphSeries<DataPoint>()
var series2 = LineGraphSeries<DataPoint>()
var series3 = LineGraphSeries<DataPoint>()
lateinit var graph: GraphView

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Sensor stuff
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private var thread_count by Delegates.notNull<Int>()
    private lateinit var executor: ExecutorService
    private var senderThread = Executors.newFixedThreadPool(1)
    private var semaphore : Semaphore = Semaphore(1)
    private var semaphoreSend : Semaphore = Semaphore(1)

    fun sendData() {
        val jsonText = makeMeAJson(readings)
        readings = arrayListOf<Data>()
        val body = RequestBody.create(JSON, jsonText)
        val request: Request = Request.Builder().url(serverURL).post(body).build()
        httpClient.newCall(request).execute()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (NUMBER_OF_CORES > 4) {
            thread_count = 4
        }
        else if (NUMBER_OF_CORES > 1){
            thread_count = NUMBER_OF_CORES - 1
        }
        else {
            thread_count = 1
        }
        executor = Executors.newFixedThreadPool(thread_count)
        setContentView(R.layout.content_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //rotation
        val button = findViewById<Button>(R.id.startMeasuring)
        val sendData = findViewById<Button>(R.id.sendData)
        graph = findViewById(R.id.graph)
        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                button.text = "Stop Reading"
                startTime = System.currentTimeMillis()
            }
            else {
                button.text = "Start Reading"
                graph.removeAllSeries()
                series1 = LineGraphSeries<DataPoint>()
                series2 = LineGraphSeries<DataPoint>()
                series3 = LineGraphSeries<DataPoint>()
                series1.setThickness(8)
                series2.setThickness(8)
                series3.setThickness(8)
                series2.setColor(Color.RED)
                series3.setColor(Color.GREEN)
                graph.addSeries(series1)
                graph.addSeries(series2)
                graph.addSeries(series3)
            }
        }
        sendData.setOnClickListener {
            if (readings.size != 0) {
                sendData.text = "Sending data..."
                senderThread.execute {
                    semaphoreSend.acquire()
                    sendData()
                    semaphoreSend.release()
                }
                sendData.text = "Send Data!"
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
            //offsetUpdater()
            val actualTime = System.currentTimeMillis()
            time = (((actualTime - startTime).toDouble())/1000)
            graph.getViewport().setMinX(time-5)
            graph.getViewport().setMaxX(time)
            graph.getViewport().setXAxisBoundsManual(true)

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val reading = Data(id, actualTime, x, y, z)
            semaphoreSend.acquire()
            readings.add(reading)
            semaphoreSend.release()

            val num1 = x.toDouble()
            val num2 = y.toDouble()
            val num3 = z.toDouble()

            val min = if(num1<=num2 && num1<=num3){
                num1
            } else if(num2<=num1 && num2<=num3){
                num2
            } else{
                num3
            }

            val max = if(num1>=num2 && num1>=num3){
                num1
            } else if(num2>=num1 && num2>=num3){
                num2
            } else{
                num3
            }

            graph.getViewport().setMinY(min - 5)
            graph.getViewport().setMaxY(max + 5)
            graph.getViewport().setYAxisBoundsManual(true)

            executor.execute {
                semaphore.acquire() //I miss lpthreads.h and semaphores.h
                series1.appendData(DataPoint(time, num1), false, 100)
                series2.appendData(DataPoint(time, num2), false, 100)
                series3.appendData(DataPoint(time, num3), false, 100)
                semaphore.release()
            }
            Thread.sleep(20) //50 hz
        }
    }
}
