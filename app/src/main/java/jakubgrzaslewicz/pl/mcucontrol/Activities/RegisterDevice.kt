package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import jakubgrzaslewicz.pl.mcucontrol.Classes.McuApiService
import jakubgrzaslewicz.pl.mcucontrol.Models.DeviceInfo
import jakubgrzaslewicz.pl.mcucontrol.R

import kotlinx.android.synthetic.main.activity_register_device.*
import kotlinx.android.synthetic.main.content_register_device.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.wifi.WifiConfiguration
import android.os.Handler
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.support.v4.content.ContextCompat
import com.baoyachi.stepview.VerticalStepView


class RegisterDevice : AppCompatActivity() {
    var SSID: String = ""
    var wifi: WifiManager? = null
    var McuApiService = jakubgrzaslewicz.pl.mcucontrol.Classes.McuApiService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)
        setSupportActionBar(toolbar)
        SSID = intent.getStringExtra("SSID")
        if (SSID.isEmpty())
        //finish()
            deviceSSID.text = SSID
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        InitializeProgress()
        InitializeWifiManager()
        ConnectToDevice()
    }

    private fun InitializeProgress() {
        val list0 = ArrayList<String>()
        list0.add("Connect with device")
        list0.add("Get device info")
        list0.add("Configure")
        list0.add("Test connection")

        progress.setStepsViewIndicatorComplectingPosition(-1)
                .reverseDraw(false)
                .setStepViewTexts(list0)
                .setLinePaddingProportion(1f)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this@RegisterDevice, android.R.color.white))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this@RegisterDevice, R.color.uncompleted_text_color))
                .setStepViewComplectedTextColor(ContextCompat.getColor(this@RegisterDevice, android.R.color.white))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this@RegisterDevice, R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this@RegisterDevice, R.drawable.complted))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this@RegisterDevice, R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this@RegisterDevice, R.drawable.attention))
    }

    private fun StartRegistering() {
        SetProgress(STATUS.DeviceInfo)
        val call = McuApiService.GetService().GetDeviceInfo()
        call.enqueue(object : Callback<DeviceInfo> {
            override fun onResponse(call: Call<DeviceInfo>?, response: Response<DeviceInfo>?) {
                if (response != null) {
                    response.raw().body().toString()
                    Log.wtf("SUCCESS", response.raw().body().toString())
                }
            }

            override fun onFailure(call: Call<DeviceInfo>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                Log.wtf("FAIL", t?.message)
            }
        })
    }

    private fun ConnectToDevice() {
        SetProgress(STATUS.Connecting)
        val conf = WifiConfiguration()
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        conf.SSID = "\"" + SSID + "\""
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi!!.addNetwork(conf)
        val list = wifi!!.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + SSID + "\"") {
                wifi!!.disconnect()
                wifi!!.enableNetwork(i.networkId, true)
                wifi!!.reconnect()

                break
            }
        }
        CheckForConnectionSuccess()

    }

    var checkCounter = 0
    private fun CheckForConnectionSuccess() {
        if (checkCounter < 10) {
            val handler = Handler()
            handler.postDelayed({
                checkCounter++
                val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val activeNetwork = cm.activeNetworkInfo
                val isWiFi = activeNetwork.type == ConnectivityManager.TYPE_WIFI
                Log.d("isWifi", isWiFi.toString())
                Log.d("SSID", "-" + wifi!!.connectionInfo.ssid + "-")
                if (isWiFi && wifi!!.connectionInfo.ssid == "\"" + SSID + "\"") {
                    Log.d("WIFI", "Connected to " + SSID)
                    StartRegistering()
                } else {
                    CheckForConnectionSuccess()
                }
            }, 1000)
        } else {
            Toast.makeText(applicationContext, "Connection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun SetProgress(status: STATUS) {
        when (status) {
            STATUS.Connecting -> progress.setStepsViewIndicatorComplectingPosition(0)
            STATUS.DeviceInfo -> progress.setStepsViewIndicatorComplectingPosition(1)
            STATUS.Configure -> progress.setStepsViewIndicatorComplectingPosition(2)
            STATUS.TestConnection -> progress.setStepsViewIndicatorComplectingPosition(3)
            STATUS.Done -> progress.setStepsViewIndicatorComplectingPosition(4)

        }
    }

    private fun InitializeWifiManager() {
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi?.isWifiEnabled == false) {
            Toast.makeText(applicationContext, "Enabling WIFI...", Toast.LENGTH_SHORT).show()
            wifi?.isWifiEnabled = true
        }
    }

    enum class STATUS {
        Connecting, DeviceInfo, Configure, TestConnection, Done
    }
}
