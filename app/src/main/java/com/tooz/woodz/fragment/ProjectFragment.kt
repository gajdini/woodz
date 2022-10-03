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
import com.tooz.woodz.adapter.ProjectAdapter
import com.tooz.woodz.databinding.ProjectFragmentBinding
import com.tooz.woodz.viewmodel.ProjectViewModel
import com.tooz.woodz.viewmodel.ProjectViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class ProjectFragment: Fragment() {

    private var _binding: ProjectFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private val viewModel: ProjectViewModel by activityViewModels{
        ProjectViewModelFactory(
            (activity?.application as WoodzApplication).database.projectDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProjectFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val projectAdapter = ProjectAdapter({
            val action = ProjectFragmentDirections.actionProjectFragmentToMaterialFragment(projectId = it.id, projectName = it.name)
            view.findNavController().navigate(action)
        })
        recyclerView.adapter = projectAdapter

        lifecycle.coroutineScope.launch {
            viewModel.allProjects().collect() {
                projectAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
