package jakubgrzaslewicz.pl.mcucontrol.Interfaces;

import org.jetbrains.annotations.NotNull;

import jakubgrzaslewicz.pl.mcucontrol.Models.ConfigureResponse;
import jakubgrzaslewicz.pl.mcucontrol.Models.ConnectToApResponse;
import jakubgrzaslewicz.pl.mcucontrol.Models.DeviceInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Jakub Grząślewicz on 15.01.2018.
 */

public interface APIClient {
    @GET("/device-info")
    Call<DeviceInfo> GetDeviceInfo();

    @POST("/set-config")
    Call<ConfigureResponse> SetConfigurationParameter(@Body String body);

    @POST("/connect-to-ap")
    Call<ConnectToApResponse> ConnectToAp(@Body RequestBody body);
}
