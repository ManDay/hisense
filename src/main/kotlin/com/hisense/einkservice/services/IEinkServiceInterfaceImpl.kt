package com.hisense.einkservice.services

import android.util.Log
import android.net.LocalSocket
import android.net.LocalSocketAddress
import com.hisense.einkservice.IEinkServiceInterface
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.observers.NightLightIntensityObserver
import java.io.FileOutputStream
import java.io.IOException

class IEinkServiceInterfaceImpl : IEinkServiceInterface.Stub() {
    private val TAG = IEinkServiceInterfaceImpl::class.java.getSimpleName()
    private val SRV_SOCKET = "testsocket"
    
    fun srvSet( tgt: Byte,v: ByteArray ) {
     val socket = LocalSocket( LocalSocket.SOCKET_SEQPACKET )
     socket.connect( LocalSocketAddress( SRV_SOCKET ) )
     socket.outputStream.write( byteArrayOf( tgt )+ v )
     socket.outputStream.flush( )
     socket.close( )
    }
    
    fun srvGet( tgt: Byte,r: ByteArray? ) {
     val socket = LocalSocket( LocalSocket.SOCKET_SEQPACKET )
     socket.connect( LocalSocketAddress( SRV_SOCKET ) )
     socket.outputStream.write( tgt )
     socket.outputStream.flush( )
     r?.let { socket.inputStream.read( r ) }
     socket.close( )
    }

    override fun setSpeed(speed: Int) {
        Log.i(TAG, "setting speed mode: $speed")
        srvSet( 'm'.code.toByte( ),byteArrayOf( speed.toByte( ) ) );
    }

    override fun clearScreen() {
        Log.i(TAG, "clearing screen")
        srvGet( 'c'.code.toByte( ),null );
    }

    override fun getCurrentSpeed(): Int {
        val r = ByteArray(1)
        srvGet( 'm'.code.toByte( ),r )
        
        return r[0].toInt()
    }

    override fun setTemperature(isNightLight: Boolean, brightness: Int) {
        val originalScale = NightLightIntensityObserver.originalScale(brightness)
        if (isNightLight) {
            setNightLight(true)
//            sendCommand( "y" + originalScale.toString() )
        } else {
            setNightLight(false)
//            sendCommand( "w" + originalScale.toString() )
        }
    }

    private fun setNightLight(enabled: Boolean) {
        if (enabled) {
//            sendCommand( "w" + 0.toString() )
        } else {
//            sendCommand( "y" + 0.toString() )
        }
    }

    override fun isNightLight(): Boolean {
        /*val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return whiteBrightness == 0 && yellowBrightness > 0*/
        return false
    }

    override fun getBrightness(): Int {
        /*val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return if (whiteBrightness == 0) {
            yellowBrightness
        } else {
            whiteBrightness
        }*/
        return 0
    }

    override fun setLockedScreen(lockscreen: CharArray?) {
        // TODO
    }
}
