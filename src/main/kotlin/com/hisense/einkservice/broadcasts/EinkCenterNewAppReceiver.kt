package com.hisense.einkservice.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.services.EinkAccessibility

class EinkCenterNewAppReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra("packageName")
        val speed = intent.getCharExtra("speed", EinkSpeed.BALANCED.toChar())
        val accessibilityService = EinkAccessibility.getInstance()
        accessibilityService.setSpeedForApp(packageName, EinkSpeed.fromChar( speed ))
    }
}
