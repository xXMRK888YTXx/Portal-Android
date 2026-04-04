package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.exception.DeviceNotFoundException
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod as DeviceConfigurationUnlockMethod
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.BluetoothManager
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
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val bluetoothManager: BluetoothManager
) : ProvideDeviceInfoContract {
    override suspend fun provideDeviceInfo(clientId: String): Flow<Device> {
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId)
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId)
        val deviceSetting = deviceSettingsRepository.getDeviceSettingsByDeviceId(clientId)
        return combine(
            wifiDevice,
            bluetoothDevice,
            deviceSetting,
            bluetoothManager.pairedDeviceMacAddresses
        ) { wifiDevice, bluetoothDevice, deviceSettings, pairedDeviceMacAddresses ->
            val deviceSettings = deviceSettings ?: throw DeviceNotFoundException(clientId)
            when {
                wifiDevice != null -> Device.WifiDevice(
                    clientId = wifiDevice.clientId,
                    deviceName = wifiDevice.deviceName,
                    host = wifiDevice.host,
                    clientCertificateFingerprint = certificateRepository.getX509CertificateFingerprint(
                        wifiDevice.clientCertificate.x509Certificate
                    ),
                    awaitUnlockRequests = deviceSettings.awaitUnlockRequests,
                    serverCertificateFingerprint = wifiDevice.serverCertificateFingerprint,
                    searchIpDynamically = deviceSettings.searchIpDynamically,
                    unlockMethod = deviceSettings.unlockMethod.toDeviceConfigurationUnlockMethod(
                        deviceSettings.unlockOnlyWhenScreenUnlocked
                    ),
                    wolMacAddress = wifiDevice.wolMacAddress?.filter { it != ':' }
                )

                bluetoothDevice != null -> Device.BluetoothDevice(
                    clientId = bluetoothDevice.clientId,
                    deviceName = bluetoothDevice.name,
                    macAddress = bluetoothDevice.macAddress,
                    awaitUnlockRequests = deviceSettings.awaitUnlockRequests,
                    unlockMethod = deviceSettings.unlockMethod.toDeviceConfigurationUnlockMethod(
                        deviceSettings.unlockOnlyWhenScreenUnlocked
                    ),
                    isPaired = pairedDeviceMacAddresses?.contains(bluetoothDevice.macAddress) ?: true
                    // If pairedDeviceMacAddresses?.contains(bluetoothDevice.macAddress) == null its mean permission not grated
                )

                else -> throw DeviceNotFoundException(clientId)
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