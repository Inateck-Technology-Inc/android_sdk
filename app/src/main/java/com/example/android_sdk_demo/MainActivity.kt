package com.example.android_sdk_demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.example.android_sdk_demo.databinding.ActivityMainBinding
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.example.android_sdk_demo.databinding.DeviceItemBinding
import androidx.recyclerview.widget.LinearLayoutManager
//import com.clj.fastble.*
import com.inateck.scanner.ble.BleListManager
import com.inateck.scanner.ble.BleScannerDevice
import com.inateck.scanner.ble.callback.BleScanResultCallBack
import com.inateck.scanner.ble.BleScannerConnectState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerView
        val adapter = DeviceAdapter(listOf(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        BleListManager.init(application)

        binding.buttonScan.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonScan.visibility = View.GONE
            BleListManager.scan(object : BleScanResultCallBack {
                override fun onScanStarted(scanResultList: List<BleScannerDevice>) {
                    adapter.updateData(BleListManager.scannerDevices)
                }

                override fun onScanning(device: BleScannerDevice) {
                    adapter.updateData(BleListManager.scannerDevices)
                }

                override fun onScanFinished(scanResultList: List<BleScannerDevice>) {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonScan.visibility = View.VISIBLE

                    adapter.updateData(BleListManager.scannerDevices)
                }
            })
        }

        binding.buttonStop.setOnClickListener {
            BleListManager.stopScan()
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
//            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
        ), 1);

    }
}

private class DeviceAdapter(private var deviceList: List<BleScannerDevice>, val context: Context) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = DeviceItemBinding.bind(itemView)

        val name: TextView = binding.deviceName
        val uuid: TextView = binding.uuid
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val switch: Switch = binding.toggleSwitch
        val progressBar: ProgressBar = binding.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = deviceList[position]
        // 直接设置文本
        holder.name.text = device.name
        holder.uuid.text = device.mac

        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isGone = true
        holder.progressBar.isGone = true
        when (device.connectState) {
            BleScannerConnectState.CONNECTING -> {
                holder.switch.isChecked = true
                holder.switch.isGone = true
                holder.progressBar.isGone = false
            }
            BleScannerConnectState.CONNECTED -> {
                holder.switch.isChecked = true
                holder.switch.isGone = false
                holder.progressBar.isGone = true
            }
            BleScannerConnectState.DISCONNECTING -> {
                holder.switch.isChecked = false
                holder.switch.isGone = true
                holder.progressBar.isGone = false
            }
            BleScannerConnectState.DISCONNECTED -> {
                holder.switch.isChecked = false
                holder.switch.isGone = false
                holder.progressBar.isGone = true
            }
            BleScannerConnectState.UNKNOWN -> {
                holder.switch.isChecked = false
            }
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            holder.switch.isGone = true
            holder.progressBar.isGone = false
            if (isChecked) {
                device.connect { result ->
                    if (result.isSuccess) {
                        holder.switch.isGone = false
                        holder.progressBar.isGone = true
                    } else {
                        holder.switch.isGone = false
                        holder.progressBar.isGone = true
                        Log.e("DeviceAdapter", "Connect failed: ${result.exceptionOrNull()}")
                    }
                }
            } else {
                device.disconnect { result ->
                    if (result.isSuccess) {
                        holder.switch.isGone = false
                        holder.progressBar.isGone = true
                    } else {
                        holder.switch.isGone = false
                        holder.progressBar.isGone = true
                        Log.e("DeviceAdapter", "Disconnect failed: ${result.exceptionOrNull()}")
                    }
                 }
            }
        }

        holder.itemView.setOnClickListener {
            if (device.connectState == BleScannerConnectState.CONNECTED) {
                val intent = Intent(context, DeviceDetailActivity::class.java)
                intent.putExtra("mac", device.mac)
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<BleScannerDevice>) {
        deviceList = newData
        notifyDataSetChanged()
    }
}