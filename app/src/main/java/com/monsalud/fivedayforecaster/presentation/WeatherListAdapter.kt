package com.monsalud.fivedayforecaster.presentation

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.monsalud.fivedayforecaster.data.datasource.local.FiveDayWeatherResult
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.databinding.ItemListWeatherBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class WeatherListAdapter(
    private var fiveDayWeatherResult: FiveDayWeatherResult
) : RecyclerView.Adapter<WeatherListAdapter.WeatherItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(weatherEntity: WeatherEntity)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
        Log.d("WeatherListAdapter", "OnItemClickListener set: $listener")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherItemViewHolder {
        val binding = ItemListWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeatherItemViewHolder, position: Int) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(fiveDayWeatherResult.list[position].dt_txt, formatter)
        val ldtFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formattedDate = dateTime.format(ldtFormatter)

        val kToF = ((1.8 * (fiveDayWeatherResult.list[position].temp-273)) + 32).toInt()
        val temp = "Temperature: $kToF F"

        val humidity = "Humidity: ${fiveDayWeatherResult.list[position].humidity}%"

        val weatherDescription = "Forecast: ${fiveDayWeatherResult.list[position].description}"

        holder.binding.dayTime.text = formattedDate.toString()
        holder.binding.temperature.text = temp
        holder.binding.humidity.text = humidity
        holder.binding.weatherDescription.text = weatherDescription

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(fiveDayWeatherResult.list[position])
            }
        } else {
            Log.d("WeatherListAdapter", "OnItemClickListener is null")
        }
    }

    fun updateData(newData: FiveDayWeatherResult) {
        fiveDayWeatherResult = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fiveDayWeatherResult.list.size

    class WeatherItemViewHolder(val binding: ItemListWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

}

