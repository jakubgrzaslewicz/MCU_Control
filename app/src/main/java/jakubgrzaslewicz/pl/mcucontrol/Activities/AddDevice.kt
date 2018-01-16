package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity
import jakubgrzaslewicz.pl.mcucontrol.R
import kotlinx.android.synthetic.main.activity_add_device.*
import kotlinx.android.synthetic.main.content_add_device.*
import org.w3c.dom.Text


class AddDevice : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        RequestPermissions()
        RegisterEvents()
    }

    private fun RegisterEvents() {
        scanResultsList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            OpenConnectionActivity((view as TextView).text.toString())
        }
    }

    private fun OpenConnectionActivity(SSID: String) {
        val i = Intent(this@AddDevice, RegisterDevice::class.java)
        i.putExtra("SSID", SSID)
        startActivity(i)
    }

    var scanResults = ArrayList<String>()
    var wifi: WifiManager? = null
    var scanResultsAdapter: ArrayAdapter<String>? = null

    private fun StartScanning() {
        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi?.isWifiEnabled == false) {
            Toast.makeText(applicationContext, "Enabling WIFI...", Toast.LENGTH_LONG).show()
            wifi?.isWifiEnabled = true
        }
        scanResultsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, scanResults)
        scanResultsList?.adapter = scanResultsAdapter
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
                    .filter { it.SSID.startsWith("MCU-HUB-Client") }
                    .forEach { scanResults.add(it.SSID) }
            scanResultsAdapter?.notifyDataSetChanged()
            Scan()
        }
    }
    fun Scan() {
        if (counter < 12)
            wifi?.startScan()
        else{
            Log.d("WIFI","ReRegistering receiver")
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

    private fun RequestPermissions() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this@AddDevice, permissions, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0)
            if (grantResults.isNotEmpty())
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    StartScanning()
                else
                    Toast.makeText(this@AddDevice, "Permissions request revoked", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this@AddDevice, "Permissions request revoked", Toast.LENGTH_LONG).show()
    }
}
