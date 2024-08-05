package com.monsalud.fivedayforecaster.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.monsalud.fivedayforecaster.R
import com.monsalud.fivedayforecaster.data.datasource.local.FiveDayWeatherResult
import com.monsalud.fivedayforecaster.data.datasource.local.WeatherEntity
import com.monsalud.fivedayforecaster.databinding.FragmentListWeatherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherListFragment : Fragment(), WeatherListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentListWeatherBinding

    private val viewModel by viewModel<WeatherViewModel>()
    private val weatherListAdapter = WeatherListAdapter(FiveDayWeatherResult(emptyList()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.five_day_forecast)
        viewModel.initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Inflate the layout for this fragment
        binding = FragmentListWeatherBinding.inflate(
            inflater,
            container,
            false
        )

        binding.btnNewForecast.setOnClickListener {
            clearRecyclerView()
            findNavController().popBackStack()
        }

        val args: WeatherListFragmentArgs by navArgs()
        val zipCode = args.zipCode

        viewLifecycleOwner.lifecycleScope.launch {
            binding.rvWeatherList.isVisible = false
            binding.progressBar.isVisible = true
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    viewModel.saveFiveDayWeatherForecast(
                        zipCode, connectivityManager
                    )

                    viewModel.weatherForecast.collect { fiveDayWeatherResult ->
                        withContext(Dispatchers.Main) {
                            binding.locationDisplay.setTextColor(resources.getColor(R.color.weather_dark))
                            weatherListAdapter.updateData(fiveDayWeatherResult)
                            binding.progressBar.isVisible = false
                            binding.rvWeatherList.isVisible = true
                            binding.locationDisplay.text =
                                "Weather forecast for ${viewModel.location}"
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showToast.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationCommand.collect { command ->
                    when (command) {
                        is NavigationCommand.To -> findNavController().navigate(command.directions)
                        is NavigationCommand.Back -> findNavController().popBackStack()
                        is NavigationCommand.BackTo -> findNavController().popBackStack(
                            command.destinationId,
                            false
                        )
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvWeatherList
        recyclerView.layoutManager = LinearLayoutManager(activity)



        weatherListAdapter.setOnItemClickListener(this)
        Log.d("WeatherListFragment", "OnItemClickListener set on adapter")
        binding.rvWeatherList.adapter = weatherListAdapter
    }

    override fun onItemClick(weatherEntity: WeatherEntity) {
        val action =
            WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailFragment(
                weatherEntity
            )
        findNavController().navigate(action)
    }

    private fun clearRecyclerView() {
        binding.locationDisplay.text = ""
        binding.rvWeatherList.adapter = WeatherListAdapter(FiveDayWeatherResult(emptyList()))
        (binding.rvWeatherList.adapter as WeatherListAdapter).notifyDataSetChanged()
    }
}
