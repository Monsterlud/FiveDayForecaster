package com.monsalud.fivedayforecaster.data

import com.google.gson.Gson
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.data.datasource.remote.GeocodingApiResponse
import com.monsalud.fivedayforecaster.domain.WeatherRepository
import kotlinx.coroutines.flow.Flow

class WeatherListRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val gson: Gson,
) : WeatherRepository {

    override suspend fun getAndSaveFiveDayWeatherForecast(zipCode: String): String {
        val geoResult =
            gson.fromJson(getGeocodingResponseJson(zipCode).getOrNull(), GeocodingApiResponse::class.java)
        val weatherJson = getWeatherResponseJson(geoResult.lat, geoResult.lon).getOrNull()
        cacheWeatherResponseJson(weatherJson)

        val location = "${geoResult.name}, ${geoResult.country}"
        localDataSource.saveLocation(location)
        return location
    }

    /**
     * Local Data From Room
     */

    override suspend fun cacheWeatherResponseJson(value: String?) {
        if (!value.isNullOrEmpty()) {
            return localDataSource.saveWeatherForecast(value)
        } else {
            throw Exception("Cannot save null value or empty string.")
        }
    }

    override fun retrieveWeatherResponseJson(): Flow<List<WeatherEntity>> {
        return localDataSource.getWeatherForecast()
    }

    override fun retrieveLocation(): Flow<String> =
        localDataSource.getLocation()

    /**
     * Remote Data From APIs
     */
    override suspend fun getGeocodingResponseJson(
        zipCode: String,
    ): Result<String?> {
        return remoteDataSource.getGeocodingResponseFromApi(zipCode)
    }

    override suspend fun getWeatherResponseJson(
        lat: Double,
        lon: Double,
    ): Result<String?> {
        return remoteDataSource.getWeatherResponseFromApi(lat.toString(), lon.toString())
    }


}