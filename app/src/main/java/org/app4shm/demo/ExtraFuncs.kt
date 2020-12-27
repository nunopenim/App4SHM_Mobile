package org.app4shm.demo

fun listStringificator(list: ArrayList<String>): String {
    var str = ""
    for (i in list) {
        str += i
    }
    return str
}