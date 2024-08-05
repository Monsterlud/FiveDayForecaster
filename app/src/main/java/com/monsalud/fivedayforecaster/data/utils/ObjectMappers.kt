package com.monsalud.fivedayforecaster.data.utils

interface ObjectMappers <OpenWeatherApiResponse, WeatherEntity> {
    fun mapFromDtoToEntity(dto: OpenWeatherApiResponse) : WeatherEntity
}