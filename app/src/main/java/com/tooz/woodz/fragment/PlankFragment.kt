package com.tooz.woodz.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.viewpager.widget.ViewPager
import com.tooz.woodz.MainActivity
import com.tooz.woodz.R
import com.tooz.woodz.WoodzApplication
import com.tooz.woodz.adapter.PlankAdapter
import com.tooz.woodz.databinding.PlankFragmentBinding
import com.tooz.woodz.viewmodel.PlankViewModel
import com.tooz.woodz.viewmodel.PlankViewModelFactory
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener


class PlankFragment : BaseToozifierFragment() {

    companion object {
        var MATERIAL_ID = "materialId"
        var MATERIAL_NAME = "materialName"
    }

    private var machineId: MutableLiveData<Int>? = null

    private val viewModel: PlankViewModel by activityViewModels {
        PlankViewModelFactory(
            (activity?.application as WoodzApplication).database.plankDao()
        )
    }

    private var _binding: PlankFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    private lateinit var observer: Observer<Int>

    private lateinit var viewPager: ViewPager

    private lateinit var previousButton: ImageView

    private lateinit var nextButton: ImageView

    private lateinit var defaultView: View

    private var materialId: Int = 0

    private lateinit var materialName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            materialId = it.getInt(MATERIAL_ID)
            materialName = it.getString(MATERIAL_NAME).toString()
        }

        mainActivity = activity as MainActivity

        observer = Observer{
            Log.i("ScanCallback", "in plank fragment beacon change: {${mainActivity.nearestMachineId.value}}")
            registerToozer()
        }

        mainActivity.nearestMachineId.observeForever(observer)
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
        defaultView = layoutInflater.inflate(R.layout.layout_prompt, null)

        toozifier.addListener(buttonEventListener)

        previousButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
            setUpUi()
        }

        nextButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
            setUpUi()
        }

        lifecycle.coroutineScope.launch {
            val planks = viewModel.planksByMaterialId(materialId)
            val plankAdapter = PlankAdapter(requireContext(), planks, ::setUpUi)
            viewPager.adapter = plankAdapter
            registerToozer()
        }
    }

    fun registerToozer() {
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
            setUpUi()
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        }
    }

    fun setUpUi() {
        if (!toozifier.isRegistered){
            registerToozer()
        }
        Log.i("ScanCallback", "machine id: ${mainActivity.nearestMachineId.value}")

        when (mainActivity.nearestMachineId.value) {
            1 -> {
                Log.i("ScanCallback", "in plank fragment view id: ${viewPager.currentItem}")
                toozifier.updateCard(
                    promptView = viewPager.get(viewPager.currentItem),
                    focusView = viewPager.get(viewPager.currentItem),
                    timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
                )
            }
            else -> {
                Log.i("ScanCallback", "in elseeee")
                toozifier.updateCard(
                    promptView = defaultView,
                    focusView = defaultView,
                    timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
                )
            }
        }
    }

    private val buttonEventListener = object : ButtonEventListener {

        override fun onButtonEvent(button: Button) {
            Timber.d("$TOOZ_EVENT Button event: $button")
            val plankId = viewPager.get(viewPager.currentItem)
                .findViewById<TextView>(R.id.plank_id).text.toString().toInt()
            viewPager.currentItem = viewPager.currentItem + 1
            lifecycle.coroutineScope.launch {
                viewModel.plankIsDone(plankId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.nearestMachineId.observeForever(observer)
        toozifier.addListener(buttonEventListener)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.nearestMachineId.removeObserver(observer)
    }

    override fun onDestroyView() {
        deregisterToozer()
        mainActivity.nearestMachineId.removeObserver(observer)
        super.onDestroyView()
        _binding = null
    }
}
