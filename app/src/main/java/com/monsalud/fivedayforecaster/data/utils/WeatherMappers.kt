package com.monsalud.fivedayforecaster.data.utils

import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.data.datasource.remote.OpenWeatherApiResponse

class WeatherMappers : ObjectMappers<OpenWeatherApiResponse, WeatherEntity> {

    override fun mapFromDtoToEntity(dto: OpenWeatherApiResponse) : WeatherEntity {
        return WeatherEntity(
            dt_txt = dto.dt_txt,
            temp = dto.main.temp,
            humidity = dto.main.humidity,
            description = dto.weather.first().description,
            all = dto.clouds.all,
            speed = dto.wind.speed,
            visibility = dto.visibility,
            icon = dto.weather.first().icon,
        )
    }
}