package com.tooz.woodz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.tooz.woodz.database.entity.Plank
import com.tooz.woodz.fragment.BaseToozifierFragment
import com.tooz.woodz.viewmodel.PlankViewModel
import com.tooz.woodz.viewmodel.PlankViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener

class ScannedPlankActivity : BaseActivity() {

    private lateinit var plankViewFactory: PlankViewModelFactory
    private lateinit var plankViewModel: PlankViewModel

    var barcodeValue = ""
    protected val toozifier = WoodzApplication.getToozApplication().toozifier
    private lateinit var plankDetailsView: View
    private lateinit var plankCornerDetailsView: View
    private lateinit var defaultView: View

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

        nearestMachineId.observe(this, Observer<Int> {
            Log.i("ScanCallback", "in scanned plank activity beacon: {${nearestMachineId.value}}")
            setUpActivityUI()
            registerToozer()
        })

        barcodeValue = intent.getStringExtra("barcode").toString()

        plankViewFactory =
            PlankViewModelFactory((application as WoodzApplication).database.plankDao())
        plankViewModel = ViewModelProvider(this, plankViewFactory).get(PlankViewModel::class.java)
        initViews()
        setContentView(defaultView)
        onBarcodeScanned()
        setUpActivityUI()
        registerToozer()
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
            Timber.d("${BaseToozifierFragment.TOOZ_EVENT} onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("${BaseToozifierFragment.TOOZ_EVENT} onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("${BaseToozifierFragment.TOOZ_EVENT} onRegisterFailure $errorCause")
        }

        override fun onRegisterSuccess() {
            when (nearestMachineId.value) {
                2 -> {
                    setUpUI(plankCornerDetailsView)

                }
               3 -> {
                    setUpUI(plankDetailsView)
                }
                else -> {
                    setUpUI(defaultView)
                }
            }
        }
    }

    private fun setUpUI(promptView: View) {
        toozifier.updateCard(
            promptView = promptView,
            focusView = promptView,
            timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
    }

    override fun onDestroy() {
        deregisterToozer()
        super.onDestroy()
    }
}