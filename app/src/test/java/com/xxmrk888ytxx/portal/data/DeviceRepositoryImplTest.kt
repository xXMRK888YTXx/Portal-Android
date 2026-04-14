package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceRepositoryImplTest {

    private val deviceDao: DeviceDao = mockk()
    private val shortcutRepository: ShortcutRepository = mockk()
    private val shortcutManager: ShortcutManager = mockk()
    private lateinit var repository: DeviceRepositoryImpl

    @Before
    fun setup() {
        repository = DeviceRepositoryImpl(deviceDao, shortcutRepository, shortcutManager)
    }

    @Test
    fun `when removeDevice is called, device is removed and its shortcuts are also removed`() = runTest {
        // Arrange
        val deviceId = "test_device_id"
        val shortcutId = "test_shortcut_id"
        val shortcuts = listOf(
            Shortcut(
                shortcutId = shortcutId,
                clientId = deviceId,
                isRequiredBiometricUnlock = false,
                isSendWOLRequest = false
            )
        )
        
        coEvery { shortcutRepository.getShortcutsByClientId(deviceId) } returns shortcuts
        coEvery { deviceDao.removeDevice(deviceId) } returns Unit
        coEvery { shortcutManager.removeShortcut(shortcutId) } returns Unit

        // Act
        repository.removeDevice(deviceId)

        // Assert
        coVerify { deviceDao.removeDevice(deviceId) }
        coVerify { shortcutManager.removeShortcut(shortcutId) }
    }
}
