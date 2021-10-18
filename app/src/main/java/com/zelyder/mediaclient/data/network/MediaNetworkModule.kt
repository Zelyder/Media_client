package com.zelyder.mediaclient.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.create
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class MediaNetworkModule(val context: Context) {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    companion object {
        private const val TAG = "MediaNetworkModule"
        private const val TIMEOUT = 10L
    }

    private val httpClient = OkHttpClient.Builder()
//        .addInterceptor(NoConnectionInterceptor(context))
        .addInterceptor(
        Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                .build()
            Log.d(TAG, request.toString())
            it.proceed(request)
        }
    )
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    @ExperimentalSerializationApi
    private val mediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .build()

    @ExperimentalSerializationApi
    fun mediaApi(): MediaApi = mediaRetrofit.create()


}

class NoConnectionInterceptor(val context: Context) : Interceptor {

    @Suppress("DEPRECATION")
    private fun preAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null) {
            return (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isConnectionOn(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager

        return if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M
        ) {
            postAndroidMInternetCheck(connectivityManager)
        } else {
            preAndroidMInternetCheck(connectivityManager)
        }
    }

    private fun getIpFromURL(url: String):String{
        val ipAddressPattern =
            "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"

        val pattern: Pattern = Pattern.compile(ipAddressPattern)
        val matcher: Matcher = pattern.matcher(url)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "0.0.0.0"
        }
    }

    private fun getPortFromURL(url: String): Int {
        val ipAddressPattern =
            ":(\\d{1,5})"

        val pattern: Pattern = Pattern.compile(ipAddressPattern)
        val matcher: Matcher = pattern.matcher(url)
        return if (matcher.find()) {
            matcher.group().drop(1).toInt()
        } else {
            8800
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress(getIpFromURL(BASE_URL), getPortFromURL(BASE_URL))

            sock.connect(sockaddr, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if(!isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }

}

class NoConnectivityException : IOException() {
    override val message: String
        get() =
            "No network available, please check your WiFi or Data connection"
}

class NoInternetException : IOException() {
    override val message: String
        get() =
            "No connection to remote server, please turn on the server"
}
