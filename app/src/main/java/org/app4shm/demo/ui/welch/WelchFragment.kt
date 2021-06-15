package org.app4shm.demo.ui.welch

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.*
import org.app4shm.demo.Data
import org.app4shm.demo.InfoSingleton
import org.app4shm.demo.R

// X é azul
// Y é vermelho
// Z é verde

lateinit var graph: GraphView
var readings = arrayListOf<Data>()
var seriesx = LineGraphSeries<DataPoint>()
var seriesy = LineGraphSeries<DataPoint>()
var seriesz = LineGraphSeries<DataPoint>()
var selected = PointsGraphSeries<DataPoint>()


class WelchFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_welch, container, false)
        graph = root.findViewById(R.id.graph_welch)
        seriesx = LineGraphSeries<DataPoint>()
        seriesy = LineGraphSeries<DataPoint>()
        seriesz = LineGraphSeries<DataPoint>()

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true

        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(InfoSingleton.welchF.size.toDouble())
        graph.viewport.setMaxY(60.0)
        graph.viewport.setMinY(0.0)

        for (i in InfoSingleton.welchF.indices) {
            seriesx.appendData(DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchX.get(i)), true, InfoSingleton.welchF.size)
            seriesy.appendData(DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchY.get(i)), true, InfoSingleton.welchF.size)
            seriesz.appendData(DataPoint(InfoSingleton.welchF.get(i), InfoSingleton.welchZ.get(i)), true, InfoSingleton.welchF.size)
        }

        graph.addSeries(seriesy)
        graph.addSeries(seriesz)
        graph.addSeries(seriesx)
        graph.viewport.setScalable(true)
        graph.viewport.setScalableY(true)
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true
        graph.viewport.setScrollableY(true)

        seriesx.setOnDataPointTapListener { series, dataPoint -> onTap(series, dataPoint) }

        return root
    }

    fun onTap(ser: Series<DataPointInterface>, dataPointInterface: DataPointInterface) {
        var msg = "X:" + dataPointInterface.x + "\nY:" + dataPointInterface.y
        graph.removeAllSeries()

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
        }

        graph.addSeries(selected)

            graph.addSeries(seriesy)
            graph.addSeries(seriesz)
        graph.addSeries(seriesx)


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


}

/*
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
*/