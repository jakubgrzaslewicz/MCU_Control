package jakubgrzaslewicz.pl.mcucontrol.Classes

import android.util.Log
import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmObjectSchema
import io.realm.RealmSchema

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by Jakub Grząślewicz on 18.02.2018.
 */

class RealmMigrations : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        Log.i("tag", oldVersion.toString())
        if (oldVersion == 0L) {
            val userSchema = schema.get("Device")
            userSchema!!.addField("custom_name", String::class.javaPrimitiveType)
            userSchema.addField("password", String::class.javaPrimitiveType)
            userSchema.addPrimaryKey("access_point_name")
            userSchema.addIndex("access_point_name")
        } else if(oldVersion == 1L){
            val userSchema = schema.get("Device")
            userSchema!!.setRequired("added_time",true)
            userSchema.setRequired("access_point_name",true)
            userSchema.setRequired("password",true)
        }
    }
}
