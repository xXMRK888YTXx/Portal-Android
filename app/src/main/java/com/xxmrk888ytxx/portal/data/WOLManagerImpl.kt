package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.WOLManager
import kotlinx.coroutines.Dispatchers
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

class WOLManagerImpl @Inject constructor() : WOLManager {
    override suspend fun sendWOLRequest(macAddress: String): Result<Unit> = runCatching(Dispatchers.IO) {
        val macBytes = getMacBytes(macAddress)
        val bytes = ByteArray(6 + 16 * macBytes.size)

        // Заполняем первые 6 байт значением 0xff
        for (i in 0 until 6) {
            bytes[i] = 0xff.toByte()
        }

        // Повторяем MAC-адрес 16 раз
        var i = 6
        while (i < bytes.size) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
            i += macBytes.size
        }

        val address = InetAddress.getByName(BROADCAST_IP)
        val port = WOL_PORT
        val packet = DatagramPacket(bytes, bytes.size, address, port)
        val socket = DatagramSocket()
        socket.send(packet)
        socket.close()
    }

    private fun getMacBytes(macAddress: String): ByteArray {
        val bytes = ByteArray(6)
        val hex = macAddress.split(":", "-")
        if (hex.size != 6) throw IllegalArgumentException("Invalid MAC address.")

        try {
            for (i in 0 until 6) {
                bytes[i] = hex[i].toInt(16).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid hex digit in MAC address.")
        }
        return bytes
    }

    companion object {
        const val BROADCAST_IP = "255.255.255.255"
        const val WOL_PORT = 7
    }
}