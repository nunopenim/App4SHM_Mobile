package org.app4shm.demo.ui.welch

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import org.app4shm.demo.Data
import org.app4shm.demo.R

lateinit var graph: GraphView
var readings = arrayListOf<Data>()
var series = LineGraphSeries<DataPoint>()
var selected = PointsGraphSeries<DataPoint>()


class WelchFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_welch, container, false)
        graph = root.findViewById(R.id.graph_welch)
        series = LineGraphSeries<DataPoint>(getDataPoint())
        graph.addSeries(series)



        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true

        graph.viewport.setMinX(3.0)
        graph.viewport.setMaxX(6.0)

        //graph.addSeries(series)
        //graph.viewport.setScalable(true)
        //graph.viewport.setScalableY(true)
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true
        //graph.viewport.setScrollableY(true)

        series.setOnDataPointTapListener { series, dataPoint -> onTap(dataPoint) }

        return root
    }

    fun onTap(dataPointInterface: DataPointInterface) {
        var msg = "X:" + dataPointInterface.x + "\nY:" + dataPointInterface.y
        graph.removeAllSeries()
        selected = PointsGraphSeries<DataPoint>()
        selected.appendData(DataPoint(dataPointInterface.x, dataPointInterface.y),false,1)
        graph.addSeries(selected)
        selected.color = Color.RED
        selected.size = 10.0F


        graph.addSeries(series)


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