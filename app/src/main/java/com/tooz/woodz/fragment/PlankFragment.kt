package com.tooz.woodz.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tooz.woodz.WoodzApplication
import com.tooz.woodz.adapter.PlankAdapter
import com.tooz.woodz.databinding.PlankFragmentBinding
import com.tooz.woodz.viewmodel.PlankViewModel
import com.tooz.woodz.viewmodel.PlankViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class PlankFragment: Fragment() {

    companion object {
        var MATERIAL_ID = "materialId"
        var MATERIAL_NAME = "materialName"
    }

    private val viewModel: PlankViewModel by activityViewModels {
        PlankViewModelFactory(
            (activity?.application as WoodzApplication).database.plankDao()
        )
    }

    private var _binding: PlankFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private var materialId: Int = 0

    private lateinit var materialName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            materialId = it.getInt(MATERIAL_ID)
            materialName = it.getString(MATERIAL_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PlankFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val plankAdapter = PlankAdapter({})
        recyclerView.adapter = plankAdapter

        lifecycle.coroutineScope.launch {
            viewModel.planksByMaterialId(materialId).collect() {
                plankAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
