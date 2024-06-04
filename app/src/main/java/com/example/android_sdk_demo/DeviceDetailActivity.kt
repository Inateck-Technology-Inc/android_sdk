package com.example.android_sdk_demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_sdk_demo.databinding.DeviceItemBinding
import com.example.android_sdk_demo.databinding.SettingsActivityBinding
import com.inateck.scanner.ble.BleListManager
import com.inateck.scanner.ble.BleScannerConnectState
import com.inateck.scanner.ble.BleScannerDevice
import android.view.*
import android.widget.*
import com.example.android_sdk_demo.databinding.DetailItemBinding
import com.google.android.material.snackbar.Snackbar

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding
    private var device: BleScannerDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show()
            Log.d("DeviceDetailActivity", "Back")
            finish()
        }

        BleListManager.disconnectHandler = { device, isUser ->
            if (this.device != null) {
                if (device.mac == this.device!!.mac) {
                    Toast.makeText(this, "Device disconnect", Toast.LENGTH_SHORT).show()
                    Log.d("DeviceDetailActivity", "Device disconnect")
                    finish()
                }
            }
        }

        val mac = intent.getStringExtra("mac")
        device = BleListManager.scannerDevices.firstOrNull { it.mac == mac }

        val recyclerView = binding.recyclerView
        val adapter = DeviceDetailsAdapter(device!!,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

private class DeviceDetailsAdapter(val device: BleScannerDevice, val context: Context) : RecyclerView.Adapter<DeviceDetailsAdapter.ViewHolder>() {

    private val settingList = listOf(
        "version",
        "battery",
        "software",
        "settingInfo",
        "closeVolume",
        "openVolume",
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = DetailItemBinding.bind(itemView)

        val name: TextView = binding.deviceName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = settingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = settingList[position]
        // 直接设置文本
        holder.name.text = setting
        holder.itemView.setOnClickListener {
            when (setting) {
                "version" -> {
                    device.messager.getHardwareInfo {
                        if (it.isSuccess) {
                            val version = it.getOrNull()
                            Toast.makeText(context, "Version: $version", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Version: $version")
                        } else {
                            Toast.makeText(context, "Failed to get version", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to get version")
                        }
                    }
                }
                "battery" -> {
                    device.messager.getBatteryInfo {
                        if (it.isSuccess) {
                            val battery = it.getOrNull()
                            Toast.makeText(context, "Battery: $battery", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Battery: $battery")
                        } else {
                            Toast.makeText(context, "Failed to get battery", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to get battery")
                        }
                    }
                }
                "software" -> {
                    device.messager.getVersion {
                        if (it.isSuccess) {
                            val software = it.getOrNull()
                            Toast.makeText(context, "Software: $software", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Software: $software")
                        } else {
                            Toast.makeText(context, "Failed to get software", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to get software")
                        }
                    }
                }
                "settingInfo" -> {
                    device.messager.getSettingInfo {
                        if (it.isSuccess) {
                            val settings = it.getOrNull()
                            Toast.makeText(context, "Settings: $settings", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Settings: $settings")
                        } else {
                            Toast.makeText(context, "Failed to get settings", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to get settings")
                        }
                    }
                }
                "closeVolume" -> {
                    val closeVolume = "[{\"area\":\"3\",\"value\":\"0\",\"name\":\"volume\"}]"
                    device.messager.setSettingInfo(closeVolume) {
                        if (it.isSuccess) {
                            Toast.makeText(context, "Volume set to 0", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Volume set to 0")
                        } else {
                            Toast.makeText(context, "Failed to set volume", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to set volume")
                        }
                    }
                }
                "openVolume" -> {
                    val openVolume = "[{\"area\":\"3\",\"value\":\"4\",\"name\":\"volume\"}]"
                    device.messager.setSettingInfo(openVolume) {
                        if (it.isSuccess) {
                            Toast.makeText(context, "Volume set to 4", Toast.LENGTH_SHORT).show()
                            Log.d("DeviceDetailActivity", "Volume set to 4")
                        } else {
                            Toast.makeText(context, "Failed to set volume", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("DeviceDetailActivity", "Failed to set volume")
                        }
                    }
                }
            }
        }
    }
}