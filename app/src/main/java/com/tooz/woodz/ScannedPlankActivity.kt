package com.tooz.woodz

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.tooz.woodz.database.entity.Plank
import com.tooz.woodz.viewmodel.PlankViewModel
import com.tooz.woodz.viewmodel.PlankViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener

class ScannedPlankActivity : BaseActivity() {

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val WATCH_EVENT = "Watch event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }

    private lateinit var plankViewFactory: PlankViewModelFactory
    private lateinit var plankViewModel: PlankViewModel

    var barcodeValue = ""
    protected val toozifier = WoodzApplication.getToozApplication().toozifier
    private lateinit var plankDetailsView: View
    private lateinit var plankCornerDetailsView: View
    private lateinit var defaultView: View

    private lateinit var observer: Observer<Int>

    var plankHeight: TextView? = null
    var plankWidth: TextView? = null
    var plankType: TextView? = null
    var plankGroup: TextView? = null

    var plankLeftCorner: TextView? = null
    var plankBottomCorner: TextView? = null
    var plankRightCorner: TextView? = null
    var plankUpCorner: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeValue = intent.getStringExtra("barcode").toString()

        plankViewFactory =
            PlankViewModelFactory((application as WoodzApplication).database.plankDao())
        plankViewModel = ViewModelProvider(this, plankViewFactory).get(PlankViewModel::class.java)
        initViews()
        setContentView(defaultView)
        onBarcodeScanned()
        setUpActivityUI()
        registerToozer()

        observer = Observer {
            Log.i("ScanCallback", "in scanned plank activity beacon: {${nearestMachineId.value}}")
            setUpActivityUI()
            registerToozer()
        }

        nearestMachineId.observeForever(observer)
    }

    private fun initViews() {
        plankDetailsView = layoutInflater.inflate(R.layout.plank_item2, null)
        plankCornerDetailsView = layoutInflater.inflate(R.layout.plank_item3, null)
        defaultView = layoutInflater.inflate(R.layout.layout_prompt, null)

        plankHeight = plankDetailsView.findViewById(R.id.plank_height)
        plankWidth = plankDetailsView.findViewById(R.id.plank_width)
        plankGroup = plankDetailsView.findViewById(R.id.plank_group)
        plankType = plankDetailsView.findViewById(R.id.plank_type)

        plankLeftCorner = plankCornerDetailsView.findViewById(R.id.left_corner)
        plankRightCorner = plankCornerDetailsView.findViewById(R.id.right_corner)
        plankUpCorner = plankCornerDetailsView.findViewById(R.id.up_corner)
        plankBottomCorner = plankCornerDetailsView.findViewById(R.id.bottom_corner)
    }

    private fun setUpActivityUI() {
        when (nearestMachineId.value) {
            2 -> {
                setContentView(plankCornerDetailsView)
            }
            3 -> {
                setContentView(plankDetailsView)
            }
            else -> {
                setContentView(defaultView)
            }
        }
        setUpUI()
    }

    private fun onBarcodeScanned() {
        lifecycle.coroutineScope.launch {
            plankViewModel.plankByBarcode(barcodeValue).collect() {
                Log.i("BarcodeScanned", "$it")
                if (it is Plank) {
                    plankHeight?.text = it.height.toString()
                    plankWidth?.text = it.width.toString()
                    plankType?.text = it.type
                    plankGroup?.text = it.group

                    plankLeftCorner?.text = it.cornerLeft.toString()
                    plankRightCorner?.text = it.cornerRight.toString()
                    plankUpCorner?.text = it.cornerUp.toString()
                    plankBottomCorner?.text = it.cornerBottom.toString()
                }
            }
        }
    }

    private fun registerToozer() {
        toozifier.register(
            this,
            getString(R.string.app_name),
            registrationListener
        )
    }

    private val registrationListener = object : RegistrationListener {
        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        }

        override fun onRegisterSuccess() {
            setUpUI()
        }
    }

    private fun setUpUI() {
        Log.i("BarcodeScanned", "this is machine id in scanned plank ${nearestMachineId.value}")

        val view: View = when (nearestMachineId.value) {
            2 -> {
                plankCornerDetailsView
            }
            3 -> {
                plankDetailsView
            }
            else -> {
                defaultView
            }
        }

        toozifier.updateCard(
            promptView = view,
            focusView = view,
            timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
    }

    override fun onDestroy() {
        deregisterToozer()
        nearestMachineId.removeObserver(observer)
        super.onDestroy()
    }
}