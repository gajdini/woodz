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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.altbeacon.beacon.BeaconManager


private const val LOCATION_PERMISSION_REQUEST_CODE = 2

abstract class BaseActivity : AppCompatActivity() {

    private var addresses = arrayOf("AC:23:3F:88:10:51", "AC:23:3F:88:10:53", "AC:23:3F:88:10:57")
    private var beaconManager: BeaconManager? = null
    private var filters: MutableList<ScanFilter> = mutableListOf()
    private val scanResults = mutableListOf<ScanResult>()
    var nearestBeaconAddress: String? = null
    private var isScanning = false


    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            doBleScan()
        }
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
            scanResults.add(result)
            scanResults.sortByDescending { it.rssi }
            nearestBeaconAddress = scanResults[0].device.address
//            Log.i("ScanCallback",scanResults[0].toString())
            with(result.device) {
                Log.i(
                    "bledevices",
                    "Found BLE device! address: $address ${result.rssi} $"
                )
            }
        }
    }

    private fun doBleScan() {
        //todo delete
        Toast.makeText(
            this, "hey",
            Toast.LENGTH_SHORT
        ).show()

        scanResults.clear()

        for (i in addresses.indices) {
            val filter = ScanFilter.Builder().setDeviceAddress(addresses[i]).build()
            filters.add(filter)
        }

        bleScanner.startScan(filters, scanSettings, scanCallback)
        isScanning = true
    }

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
}