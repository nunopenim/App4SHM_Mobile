package org.app4shm.demo

class Data(val id: String, val timeStamp: Long, val x: Float, val y: Float, val z: Float){
    override fun toString(): String {
        return "ID: ${id} | Timestamp: ${timeStamp} | X: ${x} | Y: ${y} | Z: ${z}\n"
    }

    fun JSONer(): String {
        return "{\"id\": \"${id}\", \"timeStamp\": ${timeStamp}, \"x\": ${x}, \"y\": ${y}, \"z\": ${z}}"
    }
}