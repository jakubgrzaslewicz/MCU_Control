package jakubgrzaslewicz.pl.mcucontrol.Classes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import org.afinal.simplecache.ACache

import io.realm.Realm

/**
 * Created by Jakub Grząślewicz on 16.01.2018.
 */

open class Activity : AppCompatActivity() {
    lateinit var Cache: ACache
    lateinit var realm: Realm
    lateinit var DBUtils: DatabaseUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Cache = ACache.get(this)
        realm = Realm.getDefaultInstance()
        DBUtils = DatabaseUtils(realm)
    }


    companion object {
        var TAG = "MCU_CONTROL"
    }
}
