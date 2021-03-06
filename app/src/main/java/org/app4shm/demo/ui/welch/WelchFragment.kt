package org.app4shm.demo.ui.welch

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.*
import okhttp3.Request
import okhttp3.RequestBody
import org.app4shm.demo.*
import org.app4shm.demo.ui.home.JSON
import org.app4shm.demo.ui.home.httpClient
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore


// X é azul
// Y é vermelho
// Z é verde
//O que está a ser selecionado precisa de ser desenhado por cima dos outros gráficos todos ou seja adicionado ao GraphView em último
lateinit var graph: GraphView
var readings = arrayListOf<DataPoints>()
var seriesx = LineGraphSeries<DataPoint>()
var seriesy = LineGraphSeries<DataPoint>()
var seriesz = LineGraphSeries<DataPoint>()
var selectBar = LineGraphSeries<DataPoint>()
var storedSelected = mutableMapOf<Double, Double>()
var mapX = mutableMapOf<Double, Double>()
var mapY = mutableMapOf<Double, Double>()
var mapZ = mutableMapOf<Double, Double>()
var redFirst = false
var blueFirst = true
var greenFirst = false

var maxClicks = 3
var clicks = 0


class WelchFragment : Fragment() {

    private var senderThread = Executors.newFixedThreadPool(2)
    private var semaphoreSend: Semaphore = Semaphore(1)

    lateinit var radioGroup: RadioGroup
    lateinit var redTap: RadioButton
    lateinit var blueTap: RadioButton
    lateinit var greenTap: RadioButton

    lateinit var redDraw: CheckBox
    lateinit var blueDraw: CheckBox
    lateinit var greenDraw: CheckBox


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        readings = arrayListOf<DataPoints>()
        seriesx = LineGraphSeries<DataPoint>()
        seriesy = LineGraphSeries<DataPoint>()
        seriesz = LineGraphSeries<DataPoint>()
        selectBar = LineGraphSeries<DataPoint>()
        storedSelected = mutableMapOf<Double, Double>()
        mapX = mutableMapOf<Double, Double>()
        mapY = mutableMapOf<Double, Double>()
        mapZ = mutableMapOf<Double, Double>()

        maxClicks = 3
        clicks = 0

        val root = inflater.inflate(R.layout.fragment_welch, container, false)

        val sendFrequencies: View = root.findViewById(R.id.send_frequencies)
        sendFrequencies.setOnClickListener { view ->
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle(getString(R.string.warning))
            builder.setMessage(getString(R.string.you_are_about))
            builder.setPositiveButton(getString(R.string.Confirm),
                DialogInterface.OnClickListener { dialog, which ->
                    if (readings.size != 0) {
                        senderThread.execute {
                            semaphoreSend.acquire()
                            sendData()
                            semaphoreSend.release()
                        }
                    }
                })
            builder.setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which -> })

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }


        redTap = root.findViewById(R.id.redTap)
        blueTap = root.findViewById(R.id.blueTap)
        greenTap = root.findViewById(R.id.greenTap)
        radioGroup = root.findViewById(R.id.radioGroup)

        redDraw = root.findViewById(R.id.redDraw)
        blueDraw = root.findViewById(R.id.blueDraw)
        greenDraw = root.findViewById(R.id.greenDraw)

        redTap.setOnClickListener {
            redFirst = true
            blueFirst = false
            greenFirst = false
            graph.removeAllSeries()
            reDraw()
            seriesx.setOnDataPointTapListener(null);
            seriesz.setOnDataPointTapListener(null);
            seriesy.setOnDataPointTapListener { series, dataPoint -> onTapy(series, dataPoint) }
        }
        blueTap.setOnClickListener {
            redFirst = false
            blueFirst = true
            greenFirst = false
            graph.removeAllSeries()
            reDraw()
            seriesy.setOnDataPointTapListener(null);
            seriesz.setOnDataPointTapListener(null);
            seriesx.setOnDataPointTapListener { series, dataPoint -> onTapx(series, dataPoint) }
        }
        greenTap.setOnClickListener {
            redFirst = false
            blueFirst = false
            greenFirst = true
            graph.removeAllSeries()
            reDraw()
            seriesy.setOnDataPointTapListener(null);
            seriesx.setOnDataPointTapListener(null);
            seriesz.setOnDataPointTapListener { series, dataPoint -> onTapz(series, dataPoint) }
        }
        redDraw.setOnClickListener {
            graph.removeAllSeries()
            reDraw()
        }
        blueDraw.setOnClickListener {
            graph.removeAllSeries()
            reDraw()
        }
        greenDraw.setOnClickListener {
            graph.removeAllSeries()
            reDraw()
        }

        graph = root.findViewById(R.id.graph_welch)
        seriesx = LineGraphSeries<DataPoint>()
        seriesy = LineGraphSeries<DataPoint>()
        seriesz = LineGraphSeries<DataPoint>()
        var a = DataPoint(0.0, 60.0)
        var b = DataPoint(.0, 0.0)
        selectBar.appendData(a, true, 2)
        selectBar.appendData(b, true, 2)

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true

        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(InfoSingleton.welchF.size.toDouble())
        graph.viewport.setMaxY(60.0)
        graph.viewport.setMinY(0.0)

        for (i in InfoSingleton.welchF.indices) {
            mapX.put(InfoSingleton.welchF.get(i), InfoSingleton.welchX.get(i))
            mapY.put(InfoSingleton.welchF.get(i), InfoSingleton.welchY.get(i))
            mapZ.put(InfoSingleton.welchF.get(i), InfoSingleton.welchZ.get(i))
            seriesx.appendData(
                DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchX.get(i)),
                true,
                InfoSingleton.welchF.size
            )
            seriesy.appendData(
                DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchY.get(i)),
                true,
                InfoSingleton.welchF.size
            )
            seriesz.appendData(
                DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchZ.get(i)),
                true,
                InfoSingleton.welchF.size
            )
        }

        seriesx.setDrawDataPoints(true);
        seriesx.setDataPointsRadius(10F);
        seriesy.setDrawDataPoints(true);
        seriesy.setDataPointsRadius(10F);
        seriesz.setDrawDataPoints(true);
        seriesz.setDataPointsRadius(10F);

        seriesy.color = Color.RED
        seriesz.color = Color.GREEN
        //graph.addSeries(seriesy)
        graph.addSeries(seriesz)
        //graph.addSeries(seriesx)
        graph.viewport.setScalable(true)
        graph.viewport.setScalableY(true)
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true
        graph.viewport.setScrollableY(true)

        //TEMP
        redFirst = false
        blueFirst = false
        greenFirst = true
        graph.removeAllSeries()
        reDraw()
        seriesy.setOnDataPointTapListener(null);
        seriesx.setOnDataPointTapListener(null);
        seriesz.setOnDataPointTapListener { series, dataPoint -> onTapz(series, dataPoint) }
        //TEMP

        return root
    }

    fun onTapx(ser: Series<DataPointInterface>, dataPointInterface: DataPointInterface) {
        var msg = "X:" + dataPointInterface.x + "\nY:" + dataPointInterface.y
        onTapCommonBranch(dataPointInterface)
        reDraw()

        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun onTapy(ser: Series<DataPointInterface>, dataPointInterface: DataPointInterface) {
        var msg = "X:" + dataPointInterface.x + "\nY:" + dataPointInterface.y
        onTapCommonBranch(dataPointInterface)
        reDraw()

        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun onTapz(ser: Series<DataPointInterface>, dataPointInterface: DataPointInterface) {
        var msg = "X:" + dataPointInterface.x + "\nY:" + dataPointInterface.y
        onTapCommonBranch(dataPointInterface)
        reDraw()


        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun getDataPoint(): Array<DataPoint> {

        var a = DataPoint(0.0, 1.0)
        var b = DataPoint(1.0, 11.0)
        var c = DataPoint(2.0, 5.0)
        var a1 = DataPoint(3.0, 8.0)
        var b1 = DataPoint(4.0, 5.0)
        var c1 = DataPoint(5.0, 11.0)
        var a2 = DataPoint(6.0, 9.0)
        var b2 = DataPoint(7.0, 13.0)
        var c2 = DataPoint(9.0, 9.0)

        var list = arrayOf<DataPoint>(a, b, c, a1, b1, c1, a2, b2, c2)
        return list

    }

    private fun onTapCommonBranch(dataPointInterface: DataPointInterface) {
        graph.removeAllSeries()
        /*
        selected = PointsGraphSeries<DataPoint>()
        selected.appendData(DataPoint(dataPointInterface.x, dataPointInterface.y), false, 1)
        selected.color = Color.RED
        selected.setCustomShape { canvas, paint, x, y, dataPoint ->
            run {
                paint.setStrokeWidth(5F)
                canvas.drawLine(x - 20, y, x, y - 20, paint)
                canvas.drawLine(x, y - 20, x + 20, y, paint)
                canvas.drawLine(x + 20, y, x, y + 20, paint)
                canvas.drawLine(x - 20, y, x, y + 20, paint)
            }
        }*/
        if (storedSelected.containsKey(dataPointInterface.x)) {
            clicks--
            storedSelected.remove(dataPointInterface.x)
        } else if(clicks < maxClicks){
            clicks++
            storedSelected.put(dataPointInterface.x, dataPointInterface.y)
        }

        //graph.addSeries(selected)

        seriesy.color = Color.RED
        seriesz.color = Color.GREEN
    }

    fun reDraw() {
        drawAllSelected()
        /*
        if (blueFirst) {
            if (redDraw.isChecked) {
                graph.addSeries(seriesy)
            }
            if (greenDraw.isChecked) {
                graph.addSeries(seriesz)
            }
            if (blueDraw.isChecked) {
                graph.addSeries(seriesx)
            }
        } else if (redFirst) {
            if (greenDraw.isChecked) {
                graph.addSeries(seriesz)
            }
            if (blueDraw.isChecked) {
                graph.addSeries(seriesx)
            }
            if (redDraw.isChecked) {
                graph.addSeries(seriesy)
            }
        } else if (greenFirst) {
            if (redDraw.isChecked) {
                graph.addSeries(seriesy)
            }
            if (blueDraw.isChecked) {
                graph.addSeries(seriesx)
            }
            if (greenDraw.isChecked) {
                graph.addSeries(seriesz)
            }
        }*/
        graph.addSeries(seriesz)
    }

    fun drawAllSelected() {
        readings = arrayListOf<DataPoints>()
        for (a in storedSelected) {
            var selectedX = PointsGraphSeries<DataPoint>()
            var selectedY = PointsGraphSeries<DataPoint>()
            var selectedZ = PointsGraphSeries<DataPoint>()
            customShape(selectedX)
            customShape(selectedY)
            customShape(selectedZ)
            selectedX.appendData(DataPoint(a.key, mapX.get(a.key)!!), false, 1)
            selectedX.color = Color.BLUE

            selectedY.appendData(DataPoint(a.key, mapY.get(a.key)!!), false, 1)
            selectedY.color = Color.RED

            selectedZ.appendData(DataPoint(a.key, mapZ.get(a.key)!!), false, 1)
            selectedZ.color = Color.GREEN

            readings.add(
                DataPoints(
                    InfoSingleton.username,
                    a.key,
                    mapX.get(a.key)!!,
                    mapY.get(a.key)!!,
                    mapZ.get(a.key)!!,
                    InfoSingleton.group
                )
            )

            //graph.addSeries(selectedX)
            //graph.addSeries(selectedY)
            graph.addSeries(selectedZ)
        }
    }

    fun customShape(point: PointsGraphSeries<DataPoint>) {
        point.setCustomShape { canvas, paint, x, y, dataPoint ->
            run {
                paint.setStrokeWidth(5F)
                canvas.drawLine(x - 20, y, x, y - 20, paint)
                canvas.drawLine(x, y - 20, x + 20, y, paint)
                canvas.drawLine(x + 20, y, x, y + 20, paint)
                canvas.drawLine(x - 20, y, x, y + 20, paint)
            }
        }
    }

    fun sendData() {
        val jsonText = makeMeAJsonPoints(readings)
        readings = arrayListOf<DataPoints>()
        val body = RequestBody.create(JSON, jsonText)
        val request: Request =
            Request.Builder().url("${InfoSingleton.IP}/data/points").post(body).build()
        httpClient.newCall(request).execute()
    }
}