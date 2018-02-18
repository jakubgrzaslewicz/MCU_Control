package jakubgrzaslewicz.pl.mcucontrol.Classes

import io.realm.Realm
import jakubgrzaslewicz.pl.mcucontrol.RealmModels.Device

/**
 * Created by Jakub Grząślewicz on 18.02.2018.
 *
 */
class DatabaseUtils(var realm: Realm) {
    fun DeleteDevice(device: Device) {
        realm.beginTransaction()
        val res = realm.where(Device::class.java).equalTo("access_point_name", device.access_point_name).findFirst()
        res!!.deleteFromRealm()
        if (realm.isInTransaction)
            realm.commitTransaction()
    }
}