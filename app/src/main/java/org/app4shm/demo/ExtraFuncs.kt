package org.app4shm.demo

fun listStringificator(list: ArrayList<String>): String {
    var str = ""
    for (i in list) {
        str += i
    }
    return str
}

fun makeMeAJson(list: ArrayList<Data>) : String {
    var str = "["
    for (i in list) {
        str += i.JSONer() + ","
    }
    str = str.dropLast(1)
    str += "]"
    return str
}