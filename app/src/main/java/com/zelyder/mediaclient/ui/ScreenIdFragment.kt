package com.zelyder.mediaclient.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zelyder.mediaclient.MyApp
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.CURRENT_FRAGMENT
import com.zelyder.mediaclient.data.SETTINGS_FRAGMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class ScreenIdFragment : Fragment() {

    private lateinit var etScreenId: EditText
    private lateinit var etIp: EditText
    private lateinit var btnOk: Button

    private lateinit var sharePrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CURRENT_FRAGMENT = SETTINGS_FRAGMENT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_id, container, false)
    }

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etScreenId = view.findViewById(R.id.etScreenId)
        etIp = view.findViewById(R.id.etIp)
        btnOk = view.findViewById(R.id.btnScreenId)

        sharePrefs = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)


        val screenId = sharePrefs.getInt(KEY_SCREEN_ID, 0)
        val screenIp: String = sharePrefs.getString(KEY_SCREEN_IP, BASE_URL)!!


        if (savedInstanceState == null) {
            if (screenId in 1..6) {
                etScreenId.setText(screenId.toString())
            }
            etIp.setText(screenIp)
            if (screenId != 0) {
                //toPlayerFragment(screenId, screenIp)
            }
        }


        btnOk.setOnClickListener {
            val text = etScreenId.text.toString()
            (this.activity?.application as MyApp).updateIp(etIp.text.toString())
            if (text.isNotEmpty() && text.isDigitsOnly() && text.toIntOrNull() != null && text.toIntOrNull() in 1..6) {
                val id = text.toInt()

//                var ient: InetAddress?
//                ient = null
//                try {
//                    ient = InetAddress.getByName(etIp.text.toString())
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                try {
//                    if (ient?.isReachable(5000) == true) {
//                        toPlayerFragment(id, etIp.text.toString())
//                    } else {
//                        etIp.error = "No responde: Time out"
//                    }
//                } catch (e: IOException) {
//                    etIp.error = e.toString()
//                }
                btnOk.isActivated = false
                GlobalScope.launch(Dispatchers.Main) {4
                    val strIp = etIp.text.toString()
//                    val isConnect = isHostAvailable(clearUrlToIp(strIp), getPort(strIp) ?: 0, 1000)
                    if (true) {
                        toPlayerFragment(id, etIp.text.toString())
                    } else {
                        etIp.error = "Ip is not responding"
                        btnOk.isActivated = true
                    }
                }


            } else {
                etScreenId.error = getString(R.string.screen_id_et_error_msg)
            }

        }


    }

    private fun toPlayerFragment(id: Int, ip: String) {
        val editor = sharePrefs.edit()
        findNavController().navigate(
            ScreenIdFragmentDirections.actionScreenIdFragmentToPlayerFragment(
                id,
                ip
            )
        )
        editor.putInt(KEY_SCREEN_ID, id)
        editor.putString(KEY_SCREEN_IP, ip)
        editor.apply()
    }

    fun isConnectedToInternet(url: String): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 $url")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    private suspend fun checkConnection(url: String): Boolean = withContext(Dispatchers.IO){
        val newUrl = clearUrlToIp(url) ?: false
        println("executeCommand")
        val runtime = Runtime.getRuntime()
        try {
            val mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 $newUrl")
            val mExitValue = mIpAddrProcess.waitFor()
            println(" mExitValue $mExitValue")
            mExitValue == 0
        } catch (ignore: InterruptedException) {
            ignore.printStackTrace()
            println(" Exception:$ignore")
            false
        } catch (e: IOException) {
            e.printStackTrace()
            println(" Exception:$e")
            false
        }
    }

    fun clearUrlToIp(str: String): String? {
        val matchResult = Regex("""([0-9]{1,3}[\.]){3}[0-9]{1,3}""").find(str)//(:[0-9]{4,5})?
            ?: return null
        return matchResult.value
    }
    fun getPort(url: String): Int? {
        val matchResult = Regex("""(:[0-9]{4,5})""").find(url)
            ?: return null
        return matchResult.value.replace(":","").toInt()
    }

    fun executeCmd(cmd: String, sudo: Boolean): String? {
        try {
            val p: Process
            p = if (!sudo) Runtime.getRuntime().exec(cmd) else {
                Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            }
            val stdInput = BufferedReader(InputStreamReader(p.inputStream))
            var s: String
            var res = ""
            while (stdInput.readLine().also { s = it } != null) {
                res += """
                $s
                
                """.trimIndent()
            }
            p.destroy()
            return res
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun isHostAvailable(host: String?, port: Int, timeout: Int): Boolean {
        try {
            Socket().use { socket ->
                val inetAddress: InetAddress = InetAddress.getByName(host)
                val inetSocketAddress = InetSocketAddress(inetAddress, port)
                socket.connect(inetSocketAddress, timeout)
                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

}




const val SHARED_PREF_NAME = "preferences"
const val KEY_SCREEN_ID = "pref_screen_id"
const val KEY_SCREEN_IP = "pref_screen_ip"