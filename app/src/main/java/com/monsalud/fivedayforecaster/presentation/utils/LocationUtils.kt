package com.monsalud.fivedayforecaster.presentation.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("MissingPermission")
class LocationUtils(
    private val context: Context
) {

    suspend fun getZipCodeFromDeviceLocation(): Result<String> = suspendCoroutine { continuation ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val zipCode = getZipCodeFromLocation(location)
                    continuation.resume(Result.success(zipCode))
                } else {
                    continuation.resume(Result.failure(Exception("Location not available")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    private fun getZipCodeFromLocation(location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var zipCode = ""
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                zipCode = addresses[0].postalCode
            }
            return zipCode
        } catch (e: Exception) {
            Log.e("LocationUtils", "Error getting zip code: ${e.message}")
        }
        return zipCode
    }
}