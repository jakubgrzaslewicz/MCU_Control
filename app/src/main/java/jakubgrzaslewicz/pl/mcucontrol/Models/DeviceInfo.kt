package jakubgrzaslewicz.pl.mcucontrol.Models

import com.google.gson.annotations.SerializedName

/**
 * Created by jakub on 15.01.2018.
 */

class DeviceInfo {
    @SerializedName("MCU_HUB_CLIENT")
    var Client: ClientDetails? = null
    @SerializedName("DEVICE")
    var Device: HardwareSpecification? = null

    inner class ClientDetails {
        @SerializedName("AP_SSID")
        var Ap_Ssid: String? = null

        @SerializedName("JSON-API-VER")
        var ApiVersion: String? = null
    }

    inner class HardwareSpecification {
        @SerializedName("CHIP_ID")
        var ChipId: Int = 0

        @SerializedName("FLASH_MODE")
        var FlashMode: Int = 0

        @SerializedName("DEV_VER")
        var DevVer: Int = 0

        @SerializedName("FLASH_SPEED")
        var FlashSpeed: Int = 0

        @SerializedName("MINOR_VER")
        var MinorVer: Int = 0

        @SerializedName("FLASH_SIZE")
        var FlashSize: Int = 0

        @SerializedName("MAJOR_VER")
        var MajorVer: Int = 0

        @SerializedName("FLASH_ID")
        var FlashId: Int = 0
    }
}
