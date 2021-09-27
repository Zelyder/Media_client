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
}

