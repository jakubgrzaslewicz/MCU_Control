package jakubgrzaslewicz.pl.mcucontrol.RealmModels

import java.util.Date

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import org.jetbrains.annotations.NotNull

/**
 * Created by Jakub Grząślewicz on 11.02.2018.
 */

open class Device : RealmObject() {

    @NotNull
    var added_time: Date? = null
    @PrimaryKey
    @NotNull
    var access_point_name: String? = null
    var custom_name:String? = ""
        get() = if (field.isNullOrEmpty()) access_point_name else field
    @NotNull
    var password: String? = null
}
