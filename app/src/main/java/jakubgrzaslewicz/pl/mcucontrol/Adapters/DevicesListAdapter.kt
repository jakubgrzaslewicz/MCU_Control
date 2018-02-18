package jakubgrzaslewicz.pl.mcucontrol.Adapters

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jakubgrzaslewicz.pl.mcucontrol.R
import jakubgrzaslewicz.pl.mcucontrol.RealmModels.Device

/**
 * Created by Jakub Grząślewicz on 18.02.2018.
 *
 */
class DevicesListAdapter(context: Context,  var list: ArrayList<Device>) : ArrayAdapter<Device>(context, R.layout.devices_list_row, list) {
    var vi: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val retView: View
        val device = list.get(position)
        if (convertView == null) {
            retView = vi.inflate(R.layout.devices_list_row, null)
            holder = ViewHolder()

            holder.name = retView.findViewById(R.id.name)

            retView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }
        holder.name.text = device.custom_name
        holder.name.setOnClickListener({})
        return retView
    }

    override fun getItem(position: Int): Device {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }


    internal class ViewHolder {
        lateinit var name: AppCompatTextView
    }
}