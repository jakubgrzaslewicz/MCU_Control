package jakubgrzaslewicz.pl.mcucontrol.Classes

import jakubgrzaslewicz.pl.mcucontrol.Interfaces.APIClient
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.ByteString
import retrofit2.Converter
import java.io.IOException


/**
* Created by Jakub Grząślewicz on 15.01.2018.
*
*/

class McuApiService {
    fun GetService(): APIClient {

        val API_BASE_URL = "http://10.10.0.1/"
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        val builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )

        val retrofit = builder.client(client)
                .build()

        return retrofit.create(APIClient::class.java)
    }

}
