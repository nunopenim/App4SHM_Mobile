package org.app4shm.demo
import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class LocationServices : LocationListener {

    override fun onLocationChanged(loc: Location) {
        location = loc
        loc.getLatitude()
        loc.getLongitude()
        latitude = loc.getLatitude()
        longitude = loc.getLongitude()
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        var latitude = 0.0
        var longitude = 0.0
        var location: Location? = null
    }
}