package com.monsalud.fivedayforecaster.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.monsalud.fivedayforecaster.R
import com.monsalud.fivedayforecaster.databinding.FragmentWeatherDetailBinding
import com.monsalud.fivedayforecaster.presentation.UiConstants.ICON_BASE_URL
import com.monsalud.fivedayforecaster.presentation.UiConstants.ICON_EXTENSION
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class WeatherDetailFragment : Fragment((R.layout.fragment_weather_detail)) {

    var binding: FragmentWeatherDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: WeatherDetailFragmentArgs by navArgs()
        val weatherEntity = args.weatherEntity

        val imageView = binding?.weatherIcon

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val weatherDate = LocalDateTime.parse(weatherEntity.dt_txt, formatter)
        val ldtFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formattedDate = weatherDate.format(ldtFormatter)

        val kToF = ((1.8 * (weatherEntity.temp-273)) + 32).toInt()

        weatherEntity.let {
            binding?.dateTime?.text = formattedDate.toString()
            binding?.descriptionText?.text = it.description
            binding?.temperatureText?.text = getString(R.string.temperature, kToF)
            binding?.humidityText?.text = getString(R.string.humidity, it.humidity)
            binding?.cloudCoverText?.text = getString(R.string.cloud_cover, it.all)
            binding?.windSpeedText?.text = getString(R.string.wind_speed, it.speed.toInt())
            binding?.visibilityText?.text = getString(R.string.visibility, it.visibility)

            Picasso.with(requireContext())
                .load(ICON_BASE_URL + it.icon + ICON_EXTENSION)
                .placeholder(R.drawable.image_default)
                .error(R.drawable.error)
                .into(imageView)
        }
    }
}
