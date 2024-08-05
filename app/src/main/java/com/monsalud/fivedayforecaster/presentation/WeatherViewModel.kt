package com.monsalud.fivedayforecaster.presentation

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monsalud.fivedayforecaster.data.datasource.local.FiveDayWeatherResult
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.data.utils.NetworkUtils
import com.monsalud.fivedayforecaster.domain.WeatherRepository
import com.monsalud.fivedayforecaster.presentation.utils.NavigationCommand
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherListRepository: WeatherRepository,
    private val networkUtils: NetworkUtils,
) : ViewModel() {

    var location: String? = null

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
            val action = WeatherLocationFragmentDirections.actionWeatherLocationFragmentToWeatherListFragment(zipCode)
            _navigationCommand.emit(NavigationCommand.To(action))
        }
    }

    fun navigateFromWeatherListFragmentToWeatherDetailFragment(weatherEntity: WeatherEntity) {
        viewModelScope.launch {
            val action = WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailFragment(weatherEntity)
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

    /**
     * State Flow For UI to use
     */
    private val _weatherForecast = MutableStateFlow<FiveDayWeatherResult>(FiveDayWeatherResult(emptyList()))
    val weatherForecast = _weatherForecast.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            weatherListRepository.retrieveWeatherResponseJson().collect { weatherForecast ->
                _weatherForecast.value = FiveDayWeatherResult(weatherForecast)
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
            location = weatherListRepository.getAndSaveFiveDayWeatherForecast(zipCode)
        }
    }
}
