package com.tooz.woodz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
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
import java.io.IOException


class BarcodeScannerActivity : AppCompatActivity() {
    var surfaceView: SurfaceView? = null
    var txtBarcodeValue: TextView? = null
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private val REQUEST_CAMERA_PERMISSION = 201
    var barcodeValue = ""
    private lateinit var plankViewFactory: PlankViewModelFactory
    private lateinit var plankViewModel: PlankViewModel
    protected val toozifier = WoodzApplication.getToozApplication().toozifier
    private lateinit var plankDetailsView: View
    private lateinit var plankCornerDetailsView: View

    var plankHeight: TextView? = null
    var plankWidth: TextView? = null
    var plankType: TextView? = null
    var plankGroup: TextView? = null

    var plankLeftCorner: TextView? = null
    var plankBottomCorner: TextView? = null
    var plankRightCorner: TextView? = null
    var plankUpCorner: TextView? = null

    fun registerToozer() {
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
            val beaconAddress  = intent.getStringExtra("beaconAddress")
            Log.i("ScanCallback", "in barcode activity beaconAddress: {$beaconAddress}")
            when(beaconAddress) {
                "AC:23:3F:88:10:53" -> {
                    toozifier.updateCard(
                        promptView = plankCornerDetailsView,
                        focusView = plankCornerDetailsView,
                        timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
                    )
                }
                "AC:23:3F:88:10:57" -> {
                    toozifier.updateCard(
                        promptView = plankDetailsView,
                        focusView = plankDetailsView,
                        timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
        plankViewFactory =
            PlankViewModelFactory((application as WoodzApplication).database.plankDao())
        plankViewModel = ViewModelProvider(this, plankViewFactory).get(PlankViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue)
        surfaceView = findViewById(R.id.surfaceView)
        plankDetailsView = layoutInflater.inflate(R.layout.plank_item2, null)
        plankCornerDetailsView = layoutInflater.inflate(R.layout.plank_item3, null)

        plankHeight = plankDetailsView.findViewById(R.id.plank_height)
        plankWidth = plankDetailsView.findViewById(R.id.plank_width)
        plankGroup = plankDetailsView.findViewById(R.id.plank_group)
        plankType = plankDetailsView.findViewById(R.id.plank_type)

        plankLeftCorner = plankCornerDetailsView.findViewById(R.id.left_corner)
        plankRightCorner = plankCornerDetailsView.findViewById(R.id.right_corner)
        plankUpCorner = plankCornerDetailsView.findViewById(R.id.up_corner)
        plankBottomCorner = plankCornerDetailsView.findViewById(R.id.bottom_corner)
    }

    private fun initialiseDetectorsAndSources() {
//        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@BarcodeScannerActivity,
                            Manifest.permission.CAMERA
                        ) === PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(surfaceView!!.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@BarcodeScannerActivity,
                            arrayOf<String>(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                //todo
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                //todo
                //for now it shows the scanned value in the textview
                //todo remove textview
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    txtBarcodeValue!!.post {
                        barcodeValue = barcodes.valueAt(0).displayValue
                        txtBarcodeValue!!.text = barcodeValue
                        onBarcodeScanned(barcodeValue)
                    }
                }
            }
        })
    }

    fun onBarcodeScanned(barcode: String) {
        lifecycle.coroutineScope.launch {
            plankViewModel.plankByBarcode(barcode).collect() {
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

                    registerToozer()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

    fun deregisterToozer() {
        toozifier.deregister()
    }

    override fun onDestroy() {
        deregisterToozer()
        super.onDestroy()
    }
}