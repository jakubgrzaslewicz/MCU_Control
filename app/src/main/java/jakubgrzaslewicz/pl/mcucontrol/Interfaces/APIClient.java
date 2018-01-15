package jakubgrzaslewicz.pl.mcucontrol.Interfaces;

import jakubgrzaslewicz.pl.mcucontrol.Models.DeviceInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Jakub Grząślewicz on 15.01.2018.
 *
 */

public interface APIClient {
        @GET("/device-info")
        Call<DeviceInfo> GetDeviceInfo();
}
