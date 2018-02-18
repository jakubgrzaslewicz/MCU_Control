package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity
import jakubgrzaslewicz.pl.mcucontrol.Classes.NetworkSpinnerArrayAdapter
import jakubgrzaslewicz.pl.mcucontrol.Classes.Parameters.NetworkCredentialsActivityParameters
import jakubgrzaslewicz.pl.mcucontrol.Classes.Parameters.RegisterDeviceParameters
import jakubgrzaslewicz.pl.mcucontrol.R
import kotlinx.android.synthetic.main.activity_network_credentials.*
import kotlinx.android.synthetic.main.content_network_credentials.*
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class NetworkCredentialsActivity : Activity() {
    var SSID: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_credentials)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        SSID = intent.getStringExtra(NetworkCredentialsActivityParameters.SSIDKey)
        RequestPermissions()
        RegisterEvents()
        CheckPassword()
    }

    private fun CheckPassword() {
        val text = password.text
        next_button.isEnabled = !text.isEmpty()
    }

    private fun RegisterEvents() {
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                CheckPassword()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        next_button.setOnClickListener(ButtonClick)
    }

    private var ButtonClick = View.OnClickListener {
        if (password.text.isNotEmpty()) {
            val intent = Intent(NetworkCredentialsActivity@ this, RegisterDevice::class.java)
            intent.putExtra(RegisterDeviceParameters.SSIDKey,SSID)
            Log.d(TAG,password.text.toString())
            intent.putExtra(RegisterDeviceParameters.WiFiPasswordKey,password.text.toString().trim())
            startActivity(intent)
        }
    }

    private fun RequestPermissions() {
        val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this@NetworkCredentialsActivity, permissions, 0)
    }

    var scanResults = ArrayList<ScanResult>()
    var wifi: WifiManager? = null
    var scanResultsAdapter: NetworkSpinnerArrayAdapter? = null

    private fun StartScanning() {
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi?.isWifiEnabled == false) {
            Toast.makeText(applicationContext, "Enabling WIFI...", Toast.LENGTH_LONG).show()
            wifi?.isWifiEnabled = true
        }
        scanResultsAdapter = NetworkSpinnerArrayAdapter(this, R.layout.network_spinner_item, scanResults)
        networksSpinner?.adapter = scanResultsAdapter!!
        registerReceiver(WifiResultReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        Scan()
    }

    var counter = 0

    val WifiResultReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            counter++
            scanResults.clear()
            wifi?.scanResults!!
                    .filterNot { it.SSID.startsWith("MCU-HUB-Client") }
                    .forEach { scanResults.add(it) }
            scanResultsAdapter?.notifyDataSetChanged()
            Scan()
        }
    }

    fun Scan() {
        if (counter < 12)
            wifi?.startScan()
        else {
            counter = 0
            unregisterReceiver(WifiResultReceiver)
            registerReceiver(WifiResultReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val handler = Handler()
            handler.postDelayed({
                wifi?.startScan()
            }, 1000)
        }
    }

    override fun onResume() {
        registerReceiver(WifiResultReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        CheckPassword()
        super.onResume()

    }

    override fun onPause() {
        try {
            unregisterReceiver(WifiResultReceiver)
        } catch (ignored: Exception) {
            //ignored
        }
        super.onPause()

    }

    override fun onDestroy() {
        try {
            unregisterReceiver(WifiResultReceiver)
        } catch (ignored: Exception) {
            //ignored
        }
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0)
            if (grantResults.isNotEmpty())
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    StartScanning()
                else
                    Toast.makeText(this@NetworkCredentialsActivity, "Permissions request accepted", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this@NetworkCredentialsActivity, "Permissions request revoked", Toast.LENGTH_LONG).show()
    }

}
