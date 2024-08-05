package com.monsalud.fivedayforecaster.presentation

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.monsalud.fivedayforecaster.presentation.WeatherConstants.REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE

class PermissionsHandler {

    fun isForegroundLocationPermissionGranted(context: Context): Boolean {
        val response = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return response
    }

    @TargetApi(29)
    fun requestForegroundLocationPermission(activity: Activity) {
        if (isForegroundLocationPermissionGranted(activity)) return

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE
        )
    }
}