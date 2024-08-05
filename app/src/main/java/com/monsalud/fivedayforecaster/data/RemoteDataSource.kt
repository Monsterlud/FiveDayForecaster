package com.monsalud.fivedayforecaster.data

interface RemoteDataSource {

    suspend fun getGeocodingResponseFromApi(zipCode: String) : Result<String?>

    suspend fun getWeatherResponseFromApi(lat: String, lon: String) : Result<String?>
}