package app.oson.business.api.services

import app.oson.business.api.Api
import app.oson.business.database.Preferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class BaseRestService {
    private var api: Api? = null

    fun getApi(): Api {
        if (api == null) {
            var retrofit: Retrofit
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val builder = chain.request().newBuilder()

                        val pref = Preferences.getInstance()
                        if (pref.getUserData() != null)
                            builder.addHeader("token", pref.getUserData()!!.token)

                        var request = builder.build()

                        var response = chain.proceed(request)

                        return response
                    }
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Api.API_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            api = retrofit.create(Api::class.java!!)
        }


        return api!!
    }

    fun getApiOther(): Api {
        if (api == null) {
            var retrofit: Retrofit
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Api.API_HOST_OTHER)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            api = retrofit.create(Api::class.java!!)
        }


        return api!!
    }

}