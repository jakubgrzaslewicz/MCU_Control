package jakubgrzaslewicz.pl.mcucontrol.Activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import io.realm.Realm
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity
import jakubgrzaslewicz.pl.mcucontrol.R
import jakubgrzaslewicz.pl.mcucontrol.RealmModels.Device
import kotlinx.android.synthetic.main.content_main.*
import io.realm.Sort
import jakubgrzaslewicz.pl.mcucontrol.Adapters.DevicesListAdapter
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class MainActivity : Activity(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemLongClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { openAddDeviceActivity() }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        LoadDevicesList()

    }

    var DevicesList = ArrayList<Device>()
    var DevicesListAdapter: DevicesListAdapter? = null
    private fun LoadDevicesList() {
        try {
            realm.where(Device::class.java).distinctValues("access_point_name").sort("access_point_name", Sort.DESCENDING).findAllAsync().addChangeListener { t, a ->
                run {
                    if (t.size > 0) {
                        DevicesList.clear()
                        t.forEach({
                            DevicesList.add(it)
                        })
                        DevicesListAdapter?.list = DevicesList
                        DevicesListAdapter?.notifyDataSetChanged()
                        if (list.adapter == null) {
                            DevicesListAdapter = DevicesListAdapter(this,
                                    DevicesList)
                            list.adapter = DevicesListAdapter
                        }

                        loadingInformation.visibility = LinearLayout.GONE
                        list.visibility = ListView.VISIBLE
                        notFound.visibility = LinearLayout.GONE
                        list.onItemLongClickListener = this
                    } else {
                        list.visibility = ListView.GONE
                        loadingInformation.visibility = LinearLayout.GONE
                        notFound.visibility = LinearLayout.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        var device = DevicesList[p2]
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Wybierz operację")
        builder.setItems(arrayOf<CharSequence>("Usuń", "Zmień nazwę"), { dialog, which ->
            when (which) {
                0 -> {
                    DBUtils.DeleteDevice(device)
                    LoadDevicesList()
                }
                1 -> {
                }
            }
        })
        builder.show()
        return true
    }


    fun openAddDeviceActivity() {
        val i = Intent(this@MainActivity, AddDevice::class.java)
        startActivity(i)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        LoadDevicesList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
