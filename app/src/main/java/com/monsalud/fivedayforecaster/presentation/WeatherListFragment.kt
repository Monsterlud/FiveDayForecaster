package com.monsalud.fivedayforecaster.presentation

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.monsalud.fivedayforecaster.R
import com.monsalud.fivedayforecaster.data.datasource.local.FiveDayWeatherResult
import com.monsalud.fivedayforecaster.data.datasource.local.LocationState
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.data.utils.NetworkUtils
import com.monsalud.fivedayforecaster.databinding.FragmentListWeatherBinding
import com.monsalud.fivedayforecaster.presentation.utils.NavigationCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherListFragment : Fragment(), WeatherListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentListWeatherBinding

    private val viewModel by viewModel<WeatherViewModel>()
    private val networkUtils by inject<NetworkUtils>()
    private val weatherListAdapter = WeatherListAdapter(FiveDayWeatherResult(emptyList()))

    private lateinit var networkUnavailableDialog: AlertDialog.Builder

    private var savedZipCode: String? = null
    private var savedLocationDisplay: String? = null
    private var savedWeatherResult: FiveDayWeatherResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.five_day_forecast)
        viewModel.initialize()

        savedInstanceState?.let { bundle ->
            savedZipCode = bundle.getString("saved_zip_code")
            savedLocationDisplay = bundle.getString("saved_location_display")
            savedWeatherResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("saved_weather_result", FiveDayWeatherResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable("saved_weather_result")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("saved_zip_code", savedZipCode)
        outState.putString("saved_location_display", binding.locationDisplay.text.toString())
        savedWeatherResult?.let { result ->
            outState.putParcelable("saved_weather_result", result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("WeatherListFragment", "onCreateView called")

        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkUnavailableDialog = AlertDialog.Builder(requireContext())
            .setTitle("Network Error")
            .setMessage("Network is not available. Last known forecast will be displayed.")
            .setIcon(R.drawable.error)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

        // Inflate the layout for this fragment
        binding = FragmentListWeatherBinding.inflate(inflater, container, false)
        updateLocationDisplay(LocationState.Loading)

        // Start observing locationState immediately
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("WeatherListFragment", "Starting to collect locationState")
                viewModel.locationState.collect { state ->
                    Log.d("WeatherListFragment", "Collected location state: $state")
                    updateLocationDisplay(state)
                }
            }
        }

        binding.btnNewForecast.setOnClickListener {
            clearRecyclerView()
            findNavController().popBackStack()
        }

        setupObservers()

        if (!networkUtils.hasInternetConnection(connectivityManager)) {
            networkUnavailableDialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvWeatherList
        recyclerView.layoutManager = LinearLayoutManager(activity)

        weatherListAdapter.setOnItemClickListener(this)
        Log.d("WeatherListFragment", "OnItemClickListener set on adapter")
        binding.rvWeatherList.adapter = weatherListAdapter

        savedLocationDisplay?.let {
            binding.locationDisplay.text = it
        }

        savedWeatherResult?.let {
            updateWeatherDisplay(it)
        } ?: run {
            savedZipCode?.let { zipCode ->
                fetchWeatherForecast(zipCode)
            } ?: run {
                val args: WeatherListFragmentArgs by navArgs()
                fetchWeatherForecast(args.zipCode)
            }
        }
    }

    private fun fetchWeatherForecast(zipCode: String) {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("WeatherListFragment", "Starting to fetch forecast")
                binding.rvWeatherList.isVisible = false
                binding.progressBar.isVisible = true

                viewModel.saveFiveDayWeatherForecast(zipCode, connectivityManager)

                viewModel.weatherForecast.collect { fiveDayWeatherResult ->
                    Log.d("WeatherListFragment", "Received weather forecast, updating UI")
                    updateWeatherDisplay(fiveDayWeatherResult)
                }
            }
        }
    }



    private fun updateWeatherDisplay(fiveDayWeatherResult: FiveDayWeatherResult) {
        weatherListAdapter.updateData(fiveDayWeatherResult)
        binding.progressBar.isVisible = false
        binding.rvWeatherList.isVisible = true
        savedWeatherResult = fiveDayWeatherResult
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("WeatherListFragment", "Starting to collect locationState")
                viewModel.locationState.collect { state ->
                    Log.d("WeatherListFragment", "Collected location state: $state")
                    updateLocationDisplay(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showToast.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationCommand.collect { command ->
                    when (command) {
                        is NavigationCommand.To -> findNavController().navigate(command.directions)
                        is NavigationCommand.Back -> findNavController().popBackStack()
                        is NavigationCommand.BackTo -> findNavController().popBackStack(
                            command.destinationId,
                            false
                        )
                    }
                }
            }
        }
    }


    override fun onItemClick(weatherEntity: WeatherEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateFromWeatherListFragmentToWeatherDetailFragment(weatherEntity)
        }
    }

    private fun clearRecyclerView() {
        binding.locationDisplay.text = ""
        binding.rvWeatherList.adapter = WeatherListAdapter(FiveDayWeatherResult(emptyList()))
        (binding.rvWeatherList.adapter as WeatherListAdapter).notifyDataSetChanged()
    }

    private fun updateLocationDisplay(state: LocationState) {
        val displayText = when (state) {
            is LocationState.Loading -> "Loading location..."
            is LocationState.Loaded -> "Forecast for ${state.location}"
            is LocationState.Error -> "Unable to load location"
        }
        Log.d("WeatherListFragment", "Updating location display to: $displayText")
        binding.locationDisplay.post {
            binding.locationDisplay.text = displayText
            Log.d(
                "WeatherListFragment",
                "Location display text set to: ${binding.locationDisplay.text}"
            )
        }
    }
}
