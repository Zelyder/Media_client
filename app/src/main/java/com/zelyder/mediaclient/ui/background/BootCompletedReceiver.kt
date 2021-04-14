package com.zelyder.mediaclient.ui.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val intent = Intent()
            intent.setClassName("com.zelyder.mediaclient", "com.zelyder.mediaclient.ui.MainActivity")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }
}