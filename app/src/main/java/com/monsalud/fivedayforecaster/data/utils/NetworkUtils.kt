package com.monsalud.fivedayforecaster.data.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkUtils {

    fun hasInternetConnection(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    fun isNetworkAvailable(
        connectivityManager: ConnectivityManager
    ) : Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}