package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.exception.DeviceNotFoundException
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod as DeviceConfigurationUnlockMethod
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.UnlockMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProvideDeviceInfoContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val certificateRepository: CertificateManager,
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ProvideDeviceInfoContract {
    override suspend fun provideDeviceInfo(deviceId: String): Flow<Device> {
        val wifiDevice = wifiDeviceRepository.getDeviceById(deviceId)
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(deviceId)
        val deviceSetting = deviceSettingsRepository.getDeviceSettingsByDeviceId(deviceId)
        return combine(
            wifiDevice,
            bluetoothDevice,
            deviceSetting
        ) { wifiDevice, bluetoothDevice, deviceSettings ->
            val deviceSettings = deviceSettings ?: throw DeviceNotFoundException(deviceId)
            when {
                wifiDevice != null -> Device.WifiDevice(
                    deviceId = wifiDevice.deviceId,
                    deviceName = wifiDevice.deviceName,
                    host = wifiDevice.host,
                    clientCertificateFingerprint = certificateRepository.getX509CertificateFingerprint(
                        wifiDevice.clientCertificate.x509Certificate
                    ),
                    awaitUnlockRequests = deviceSettings.awaitUnlockRequests,
                    serverCertificateFingerprint = wifiDevice.serverCertificateFingerprint,
                    searchIpDynamically = deviceSettings.searchIpDynamically,
                    unlockMethod = deviceSettings.unlockMethod.toDeviceConfigurationUnlockMethod(deviceSettings.unlockOnlyWhenScreenUnlocked)
                )

                bluetoothDevice != null -> Device.BluetoothDevice(
                    deviceId = bluetoothDevice.clientId,
                    deviceName = bluetoothDevice.name,
                    macAddress = bluetoothDevice.macAddress,
                    awaitUnlockRequests = deviceSettings.awaitUnlockRequests,
                    unlockMethod = deviceSettings.unlockMethod.toDeviceConfigurationUnlockMethod(deviceSettings.unlockOnlyWhenScreenUnlocked)
                )

                else -> throw DeviceNotFoundException(deviceId)
            }
        }
    }

    private fun UnlockMethod.toDeviceConfigurationUnlockMethod(unlockOnlyWhenScreenUnlocked: Boolean): DeviceConfigurationUnlockMethod {
        return when (this) {
            is UnlockMethod.Automatic -> DeviceConfigurationUnlockMethod.Automatic(
                unlockOnlyWhenScreenUnlocked
            )

            is UnlockMethod.ConfirmationScreen -> DeviceConfigurationUnlockMethod.ConfirmationScreen
            is UnlockMethod.Notification -> DeviceConfigurationUnlockMethod.Notification
        }
    }
}