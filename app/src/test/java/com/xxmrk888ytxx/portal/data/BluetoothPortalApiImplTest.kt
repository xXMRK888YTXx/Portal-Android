package com.xxmrk888ytxx.portal.data

import android.util.Log
import com.xxmrk888ytxx.portal.data.model.BluetoothUnlockResponse
import com.xxmrk888ytxx.portal.data.model.PairResponse
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.connection.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BluetoothPortalApiImplTest {

    private val bluetoothManager: BluetoothManager = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var api: BluetoothPortalApiImpl
    
    private val mockConnection: BluetoothConnection = mockk(relaxed = true)
    private val incomingDataFlow = MutableSharedFlow<ByteArray>(replay = 1)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0

        api = BluetoothPortalApiImpl(bluetoothManager, json)
        every { mockConnection.incomingData } returns incomingDataFlow
        coEvery { bluetoothManager.openConnection(any()) } returns mockConnection
    }

    @Test
    fun `pair should send pair code and return clientId on success`() = runTest {
        // Arrange
        val pairedDevice = PairedBluetoothDevice("PC", "00:11:22:33:44:55")
        val pairCode = "123456"
        val expectedClientId = "client_123"
        val responseJson = json.encodeToString(PairResponse(clientId = expectedClientId))
        
        // Act
        val pairResult = async {
            api.pair(pairedDevice, pairCode)
        }
        
        // Симулируем получение данных от сервера
        incomingDataFlow.emit(responseJson.toByteArray())
        
        assertEquals(expectedClientId, pairResult.await().clientId)

        // Assert
        coVerify { bluetoothManager.openConnection(pairedDevice.macAddress) }
        coVerify { mockConnection.sendData(any()) }
    }

    @Test
    fun `unlock should send clientId and return true on success`() = runTest {
        // Arrange
        val device = BluetoothDevice(
            clientId = "client_123",
            name = "PC",
            macAddress = "00:11:22:33:44:55"
        )
        val responseJson = json.encodeToString(BluetoothUnlockResponse(isSuccessful = true))
        
        // Act
        val unlockResult = async {
            api.unlock(device)
        }
        
        incomingDataFlow.emit(responseJson.toByteArray())
        
        assertTrue(unlockResult.await())

        // Assert
        coVerify { bluetoothManager.openConnection(device.macAddress) }
    }
}
