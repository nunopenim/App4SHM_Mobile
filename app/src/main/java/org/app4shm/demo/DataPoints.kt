package org.app4shm.demo

class DataPoints (val id: String, val t: Double, val x: Double, val y: Double, val z: Double, val group: String) {
    override fun toString(): String {
        return "ID: ${id} | group: ${group} | t: ${t} | X: ${x} | Y: ${y} | Z: ${z}\n"
    }

    fun JSONer(): String {
        return "{\"id\": \"${id}\", \"t\": ${t}, \"x\": ${x}, \"y\": ${y}, \"z\": ${z}, \"group\": \"${group}\"}"
    }
}