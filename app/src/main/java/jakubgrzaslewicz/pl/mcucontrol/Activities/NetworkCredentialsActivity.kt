package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity
import jakubgrzaslewicz.pl.mcucontrol.Classes.NetworkSpinnerArrayAdapter
import jakubgrzaslewicz.pl.mcucontrol.R

import kotlinx.android.synthetic.main.activity_network_credentials.*
import kotlinx.android.synthetic.main.content_network_credentials.*

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

        SSID = intent.getStringExtra("SSID")
        RequestPermissions()
        RegisterEvents()
    }

    private fun RegisterEvents() {

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
            Log.d("SCAN", "RECEIVED")
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
            Log.d("WIFI", "ReRegistering receiver")
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
