package jakubgrzaslewicz.pl.mcucontrol.Classes

import android.app.Application
import android.util.Log

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Jakub Grząślewicz on 18.02.2018.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
                .name("MCUControl.realm")
                .schemaVersion(3)
                .migration(RealmMigrations())
                .build()
        Realm.setDefaultConfiguration(configuration)
        val realm = Realm.getInstance(configuration)
        Log.i(Activity.TAG, "Current Realm version: " + realm.configuration.schemaVersion.toString())

    }

    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
    }
}
