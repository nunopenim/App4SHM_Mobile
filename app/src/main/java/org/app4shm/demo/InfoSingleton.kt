package org.app4shm.demo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

object InfoSingleton {
    var group = "testgroup"
    var username = "unknown"
    var IP = "10.0.2.2:8080"

    var response: Response? = null

    var welchX = ArrayList<Double>()
    var welchY = ArrayList<Double>()
    var welchZ = ArrayList<Double>()
    var welchF = ArrayList<Double>()

    fun changeName(new_name: String) : Boolean {
        if (new_name == "") {
            return false
        }
        username = new_name
        return true
    }

    fun changeIP(new_name: String) : Boolean {
        if (new_name == "") {
            return false
        }
        IP = new_name
        return true
    }

    fun changeGroup(new_name: String) : Boolean {
        if (new_name == "") {
            return false
        }
        group = new_name
        return true
    }

    fun processRecievedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val strs = response!!.body!!.string().split("],[")
            val strslist = ArrayList<String>()
            for (i in strs.indices) {
                var str = strs[i].replace("[","")
                str = str.replace("]", "")
                strslist.add(str)
            }
            val welch_f = ArrayList<Double>()
            val welch_x = ArrayList<Double>()
            val welch_y = ArrayList<Double>()
            val welch_z = ArrayList<Double>()
            for (i in strslist.indices) {
                val values_str = strslist[i].split(",")
                for (j in values_str) {
                    if (i == 0) {
                        welch_f.add(j.toDouble())
                    }
                    else if(i == 1) {
                        welch_x.add(j.toDouble())
                    }
                    else if (i == 2) {
                        welch_y.add(j.toDouble())
                    }
                    else if (i == 3) {
                        welch_z.add(j.toDouble())
                    }
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                welchF = welch_f
                welchX = welch_x
                welchY = welch_y
                welchZ = welch_z
            }
        }
    }

}