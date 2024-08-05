package com.monsalud.fivedayforecaster.domain

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class WeatherInfo(
    var id: Int = 0,
    var dt_txt: String = "",
    var temp: Double = 0.0,
    var humidity: Int = 100,
    var description: String = "",
    var all: Int = 0,
    var speed: Double = 0.0,
    var visibility: Long = 0,
    var icon: String = ""
)
