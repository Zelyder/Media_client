package com.zelyder.mediaclient.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.CURRENT_FRAGMENT
import com.zelyder.mediaclient.data.PLAYER_FRAGMENT


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("debug", "we are here")
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if(CURRENT_FRAGMENT == PLAYER_FRAGMENT){
//                    toSettingsFragment()
                }else {
                    return false
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun toSettingsFragment() {
        findNavController(R.id.nav_host_fragment).navigate(PlayerFragmentDirections.actionPlayerFragmentToScreenIdFragment())
    }
}

