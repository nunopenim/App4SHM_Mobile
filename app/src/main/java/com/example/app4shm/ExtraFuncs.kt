package com.example.app4shm

fun listStringificator(list: ArrayList<String>): String {
    var str = ""
    for (i in list) {
        str += i
    }
    return str
}