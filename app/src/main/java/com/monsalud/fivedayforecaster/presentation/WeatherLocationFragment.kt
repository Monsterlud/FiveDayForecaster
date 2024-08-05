package com.monsalud.fivedayforecaster.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.monsalud.fivedayforecaster.databinding.FragmentWeatherLocationBinding
import com.monsalud.fivedayforecaster.presentation.utils.LocationUtils
import com.monsalud.fivedayforecaster.presentation.utils.WeatherConstants.ZIP_REGEX
import com.monsalud.fivedayforecaster.presentation.utils.NavigationCommand
import com.monsalud.fivedayforecaster.presentation.utils.PermissionsHandler
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherLocationFragment : Fragment() {
    private lateinit var binding: FragmentWeatherLocationBinding

    private val viewModel by viewModel<WeatherViewModel>()
    private val locationUtils: LocationUtils by inject<LocationUtils>()
    private val permissionsHandler: PermissionsHandler by inject<PermissionsHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding = FragmentWeatherLocationBinding.inflate(
            inflater,
            container,
            false
        )

        binding.zipCodeSubmitButton.setOnClickListener {
            val zipValidator = Pattern.compile(ZIP_REGEX)
            val zipCode = binding.etZipCode.text.toString()
            val matcher = zipValidator.matcher(zipCode)

            if (!matcher.matches()) {
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                lifecycleScope.launch {
                    viewModel.showToast("Invalid Zip Code")
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.navigateFromWeatherLocationFragmentToWeatherListFragment(zipCode)
                }
            }
        }

        binding.recyclerClearButton.setOnClickListener {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            lifecycleScope.launch {
                viewModel.showToast("Zip Code Cleared")
            }
            binding.etZipCode.text = null
        }

        binding.btnUseLocation.setOnClickListener {
            if (permissionsHandler.isForegroundLocationPermissionGranted(requireActivity())) {
                // permissions are already granted...get zip code from device location
                lifecycleScope.launch {
                    val result = locationUtils.getZipCodeFromDeviceLocation()
                    if (result.isSuccess) {
                        // then navigate to weather list fragment and pass this zip code as an argument
                        val zipCode = result.getOrNull().toString()
                        viewModel.navigateFromWeatherLocationFragmentToWeatherListFragment(zipCode)
                    } else {
                        Log.e("WeatherLocationFragment", "Error getting location: ${result.exceptionOrNull()}")
                        viewModel.showToast("Error getting location")
                    }
                }
            }
            permissionsHandler.requestForegroundLocationPermission(requireActivity())
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

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        binding.etZipCode.text = null
    }
}



