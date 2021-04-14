package com.zelyder.mediaclient.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zelyder.mediaclient.MyApp
import com.zelyder.mediaclient.R
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
//    private lateinit var mSocket: Socket
//    private lateinit var onNewMessage: Emitter.Listener
//    private val refreshEvent = "screen refresh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        val instance = application as MyApp
//        val mSocket: Socket = instance.getSocketInstance()
//        val onNewMessage = Emitter.Listener { args ->
//            this.runOnUiThread(Runnable {
//                val data = args[0] as JSONObject
//                try {
//                    Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show()
//                    Log.d("LOL", data.toString())
//                } catch (e: JSONException) {
//                    return@Runnable
//                }
//            })
//        }
//
//        mSocket.on(refreshEvent, onNewMessage)
//        mSocket.connect()
//
//        if (mSocket.connected()){
//            Toast.makeText(this, "Socket Connected!!", Toast.LENGTH_SHORT).show()
//        }

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mSocket.disconnect()
//        mSocket.off(refreshEvent, onNewMessage)
//    }
}