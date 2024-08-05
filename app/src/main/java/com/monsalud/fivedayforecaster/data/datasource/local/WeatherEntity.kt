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

@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 1,
    @ColumnInfo(name = "location")
    var location: String = "",
)

sealed class LocationState {
    object Loading : LocationState()
    data class Loaded(val location: String) : LocationState()
    object Error : LocationState()
}
