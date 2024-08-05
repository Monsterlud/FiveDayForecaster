package com.monsalud.fivedayforecaster

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.monsalud.fivedayforecaster.presentation.NavigationCommand
import com.monsalud.fivedayforecaster.presentation.WeatherConstants.REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE
import com.monsalud.fivedayforecaster.presentation.WeatherViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by inject()

    private lateinit var permissionsExplanationDialog: AlertDialog.Builder
    private lateinit var permissionsErrorDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        permissionsExplanationDialog = AlertDialog.Builder(this)
            .setTitle("Location Permissions")
            .setMessage("You must grant location permission to get a weather forecast using this feature")
            .setIcon(R.drawable.ic_location)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

        permissionsErrorDialog = AlertDialog.Builder(this)
            .setTitle("Permissions Error")
            .setMessage("There was an error getting the location permissions")
            .setIcon(R.drawable.error)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationCommand.collect { command ->
                    when (command) {
                        is NavigationCommand.To -> navController.navigate(command.directions)
                        is NavigationCommand.Back -> navController.popBackStack()
                        is NavigationCommand.BackTo -> navController.popBackStack(
                            command.destinationId,
                            false
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(TAG, "onRequestPermissionsResult called with requestCode: $requestCode")
        Log.d(TAG, "Permissions: ${permissions.joinToString()}")
        Log.d(TAG, "GrantResults: ${grantResults.joinToString()}")

        when (requestCode) {
            REQUEST_FOREGROUND_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "foreground location permissions granted")
                    // permission is granted, now get zip code using location and pass to viewmodel
                    viewModel.navigateFromWeatherLocationFragmentToWeatherListFragment("80304")
                } else {
                    Log.d(TAG, "foreground location permissions denied")
                    permissionsExplanationDialog.show()
                }
            }
            else -> {
                permissionsErrorDialog.show()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
