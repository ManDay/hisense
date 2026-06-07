package com.hisense.einkservice.services

import android.util.Log
import android.net.LocalSocket
import android.net.LocalSocketAddress
import com.hisense.einkservice.IEinkServiceInterface
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.observers.NightLightIntensityObserver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IEinkServiceInterfaceImpl : IEinkServiceInterface.Stub() {
    private val TAG = IEinkServiceInterfaceImpl::class.java.getSimpleName()
    private val SRV_SOCKET = "testsocket"
    
    private val EINK_PATH = "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/"
    private val YELLOW_LED = "/sys/devices/platform/soc/4a84000.i2c/i2c-1/1-0036/backlight/aw99703-bl-1/brightness"
    private val WHITE_LED = "/sys/devices/platform/soc/4a80000.i2c/i2c-0/0-0036/backlight/aw99703-bl-2/brightness"
    
    fun sendCommand( cmd: String ) {
     val socket = LocalSocket( LocalSocket.SOCKET_DGRAM )
     socket.connect( LocalSocketAddress( SRV_SOCKET ) )
     socket.outputStream.write( cmd.toByteArray() )
     socket.outputStream.flush( )
     socket.disconnect( )
    }

    override fun setSpeed(speed: Char) {
        Log.i(TAG, "setting speed mode: $speed")
        sendCommand( speed.toString() )
    }

    override fun clearScreen() {
        Log.i(TAG, "clearing screen")
        sendCommand( "r" )
    }

    override fun getCurrentSpeed(): Char {
        val speed = readFromFile(EINK_PATH + "epd_display_mode")
        return EinkSpeed.fromInt( speed.filter { it.isDigit() }.toInt() ).toChar( )
    }

    override fun setTemperature(isNightLight: Boolean, brightness: Int) {
        val originalScale = NightLightIntensityObserver.originalScale(brightness)
        if (isNightLight) {
            setNightLight(true)
            sendCommand( "y" + originalScale.toString() )
        } else {
            setNightLight(false)
            sendCommand( "w" + originalScale.toString() )
        }
    }

    private fun setNightLight(enabled: Boolean) {
        if (enabled) {
            sendCommand( "w" + 0.toString() )
        } else {
            sendCommand( "y" + 0.toString() )
        }
    }

    override fun isNightLight(): Boolean {
        val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return whiteBrightness == 0 && yellowBrightness > 0
    }

    override fun getBrightness(): Int {
        val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return if (whiteBrightness == 0) {
            yellowBrightness
        } else {
            whiteBrightness
        }
    }

    override fun setLockedScreen(lockscreen: CharArray?) {
        // TODO
    }

    private fun readFromFile(
        filename: String,
    ): String {
        try {
            val file = File(filename)
            val stream = file.inputStream()
            return stream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "File read failed: $e")
        }
        return ""
    }
}
