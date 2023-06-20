package com.example.mybleapp


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_sdk.R
import com.inateck.ScannerJNI
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isBlePermissionGranted = false
    private var isBleAdminPermissionGranted = false
    private var isBleScanPermissionGranted = false
    private var isBleAdvertisePermissionGranted = false
    private var isBleConnectPermissionGranted = false
    private var isBleFineLocationPermissionGranted = false
    private var isBleCoarseLocationPermissionGranted = false
    private var isBleBackgroundLocationPermissionGranted = false



    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Check for permissions.
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissons ->
            isBlePermissionGranted = permissons[Manifest.permission.BLUETOOTH] ?: isBlePermissionGranted
            isBleAdminPermissionGranted = permissons[Manifest.permission.BLUETOOTH_ADMIN] ?: isBleAdminPermissionGranted
            isBleScanPermissionGranted = permissons[Manifest.permission.BLUETOOTH_SCAN] ?: isBleScanPermissionGranted
            isBleAdvertisePermissionGranted = permissons[Manifest.permission.BLUETOOTH_ADVERTISE] ?: isBleAdvertisePermissionGranted
            isBleConnectPermissionGranted = permissons[Manifest.permission.BLUETOOTH_CONNECT] ?: isBleConnectPermissionGranted
            isBleFineLocationPermissionGranted = permissons[Manifest.permission.ACCESS_FINE_LOCATION] ?: isBleFineLocationPermissionGranted
            isBleCoarseLocationPermissionGranted = permissons[Manifest.permission.ACCESS_COARSE_LOCATION] ?: isBleCoarseLocationPermissionGranted
            isBleBackgroundLocationPermissionGranted =  permissons[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: isBleBackgroundLocationPermissionGranted
        }

        requestPermission()
        //initPermission()
        // Make sure if the device support bluetooth or not.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        // Check to see if the Bluetooth classic feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            Toast.makeText(this, "bluetooth_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Check to see if the BLE feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }

        val btn: Button = findViewById<Button>(R.id.scanBtn)

        btn.setOnClickListener {
//            requestPermission()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
               // return@setOnClickListener;
            }
            var jni = ScannerJNI()
            var devices = jni.scan();   // Calling Rust library method to start scanning for bluetooth device

            val countLabel: TextView = findViewById<TextView>(R.id.countLabel)
            if (devices != "") {
                Log.e("devices",devices)
                val jsonArray = JSONArray(devices)
                val deviceList = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val deviceId = jsonObject.getString("device_id")
                    deviceList.add("$deviceId")
                }

                countLabel.text = "Found "+ deviceList.size + " Devices. Click to Pair."

                var arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList)
                val listView: ListView = findViewById<ListView>(R.id.deviceList)
                listView.adapter = arrayAdapter

                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    var result: String = jni.connect(selectedItem,"*","*","*")
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                    Log.println(Log.DEBUG, "connect",result)

                    var cacheRs = jni.getBasicProperties(selectedItem,"firmware_version")
                    Log.println(Log.DEBUG, "firmware_version",cacheRs)

                    var lighting_lamp = jni.getPropertiesInfoByKey(selectedItem,"lighting_lamp_control")
                    Log.println(Log.DEBUG, "lighting_lamp",lighting_lamp)

                    var lightRs = jni.editPropertiesInfoByKey(selectedItem,"lighting_lamp_control","01")
                    Log.println(Log.DEBUG, "ligthRs",lightRs)

                    var barcodes = jni.getAllBarcodeProperties(selectedItem)
                    Log.println(Log.DEBUG, "barcodes",barcodes)

                    var disconnect = jni.disconnect(selectedItem)
                    Log.println(Log.DEBUG, "disconnect",disconnect)
                }
            }
            else {
                countLabel.text = "No bluetooth devices found. Try Again!"
            }
        }

    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)


    private fun requestPermission() {
        isBlePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED

        isBleAdvertisePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADVERTISE
        ) == PackageManager.PERMISSION_GRANTED

        isBleFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isBleCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isBleConnectPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        isBleScanPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        isBleAdminPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED

        isBleBackgroundLocationPermissionGranted =  ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


        val permissionRequest : MutableList<String> = ArrayList()

        if (!isBlePermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH))
        }

        if (!isBleAdvertisePermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_ADVERTISE))
        }

        if (!isBleFineLocationPermissionGranted) {
            permissionRequest.add((Manifest.permission.ACCESS_FINE_LOCATION))
        }

        if (!isBleCoarseLocationPermissionGranted) {
            permissionRequest.add((Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        if (!isBleConnectPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_CONNECT))
        }

        if (!isBleScanPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_SCAN))
        }

        if (!isBleAdminPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_ADMIN))
        }

        if (!isBleBackgroundLocationPermissionGranted){
            permissionRequest.add((Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }
}