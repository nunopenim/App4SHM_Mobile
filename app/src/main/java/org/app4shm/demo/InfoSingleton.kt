package org.app4shm.demo

import okhttp3.Response

object InfoSingleton {
    var group = "testgroup"
    var username = "unknown"
    var IP = "95.94.8.193"

    var response: Response? = null

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
}