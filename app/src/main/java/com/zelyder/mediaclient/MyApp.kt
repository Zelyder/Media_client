package com.zelyder.mediaclient

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.MEDIA_BASE_URL
import com.zelyder.mediaclient.data.network.MediaNetworkModule
import com.zelyder.mediaclient.domain.datasources.RemoteDataSourceImpl
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import com.zelyder.mediaclient.domain.repositories.MediaRepositoryImpl
import com.zelyder.mediaclient.ui.core.ViewModelFactory
import com.zelyder.mediaclient.ui.core.ViewModelFactoryProvider
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class MyApp: Application(), ViewModelFactoryProvider {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mediaRepository: MediaRepository
    private  var iSocket: Socket? = null

    @ExperimentalSerializationApi
    override fun onCreate() {
        super.onCreate()

        initRepositories()

    }



    @ExperimentalSerializationApi
    private fun initRepositories() {

        val remoteDateSource = RemoteDataSourceImpl(MediaNetworkModule().mediaApi())

        mediaRepository = MediaRepositoryImpl(remoteDateSource)

        viewModelFactory = ViewModelFactory(mediaRepository)
    }

    override fun viewModelFactory(): ViewModelFactory  = viewModelFactory

    private fun updateSocket(){
        try {
            iSocket = getSocketInstance()
        } catch (e: URISyntaxException) {
            Log.e("LOL", e.message.toString())
            Toast.makeText(
                this, "Connection failed!! \n" +
                        " Try use up Key and input correct ip", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @ExperimentalSerializationApi
    fun updateIp(ip: String) {
        BASE_URL = ip
        MEDIA_BASE_URL = "${BASE_URL}screen/"
        updateSocket()
        initRepositories()
    }
    /**
     * To get socket single instance.
     *
     * @return socket instance
     */
    fun getSocketInstance(): Socket? {
        if (iSocket != null) return iSocket else {
            try {
                val myHostnameVerifier: HostnameVerifier =
                    HostnameVerifier { _, _ -> true }
                val mySSLContext: SSLContext = SSLContext.getInstance("TLS")
                val trustAllCerts: Array<TrustManager> =
                    arrayOf<TrustManager>(object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: kotlin.String?
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: kotlin.String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return  emptyArray()
                        }
                    })
                mySSLContext.init(null, trustAllCerts, SecureRandom())
                val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .hostnameVerifier(myHostnameVerifier)
                    .sslSocketFactory(mySSLContext.socketFactory, object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }


                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return emptyArray()
                        }
                    })

                .build()


                // HttpsURLConnection.setDefaultHostnameVerifier(myHostnameVerifier);
                val options: IO.Options = IO.Options()
                //options.webSocketFactory = okHttpClient;
                //options.secure = true;
                //options.transports = new String[]{WebSocket.NAME};
                //options.reconnection = true;
                //options.forceNew = true;
                options.callFactory = okHttpClient
                options.webSocketFactory = okHttpClient
                iSocket = IO.socket(BASE_URL, options)
                iSocket?.connect()
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
        }
        return iSocket
    }

}

fun Context.viewModelFactoryProvider() = (applicationContext as MyApp)

fun Fragment.viewModelFactoryProvider() = requireContext().viewModelFactoryProvider()