package com.tooz.woodz.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tooz.woodz.WoodzApplication
import com.tooz.woodz.adapter.MaterialAdapter
import com.tooz.woodz.databinding.MaterialFragmentBinding
import com.tooz.woodz.viewmodel.MaterialViewModel
import com.tooz.woodz.viewmodel.MaterialViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MaterialFragment: Fragment() {

    companion object {
        var PROJECT_ID = "projectId"
        var PROJECT_NAME = "projectName"
    }

    private val viewModel: MaterialViewModel by activityViewModels {
        MaterialViewModelFactory(
            (activity?.application as WoodzApplication).database.materialDao()
        )
    }

    private var _binding: MaterialFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private var projectId: Int = 0

    private lateinit var projectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            projectId = it.getInt(PROJECT_ID)
            projectName = it.getString(PROJECT_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MaterialFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val materialAdapter = MaterialAdapter({
            val action = MaterialFragmentDirections.actionMaterialFragmentToPlankFragment(materialId = it.id, materialName = it.name)
            view.findNavController().navigate(action)
        })
        recyclerView.adapter = materialAdapter
        // submitList() is a call that accesses the database. To prevent the
        // call from potentially locking the UI, you should use a
        // coroutine scope to launch the function. Using GlobalScope is not
        // best practice, and in the next step we'll see how to improve this.
        lifecycle.coroutineScope.launch {
            viewModel.materialsByProjectId(projectId).collect() {
                materialAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
