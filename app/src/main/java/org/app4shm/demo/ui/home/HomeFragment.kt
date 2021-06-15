package org.app4shm.demo.ui.home

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.app4shm.demo.*
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

// X é azul
// Y é vermelho
// Z é verde

//eu sei que com variáveis globais, o código fica meio sujo, mas ainda não repensei de uma
//maneira melhor de fazer, temos de pensar nisso depois, para já não há bugs, mas de futuro...

//Singleton stuff
//var group = InfoSingleton.group
//var device_id = InfoSingleton.username
//val serverURL = "http://${InfoSingleton.IP}/data/reading"

//Server shtuff
val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
var httpClient: OkHttpClient = OkHttpClient()

//leituras
var isReading = false
var readings = arrayListOf<Data>()

//medições
var time = 0.0

//GPS and ID stuff
//var requestingLocationUpdates = true
//lateinit var location : Location
var offset: Long = 0
var startTime = System.currentTimeMillis() + offset

//gráficos
var series1 = LineGraphSeries<DataPoint>()
var series2 = LineGraphSeries<DataPoint>()
var series3 = LineGraphSeries<DataPoint>()
lateinit var graph: GraphView

const val SAMPLING_PERIOD = 20 // ms

class HomeFragment : Fragment(), SensorEventListener {

    // Sensor stuff
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var executor = Executors.newFixedThreadPool(3)
    private var senderThread = Executors.newFixedThreadPool(2)
    private var semaphoreSend: Semaphore = Semaphore(1)
    private var semaphoreDraw: Semaphore = Semaphore(1)

    var count = 0

    fun sendData() {
        val jsonText = makeMeAJson(readings)
        readings = arrayListOf<Data>()
        val body = RequestBody.create(JSON, jsonText)
        val request: Request = Request.Builder().url("http://${InfoSingleton.IP}/data/reading").post(body).build()
        InfoSingleton.response = httpClient.newCall(request).execute()
        InfoSingleton.processRecievedData()
    }

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button = root.findViewById<Button>(R.id.startMeasuring)
        val sendData = root.findViewById<Button>(R.id.sendData)
        graph = root.findViewById(R.id.graph)

        mSensorManager = this.context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                readings = arrayListOf<Data>()
                button.text = "Stop Reading"
                startTime = System.currentTimeMillis()

                count = 0

                graph.getViewport().setMinX(0.0)
                graph.getViewport().setMaxX(50.0)
                graph.getViewport().setXAxisBoundsManual(true);

                mSensorManager.registerListener(this, mAccelerometer, SAMPLING_PERIOD * 1000)
            } else {

                mSensorManager.unregisterListener(this, mAccelerometer)

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
                Toast.makeText(activity, getString(R.string.sent_data), Toast.LENGTH_SHORT)
                    .show()
                sendData.text = "Send Data!"
            }
        }


        //Precisa de semáfero, comentado para não causar transtorno
        // comentar o de cima e descomentar o de baixo quando tiver semáfero

/*
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mSensorThread = HandlerThread("sensor_thread"); //$NON-NLS-1$
        mSensorThread.start();
        val mHandler = Handler(mSensorThread.looper);
        val sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null)
        {
            mSensorManager.registerListener(this, sensor, 15, mHandler);
        }
*/
        series1.setThickness(8)
        series2.setThickness(8)
        series3.setThickness(8)
        series2.setColor(Color.RED)
        series3.setColor(Color.GREEN)
        graph.addSeries(series1)
        graph.addSeries(series2)
        graph.addSeries(series3)
        return root
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this, mAccelerometer)
        isReading = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        //val values = findViewById<TextView>(R.id.values)
        //if (isReading) {
        //offsetUpdater()
        val actualTime = System.currentTimeMillis()
        //Log.i("App", actualTime.toString())
        time = (((actualTime - startTime).toDouble()) / 1000)

        count++
        if (count % 20 == 0) {
            Log.i(
                "App",
                "Average sampling time: " + (((actualTime - startTime).toDouble()) / count) + " ms"
            )
        }

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val reading = Data(InfoSingleton.username, actualTime, x, y, z, InfoSingleton.group)
        senderThread.execute {
            semaphoreSend.acquire()
            readings.add(reading)
            semaphoreSend.release()
        }

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

        if (count % (100 / SAMPLING_PERIOD) == 0) {
            series1.appendData(DataPoint(count.toDouble(), num1), true, 100)
            series2.appendData(DataPoint(count.toDouble(), num2), true, 100)
            series3.appendData(DataPoint(count.toDouble(), num3), true, 100)
            graph.getViewport().setMinY(min - 2)
            graph.getViewport().setMaxY(max + 2)
        }

        /*executor.execute {
            graph.getViewport().setMinX(time - 5)
            graph.getViewport().setMaxX(time)
            graph.getViewport().setXAxisBoundsManual(true)
            graph.getViewport().setMinY(min - 5)
            graph.getViewport().setMaxY(max + 5)
            graph.getViewport().setYAxisBoundsManual(true)
            series1.appendData(DataPoint(time, num1), false, 100)
            series2.appendData(DataPoint(time, num2), false, 100)
            series3.appendData(DataPoint(time, num3), false, 100)
        }


        if (20 - (System.currentTimeMillis() - actualTime) > 0) {
            Thread.sleep(20 - (System.currentTimeMillis() - actualTime)) //50 hz
        }*/
        //}
    }

}