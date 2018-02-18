package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import jakubgrzaslewicz.pl.mcucontrol.Models.DeviceInfo
import jakubgrzaslewicz.pl.mcucontrol.R

import kotlinx.android.synthetic.main.activity_register_device.*
import kotlinx.android.synthetic.main.content_register_device.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.wifi.WifiConfiguration
import android.os.Handler
import android.net.ConnectivityManager
import android.support.v4.content.ContextCompat
import com.google.gson.Gson
import io.realm.Realm
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity
import jakubgrzaslewicz.pl.mcucontrol.Classes.Parameters.RegisterDeviceParameters
import jakubgrzaslewicz.pl.mcucontrol.Classes.RandomString
import jakubgrzaslewicz.pl.mcucontrol.Models.ConfigureResponse
import jakubgrzaslewicz.pl.mcucontrol.Models.ConnectToApResponse
import jakubgrzaslewicz.pl.mcucontrol.RealmModels.Device
import okhttp3.MediaType
import okhttp3.RequestBody
import java.security.SecureRandom
import java.util.*


class RegisterDevice : Activity() {

    var SSID: String = ""
    var WiFiPassword: String = ""
    var wifi: WifiManager? = null
    var McuApiService = jakubgrzaslewicz.pl.mcucontrol.Classes.McuApiService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)
        setSupportActionBar(toolbar)
        SSID = intent.getStringExtra(RegisterDeviceParameters.SSIDKey)
        WiFiPassword = intent.getStringExtra(RegisterDeviceParameters.WiFiPasswordKey)
        if (!SSID.isEmpty())
            deviceSSID.text = SSID
        else
            finish()
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
        list0.add("Set up wifi connection")
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

    private fun GetDeviceInfo() {
        SetProgress(STATUS.DeviceInfo)
        val call = McuApiService.GetService().GetDeviceInfo()
        call.enqueue(object : Callback<DeviceInfo> {
            override fun onResponse(call: Call<DeviceInfo>?, response: Response<DeviceInfo>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.d(TAG, response.body()?.Client?.Ap_Ssid)
                        if (response.body()?.Client?.Ap_Ssid == SSID)
                            ConfigureDevice()
                    } else {
                        Log.wtf(TAG, response.errorBody()?.string())
                        Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeviceInfo>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                Log.wtf("FAIL", t?.message)
            }
        })
    }

    var ApPass: String? = null
    private fun ConfigureDevice() {
        SetProgress(STATUS.Configure)
        val randomGenerator = RandomString(15, SecureRandom())
        ApPass = randomGenerator.nextString()

        val call = McuApiService.GetService().SetConfigurationParameter("-WIFI_AP_PASS=" + ApPass)
        call.enqueue(object : Callback<ConfigureResponse> {
            override fun onResponse(call: Call<ConfigureResponse>?, response: Response<ConfigureResponse>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.SUCCESS == true) SetUpWifiConnection()
                    } else {
                        Log.wtf(TAG, response.errorBody()?.string())
                        Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConfigureResponse>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                Log.wtf("FAIL", t?.message)
            }
        })
    }

    private fun ConfigureDeviceSetConfirmed() {
        val call = McuApiService.GetService().SetConfigurationParameter("-WORKING_MODE=CONFIGURED")

        call.enqueue(object : Callback<ConfigureResponse> {
            override fun onResponse(call: Call<ConfigureResponse>?, response: Response<ConfigureResponse>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.SUCCESS == true) ConfigureDeviceSetConfirmed()
                    } else {
                        Log.wtf(TAG, response.errorBody()?.string())
                        Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConfigureResponse>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                Log.wtf("FAIL", t?.message)
            }
        })
    }

    private fun SetUpWifiConnection() {
        SetProgress(STATUS.WiFiSetUp)
        val body = RequestBody.create(MediaType.parse("text/plain"), "-SSID=TP-LINK\n-PASS=grzaslewicz")

        val call = McuApiService.GetService().ConnectToAp(body)
        call.enqueue(object : Callback<ConnectToApResponse> {
            override fun onResponse(call: Call<ConnectToApResponse>?, response: Response<ConnectToApResponse>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.SUCCESS == true) TestConnection()
                    } else {
                        Log.wtf(TAG, response.errorBody()?.string())
                        Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConnectToApResponse>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext, "FAIL", Toast.LENGTH_SHORT).show()
                Log.wtf("FAIL", t?.message)
            }
        })

    }

    private fun TestConnection() {
        SetProgress(STATUS.TestConnection)


        SetProgress(STATUS.Done)
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
                if (activeNetwork != null) {
                    val isWiFi = activeNetwork.type == ConnectivityManager.TYPE_WIFI

                    Log.d("isWifi", isWiFi.toString())
                    Log.d("SSID", "-" + wifi!!.connectionInfo.ssid + "-")
                    if (isWiFi && wifi!!.connectionInfo.ssid == "\"" + SSID + "\"") {
                        Log.d("WIFI", "Connected to " + SSID)
                        GetDeviceInfo()
                    } else {
                        CheckForConnectionSuccess()
                    }
                } else CheckForConnectionSuccess()
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
            STATUS.WiFiSetUp -> progress.setStepsViewIndicatorComplectingPosition(3)
            STATUS.TestConnection -> progress.setStepsViewIndicatorComplectingPosition(4)
            STATUS.Done -> {
                progress.setStepsViewIndicatorComplectingPosition(5)
                SaveDeviceConfig()
            }

        }
    }

    private fun SaveDeviceConfig() {
        realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        try {
            val device = realm.createObject(Device::class.java,SSID)
            device.added_time = Date()
            device.password = ApPass

        } catch (e: Exception) {
            e.printStackTrace()
            realm.cancelTransaction()
        } finally {
            if (realm.isInTransaction)
                realm.commitTransaction()
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
        Connecting, DeviceInfo, Configure, WiFiSetUp, TestConnection, Done
    }
}
