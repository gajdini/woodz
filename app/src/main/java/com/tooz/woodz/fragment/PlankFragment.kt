package com.tooz.woodz.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.viewpager.widget.ViewPager
import com.tooz.woodz.WoodzApplication
import com.tooz.woodz.adapter.PlankAdapter
import com.tooz.woodz.databinding.PlankFragmentBinding
import com.tooz.woodz.viewmodel.PlankViewModel
import com.tooz.woodz.viewmodel.PlankViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener


class PlankFragment: BaseToozifierFragment() {

    companion object {
        var MATERIAL_ID = "materialId"
        var MATERIAL_NAME = "materialName"
    }

    private var beaconAddress: String? = null

    private val viewModel: PlankViewModel by activityViewModels {
        PlankViewModelFactory(
            (activity?.application as WoodzApplication).database.plankDao()
        )
    }

    private var _binding: PlankFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager

    private lateinit var previousButton: ImageView

    private lateinit var nextButton: ImageView

    private var materialId: Int = 0

    private lateinit var materialName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            materialId = it.getInt(MATERIAL_ID)
            materialName = it.getString(MATERIAL_NAME).toString()
        }

        Log.i("ScanCallback", "in plank fragment arguments: {$arguments}")

        val bundle = arguments
        beaconAddress = bundle!!.getString("beaconAddress")
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
        viewPager = binding.idViewPager
        previousButton = binding.previousPlank
        nextButton = binding.nextPlank

        toozifier.addListener(buttonEventListener)


        previousButton.setOnClickListener{
            viewPager.currentItem = viewPager.currentItem - 1
        }

        nextButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
        }

        lifecycle.coroutineScope.launch {
            viewModel.planksByMaterialId(materialId).collect() {
                val plankAdapter = PlankAdapter(requireContext(), it, ::setUpUi)
                viewPager.adapter = plankAdapter
                registerToozer()
            }
        }
    }

    fun setUpUi(view: View?) {
        Log.i("ScanCallback", "in plank fragment beaconAddress: {$beaconAddress}")

        if (view != null) {
            toozifier.updateCard(
                promptView = view,
                focusView = view,
                timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
            )
        }
    }

    private val buttonEventListener = object : ButtonEventListener {

        override fun onButtonEvent(button: Button) {
            Timber.d("$TOOZ_EVENT Button event: $button")
            viewPager.currentItem = viewPager.currentItem + 1
        }
    }

    override fun onDestroyView() {
        deregisterToozer()
        super.onDestroyView()
        _binding = null
    }
}
