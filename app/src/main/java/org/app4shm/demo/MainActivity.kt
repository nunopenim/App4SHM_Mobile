package org.app4shm.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app4shm.server.Data
import com.google.android.gms.location.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.properties.Delegates

//eu sei que com variáveis globais, o código fica meio sujo, mas ainda não repensei de uma
//maneira melhor de fazer, temos de pensar nisso depois, para já não há bugs, mas de futuro...

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
var id = ""

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
    private var semaphore : Semaphore = Semaphore(1)
    //private lateinit var locationCallback: LocationCallback
    //lateinit var fusedLocationClient : FusedLocationProviderClient
    //var locationRequest = LocationRequest.create()
    //private var semaphoreSend : Semaphore = Semaphore(1)

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    //@SuppressLint("MissingPermission")
    //private fun startLocationUpdates() {
    //    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    //}

    //override fun onResume() {
    //    super.onResume()
    //    startLocationUpdates()
    //}

    fun init() {

        // Este bloco de código gera o número de threads dinamicamente
        //location = locateMe()
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

        // id, gps stuff, you name it!

        id = Secure.getString(contentResolver, Secure.ANDROID_ID)
        //locationCallback = object : LocationCallback() {
        //    override fun onLocationResult(locationResult: LocationResult?) {
        //        locationResult ?: return
        //        location = locationResult.lastLocation
        //    }
        //}
        //locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //app itself

        setContentView(R.layout.content_main)
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

    //fun offsetUpdater () {
    //    if (offset > 100) {
    //        startLocationUpdates()
    //    }
    //    offset = location.time - System.currentTimeMillis()
    //    locationCallback = object : LocationCallback() {
    //        override fun onLocationResult(locationResult: LocationResult?) {
    //            locationResult ?: return
    //            location = locationResult.lastLocation
    //        }
    //    }
    //}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1111)

            // Still needs work permission-wise!
        }
        else {
            init()
        }
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
            readings.add(reading)

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
                semaphore.acquire() //I miss lpthreads.h and semaphores.h
                series1.appendData(DataPoint(time, num1), false, 100)
                series2.appendData(DataPoint(time, num2), false, 100)
                series3.appendData(DataPoint(time, num3), false, 100)
                semaphore.release()
            }
            Thread.sleep(20) //50 hz
        }
    }

    //@SuppressLint("MissingPermission")
    //fun locateMe() : Location {
    //    var gps_enabled = false
    //    var network_enabled = false
    //    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    //    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    //    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    //    var net_loc: Location? = null
    //    var gps_loc: Location? = null
    //    var finalLoc: Location? = null
    //    if (gps_enabled) gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    //    if (network_enabled) net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    //    if (gps_loc != null && net_loc != null) {
    //        //smaller the number more accurate result will
    //        finalLoc = if (gps_loc.accuracy > net_loc.accuracy) net_loc else gps_loc

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
    //    } else {
    //        if (gps_loc != null) {
    //            finalLoc = gps_loc
    //        } else if (net_loc != null) {
    //            finalLoc = net_loc
    //        }
    //    }
    //    return finalLoc!!
    //}
}
