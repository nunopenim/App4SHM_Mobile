package org.app4shm.demo

import android.annotation.SuppressLint
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
import kotlin.properties.Delegates

//eu sei que com variáveis globais, o código fica meio sujo, mas ainda não repensei de uma
//maneira melhor de fazer, temos de pensar nisso depois, para já não há bugs, mas de futuro...

//leituras
var isReading = false
//var readings = arrayListOf<String>()

//medições
var time = 0.0
var startTime = System.currentTimeMillis()

//gráficos
var series1 = LineGraphSeries<DataPoint>()
var series2 = LineGraphSeries<DataPoint>()
var series3 = LineGraphSeries<DataPoint>()
lateinit var graph: GraphView

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Sensor stuff
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    /* Ok Paulo, acho importante descrever as 4 linhas de código que estão à frente e o bloco de
     * código inicial no onCreate(), para evitar questões.
     *
     * Inicialmente eu vou buscar o número de cores disponíveis. Não todos os do dispositivo, apenas os
     * disponíveis.
     *
     * Posteriormente crio uma pool de threads, que com um número calculado na onCreate(). Mesmo num dispositivo dual core, é preferivel
     * ter uma thread para um core do que ter um número fixo de threads, por exemplo 2 threads.
     *
     * Porquê? Simples, se tiveres 2 threads, num CPU quadcore, podes ter até 2 cores sem fazer nada, tendo falta de desempenho.
     * Terás o desempenho melhor com um dual core. Mas num single core, o sheduler do CPU estará
     * constantemente a alterar entre as threads, e a app poderá até ficar muito mais lenta do que com apenas
     * 1 thread.
     *
     * O semáforo pronto... é um semáforo. Tem apenas uma autorização para se comportar como um semáforo binário.
     *
     * E sim, tenho saudades de coisas de mais baixo nível. I <3 C & Assembly. Ainda acho que era mais fácil com
     * a biblioteca das pthreads e dos semaphores.
     */

    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private var thread_count by Delegates.notNull<Int>()
    private lateinit var executor: ExecutorService
    private var semaphore : Semaphore = Semaphore(1)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Este bloco de código gera o número de threads dinamicamente
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
        val button = findViewById<Button>(R.id.startMeasuring)
        graph = findViewById(R.id.graph)
        button.setOnClickListener {
            isReading = !isReading
            if (isReading) {
                button.setText("Stop Reading")
                startTime = System.currentTimeMillis()
            }
            else {
                button.setText("Start Reading")
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
                semaphore.acquire() //I miss lpthreads.h and semaphores.h
                series1.appendData(DataPoint(time, event.values[0].toDouble()), false, 100)
                series2.appendData(DataPoint(time, event.values[1].toDouble()), false, 100)
                series3.appendData(DataPoint(time, event.values[2].toDouble()), false, 100)
                semaphore.release()
            }
            Thread.sleep(20) //50 hz
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

