package com.monsalud.fivedayforecaster.data.datasource.remote

import com.monsalud.fivedayforecaster.data.RemoteDataSource
import com.monsalud.fivedayforecaster.data.utils.DataConstants
import com.monsalud.fivedayforecaster.data.utils.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.errors.IOException

class RemoteDataSourceImpl(
    private val client: HttpClient
) : RemoteDataSource {

    override suspend fun getGeocodingResponseFromApi(zipCode: String): Result<String> {
        try {
            val geoString = client.get<String>("https://api.openweathermap.org/geo/1.0/zip") {
                contentType(ContentType.Application.Json)
                parameter("zip", zipCode)
                parameter("appid", DataConstants.GEOAPIKEY)
            }
            return Result.success(geoString)
        } catch (e: IOException) {
            return Result.failure(DataError.Network(e))
        } catch (e: Exception) {
            return Result.failure(DataError.Unknown(e))
        }
    }

    override suspend fun getWeatherResponseFromApi(lat: String, lon: String): Result<String> {
        try {
            val weatherString =
                client.get<String>("https://api.openweathermap.org/data/2.5/forecast") {
                    contentType(ContentType.Application.Json)
                    parameter("lat", lat)
                    parameter("lon", lon)
                    parameter("appid", DataConstants.WEATHERAPIKEY)
                }
            return Result.success(weatherString)
        } catch (e: IOException) {
            return Result.failure(DataError.Network(e))
        } catch (e: Exception) {
            return Result.failure(DataError.Unknown(e))
        }
    }
}