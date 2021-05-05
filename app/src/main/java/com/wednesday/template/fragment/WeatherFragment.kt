package com.wednesday.template.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wednesday.template.R
import com.wednesday.template.adapter.WeatherAdapter
import com.wednesday.template.databinding.FragmentWeatherBinding
import com.wednesday.template.model.Weather
import com.wednesday.template.util.getList
import com.wednesday.template.viewmodel.UiState
import com.wednesday.template.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_weather.*

class WeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels()

    private val weatherAdapter by lazy { WeatherAdapter() }
    lateinit var binding: FragmentWeatherBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setupRecyclerView()
        setObserver()
        binding.addFavoriteWeatherActionButton.setOnClickListener {
            findNavController().navigate(WeatherFragmentDirections.actionWeatherFragmentToCitiesFragment())
        }
        weatherAdapter.submitList(listOf(Weather("ds", listOf())))
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setObserver() {
        viewModel.weatherList.observe(viewLifecycleOwner, Observer { uiState ->

            uiState?.let {
                when (uiState) {
                    is UiState.Succuss -> {
                        val list = (uiState.data as Iterable<Weather>).toList()
                        weatherAdapter.submitList(list)
                        binding.progress.visibility = View.INVISIBLE
                    }
                    is UiState.Loading ->{
                        binding.progress.visibility = View.VISIBLE
                    }

                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.weatherRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = weatherAdapter
        }
    }
}

