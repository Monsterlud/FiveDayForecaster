package com.monsalud.fivedayforecaster.presentation

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monsalud.fivedayforecaster.data.datasource.local.FiveDayWeatherResult
import com.monsalud.fivedayforecaster.data.datasource.local.LocationState
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.data.utils.NetworkUtils
import com.monsalud.fivedayforecaster.domain.WeatherRepository
import com.monsalud.fivedayforecaster.presentation.utils.NavigationCommand
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherListRepository: WeatherRepository,
    private val networkUtils: NetworkUtils,
) : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Loading)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)

    val combinedLocationState =
        combine(_locationState, _isInitialized) { locationState, isInitialized ->
            if (isInitialized) {
                locationState
            } else {
                LocationState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocationState.Loading
        )

    /** Toast Flow Setup */
    private val _showToast = MutableSharedFlow<String>()
    val showToast = _showToast.asSharedFlow()

    suspend fun showToast(message: String) {
        _showToast.emit(message)
    }

    /** Navigation Flow Setup */
    private val _navigationCommand = MutableSharedFlow<NavigationCommand>()
    val navigationCommand = _navigationCommand.asSharedFlow()

    fun navigateFromWeatherLocationFragmentToWeatherListFragment(zipCode: String) {
        viewModelScope.launch {
            val action =
                WeatherLocationFragmentDirections.actionWeatherLocationFragmentToWeatherListFragment(
                    zipCode
                )
            _navigationCommand.emit(NavigationCommand.To(action))
        }
    }

    fun navigateFromWeatherListFragmentToWeatherDetailFragment(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            val action =
                WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailFragment(
                    weatherEntity
                )
            _navigationCommand.emit(NavigationCommand.To(action))
        }
    }

    /** Zip Code Flow Setup */
    private val _zipCode = MutableStateFlow("")
    val zipCode: StateFlow<String>
        get() = _zipCode

    fun setZipCode(zipCode: String) {
        _zipCode.value = zipCode
    }

    init {
        Log.d("WeatherViewModel", "ViewModel initialized with state: ${_locationState.value}")
    }

    /**
     * State Flow For UI to use
     */
    private val _weatherForecast =
        MutableStateFlow<FiveDayWeatherResult>(FiveDayWeatherResult(emptyList()))
    val weatherForecast = _weatherForecast.asStateFlow()


    fun initialize() {
        viewModelScope.launch {
            launch {
                weatherListRepository.retrieveWeatherResponseJson().collect { weatherForecast ->
                    _weatherForecast.value = FiveDayWeatherResult(weatherForecast)
                }
            }
            launch {
                _locationState.value = LocationState.Loading
                weatherListRepository.retrieveLocation().collect { location ->
                    Log.d("WeatherViewModel", "location: $location")
                    if (location.isNotBlank()) {
                        _locationState.value = LocationState.Loaded(location)
                    } else {
                        Log.d("WeatherViewModel", "weatherListRepository.retrieveLocation() returned LocationState.Error")
                        _locationState.value = LocationState.Loaded(location)
                    }
                }
            }
        }
    }

    /**
     * Save Five Day Weather Forecast
     */
    suspend fun saveFiveDayWeatherForecast(
        zipCode: String,
        connectivityManager: ConnectivityManager
    ) {
        if (networkUtils.hasInternetConnection(connectivityManager)) {
            _locationState.value = LocationState.Loading
            try {
                val location = weatherListRepository.getAndSaveFiveDayWeatherForecast(zipCode)
                _locationState.value = LocationState.Loaded(location)
            } catch (e: Exception) {
                _locationState.value = LocationState.Error
                _showToast.emit("Error loading forecast: ${e.message}")
            }
        } else {
            Log.d("WeatherViewModel", "No network connection causing LocationState.Error")
        }
        _isInitialized.value = true
    }
}
