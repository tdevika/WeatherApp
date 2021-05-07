package com.wednesday.template.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wednesday.template.adapter.CitiesAdapter
import com.wednesday.template.adapter.CitySelected
import com.wednesday.template.databinding.FragmentCitiesBinding
import com.wednesday.template.model.City
import com.wednesday.template.viewmodel.CityViewModel
import com.wednesday.template.viewmodel.CityViewModelFactory
import kotlinx.android.synthetic.main.fragment_cities.*
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class CitiesFragment : Fragment(), CitySelected, DIAware {

    override val di by di()
    private val viewModeFactory: CityViewModelFactory by instance("cityViewModelFactory")

    private lateinit var viewModel: CityViewModel
    private val citiesAdapter: CitiesAdapter by lazy { CitiesAdapter(this) }

    lateinit var binding: FragmentCitiesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModeFactory).get(CityViewModel::class.java)
        binding = FragmentCitiesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setRecyclerView()
        setObserver()
    }

    private fun setToolbar() {
        binding.searchCityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    citiesRecyclerView.visibility = View.GONE
                } else {
                    citiesRecyclerView.visibility = View.VISIBLE
                    viewModel.fetchCities(s.toString())
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        citiesRecyclerView.layoutManager = linearLayoutManager
        citiesRecyclerView.adapter = citiesAdapter
    }

    private fun setObserver() {
        viewModel.searchCities.observe(viewLifecycleOwner, Observer {
            it?.let { citiesAdapter.submitList(it) }
        })
    }

    override fun onCitySelected(city: City) {
        viewModel.markCityAsFavorite(city)
    }
}
