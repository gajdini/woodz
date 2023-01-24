package com.tooz.woodz

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.tooz.woodz.viewmodel.MachineViewModel
import com.tooz.woodz.viewmodel.MachineViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.altbeacon.beacon.BeaconManager


private const val LOCATION_PERMISSION_REQUEST_CODE = 2

abstract class BaseActivity : AppCompatActivity() {

    private var addresses = emptyList<String>()

    private lateinit var machineViewFactory: MachineViewModelFactory
    private lateinit var machineViewModel: MachineViewModel

    private var beaconManager: BeaconManager? = null
    private var filters: MutableList<ScanFilter> = mutableListOf()
    private val scanResults = mutableListOf<ScanResult>()
    open var nearestMachineId = MutableLiveData<Int>(4)
    private var isScanning = false
    private var nearestAddress: String = ""

    open fun getMachineId(): MutableLiveData<Int>? {
        return nearestMachineId
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .build()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        machineViewFactory =
            MachineViewModelFactory((application as WoodzApplication).database.machineDao())
        machineViewModel =
            ViewModelProvider(this, machineViewFactory).get(MachineViewModel::class.java)

        lifecycle.coroutineScope.launch {
            machineViewModel.allMachineAddresses().collect() {
                addresses = it
                setFilters()
                if (checkPermissions()) {
                    doBleScan()
                }
            }
        }
    }

    private fun setFilters() {
        for (i in addresses.indices) {
            val filter = ScanFilter.Builder().setDeviceAddress(addresses[i]).build()
            filters.add(filter)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun checkPermissions(): Boolean {
        when {
            bluetoothAdapter.isEnabled && isLocationPermissionGranted -> return true
            !bluetoothAdapter.isEnabled -> requestEnableBluetooth()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted -> requestLocationPermission()
        }
        return false
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                requestEnableBluetooth()
            }
        }

    private fun requestEnableBluetooth() {
//        if bluetooth not enabled, it asks to enable it
        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBluetoothIntent)
        }
    }

    private fun requestLocationPermission() {
        //asks permission for location if not granted
        if (isLocationPermissionGranted) {
            return
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(R.string.location_permission_alert_title)
            alertDialogBuilder.setMessage(R.string.location_permission_alert_message)
            alertDialogBuilder.setPositiveButton(R.string.ok, null)
            alertDialogBuilder.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) {
                scanResults[indexQuery] = result
            } else {
                scanResults.add(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("ScanCallback", "Error: $errorCode"
            )
        }
    }

    private fun doBleScan() {
        scanResults.clear()
        Handler().postDelayed({ doBleStop() }, 10000)
        bleScanner.startScan(filters, scanSettings, scanCallback)
        Log.i("ScanCallback", "scan started and cleared")
        isScanning = true
    }

    private fun doBleStop() {
        scanResults.sortByDescending { it.rssi }
        Log.i(
            "ScanCallback",
            "scan results: $scanResults"
        )
        //defaults if no machine detected
        if (scanResults.isEmpty()){
            nearestAddress = ""
            nearestMachineId.value = 4
        }
        //then we have new nearest address
        if (scanResults.isNotEmpty() && nearestAddress != scanResults[0].device.address) {
            nearestAddress = scanResults[0].device.address

            Log.i(
                "ScanCallback",
                "Found BLE device changed! address: ${nearestAddress}"
            )
            lifecycle.coroutineScope.launch {
                machineViewModel.machineIdByAddress(nearestAddress).collect() {
                    nearestMachineId.value = it
                }
            }
        }
        //stop scan and start again (every 10 seconds)
        bleScanner.stopScan(scanCallback)
        doBleScan()
        isScanning = false
    }


    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
}