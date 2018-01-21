package jakubgrzaslewicz.pl.mcucontrol.Classes

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.calculateSignalLevel
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import jakubgrzaslewicz.pl.mcucontrol.Classes.Activity.TAG
import jakubgrzaslewicz.pl.mcucontrol.R
import kotlinx.android.synthetic.main.network_spinner_item.view.*

/**
 * Created by Jakub Grząślewicz on 21.01.2018.
 */

class NetworkSpinnerArrayAdapter(context: Context, spinnerItemResourceId: Int, values: ArrayList<ScanResult>) : BaseAdapter() {
    var resource: Int
    var list: ArrayList<ScanResult>
    var vi: LayoutInflater

    init {

        this.resource = spinnerItemResourceId
        this.list = values
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): ScanResult {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @Suppress("NAME_SHADOWING")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val inflatedView: View
        val item = getItem(position)
        if (convertView == null) {
            inflatedView = vi.inflate(resource, null)
            holder = ViewHolder()

            holder.icon = inflatedView.findViewById(R.id.icon) as ImageView?
            holder.networkName = inflatedView.findViewById(R.id.networkName) as AppCompatTextView?

            inflatedView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            inflatedView = convertView
        }

        holder.networkName?.text = item.SSID
        holder.icon?.setImageResource(getImageForNetwork(item))
        return inflatedView

    }

    private fun getImageForNetwork(item: ScanResult): Int {
        val rangeLevel = calculateSignalLevel(item.level, 4)
        when (isNetworkOpened(item)) {
            false -> {
                when (rangeLevel) {
                    0 -> return R.drawable.ic_signal_wifi_0_bar_black_24dp
                    1 -> return R.drawable.ic_signal_wifi_1_bar_lock_black_24dp
                    2 -> return R.drawable.ic_signal_wifi_2_bar_lock_black_24dp
                    3 -> return R.drawable.ic_signal_wifi_3_bar_lock_black_24dp
                    4 -> return R.drawable.ic_wifi_lock_black_24dp
                }
            }
            true -> {
                when (rangeLevel) {
                    0 -> return R.drawable.ic_signal_wifi_0_bar_black_24dp
                    1 -> return R.drawable.ic_signal_wifi_1_bar_black_24dp
                    2 -> return R.drawable.ic_signal_wifi_2_bar_black_24dp
                    3 -> return R.drawable.ic_signal_wifi_3_bar_black_24dp
                    4 -> return R.drawable.ic_signal_wifi_4_bar_black_24dp
                }
            }
        }
        return R.drawable.ic_signal_wifi_0_bar_black_24dp
    }

    private fun isNetworkOpened(network: ScanResult): Boolean {
        val temp = network.capabilities.toUpperCase()
        return !(temp.contains("WEP") || temp.contains("WPA") || temp.contains("WPA2"))
    }

    internal class ViewHolder {
        var networkName: AppCompatTextView? = null
        var icon: ImageView? = null
    }
}
