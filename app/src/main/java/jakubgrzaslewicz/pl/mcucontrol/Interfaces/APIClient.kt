package jakubgrzaslewicz.pl.mcucontrol.Interfaces

import jakubgrzaslewicz.pl.mcucontrol.Models.ConfigureResponse
import jakubgrzaslewicz.pl.mcucontrol.Models.ConnectToApResponse
import jakubgrzaslewicz.pl.mcucontrol.Models.DeviceInfo
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Jakub Grząślewicz on 15.01.2018.
 */

interface APIClient {
    @GET("/device-info")
    fun GetDeviceInfo(): Call<DeviceInfo>

    @POST("/set-config")
    fun SetConfigurationParameter(@Body body: String): Call<ConfigureResponse>

    @POST("/connect-to-ap")
    fun ConnectToAp(@Body body: RequestBody): Call<ConnectToApResponse>
}
