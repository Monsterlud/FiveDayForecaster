package com.monsalud.fivedayforecaster.data.datasource.remote

/**
 * Geocoding Api Response
 */

data class GeocodingApiResponse(
    var country: String,
    var lat: Double,
    var lon: Double,
    var name: String,
    var zip: String
)

/**
 * Open Weather Api Response
 */

data class FiveDayWeatherResponseFromApi(
    var list: List<OpenWeatherApiResponse>
)

data class OpenWeatherApiResponse(
    var clouds: Clouds,
    var dt: Int,
    var dt_txt: String,
    var main: Main,
    var pop: Double,
    var rain: Rain,
    var sys: Sys,
    var visibility: Int,
    var weather: List<Weather>,
    var wind: Wind
)

data class Clouds(
    var all: Int
)

data class Main(
    var feels_like: Double,
    var grnd_level: Int,
    var humidity: Int,
    var pressure: Int,
    var sea_level: Int,
    var temp: Double,
    var temp_kf: Double,
    var temp_max: Double,
    var temp_min: Double
)

data class Rain(
    var `3h`: Double
)

data class Sys(
    var pod: String
)

data class Weather(
    var description: String,
    var icon: String,
    var id: Int,
    var main: String
)

data class Wind(
    var deg: Int,
    var gust: Double,
    var speed: Double
)