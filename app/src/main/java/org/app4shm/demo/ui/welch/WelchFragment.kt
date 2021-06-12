package org.app4shm.demo.ui.welch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.app4shm.demo.Data
import org.app4shm.demo.R

lateinit var graph: GraphView
var readings = arrayListOf<Data>()
var series = LineGraphSeries<DataPoint>()

class WelchFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_welch, container, false)
        org.app4shm.demo.ui.home.graph = root.findViewById(R.id.graph_welch)

        graph.addSeries(series)

        return root
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