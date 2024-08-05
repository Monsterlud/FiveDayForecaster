package com.monsalud.fivedayforecaster.data.datasource.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "dt_txt")
    var dt_txt: String = "",
    @ColumnInfo(name = "temp")
    var temp: Double = 0.0,
    @ColumnInfo(name = "humidity")
    var humidity: Int = 100,
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "clouds")
    var all: Int = 0,
    @ColumnInfo(name = "wind_speed")
    var speed: Double = 0.0,
    @ColumnInfo(name = "visibility")
    var visibility: Int = 0,
    @ColumnInfo(name = "icon")
    var icon: String = ""
) : Parcelable

data class FiveDayWeatherResult(
    var list: List<WeatherEntity>
)
