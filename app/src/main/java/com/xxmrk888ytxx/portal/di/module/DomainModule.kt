package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.data.AwaitUnlockRequestManagerImpl
import com.xxmrk888ytxx.portal.data.BiometricDialogControllerImpl
import com.xxmrk888ytxx.portal.data.BiometricRequestManager
import com.xxmrk888ytxx.portal.data.BluetoothManagerImpl
import com.xxmrk888ytxx.portal.data.BluetoothPortalApiImpl
import com.xxmrk888ytxx.portal.data.CertificateManagerImpl
import com.xxmrk888ytxx.portal.data.WifiDeviceRepositoryImpl
import com.xxmrk888ytxx.portal.data.DeviceSettingsRepositoryImpl
import com.xxmrk888ytxx.portal.data.DeviceUnlockManagerImpl
import com.xxmrk888ytxx.portal.data.MdnsManagerImpl
import com.xxmrk888ytxx.portal.data.PermissionManagerImpl
import com.xxmrk888ytxx.portal.data.WifiPortalApiImpl
import com.xxmrk888ytxx.portal.data.PreferenceManagerImpl
import com.xxmrk888ytxx.portal.data.QRScannerManagerImpl
import com.xxmrk888ytxx.portal.data.SecureStorageImpl
import com.xxmrk888ytxx.portal.data.ShortcutManagerImpl
import com.xxmrk888ytxx.portal.data.ShortcutRepositoryImpl
import com.xxmrk888ytxx.portal.data.UnlockRequestHandlerImpl
import com.xxmrk888ytxx.portal.data.UnlockScreenManagerImpl
import com.xxmrk888ytxx.portal.data.UnlockServiceManagerImpl
import com.xxmrk888ytxx.portal.domain.AwaitUnlockRequestManager
import com.xxmrk888ytxx.portal.domain.BiometricActivityResultReceiver
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.BluetoothPortalApi
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import com.xxmrk888ytxx.portal.domain.QRScannerManager
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockScreenManager
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {
    @Binds
    fun bindsPreferenceManager(
        preferenceManagerImpl: PreferenceManagerImpl
    ) : PreferenceManager

    @Binds
    fun bindsCertificateManager(
        certificateManagerImpl: CertificateManagerImpl
    ) : CertificateManager

    @Binds
    @AppScope
    fun bindsSecureStorage(
        secureStorageImpl: SecureStorageImpl
    ) : SecureStorage

    @Binds
    fun bindsDeviceRepository(
        deviceRepositoryImpl: WifiDeviceRepositoryImpl
    ) : WifiDeviceRepository

    @Binds
    fun bindsPortalApi(
        portalApiImpl: WifiPortalApiImpl
    ) : WifiPortalApi

    @Binds
    fun bindsDeviceUnlockManager(
        deviceUnlockManagerImpl: DeviceUnlockManagerImpl
    ) : DeviceUnlockManager

    @Binds
    fun bindsDeviceSettingsRepository(
        deviceSettingsRepositoryImpl: DeviceSettingsRepositoryImpl
    ) : DeviceSettingsRepository

    @Binds
    @AppScope
    fun bindsUnlockServiceManager(
        unlockServiceManagerImpl: UnlockServiceManagerImpl
    ) : UnlockServiceManager

    @Binds
    @AppScope
    fun bindsAwaitUnlockRequestManager(
        awaitUnlockRequestManagerImpl: AwaitUnlockRequestManagerImpl
    ) : AwaitUnlockRequestManager

    @Binds
    @AppScope
    fun bindsUnlockRequestHandlerImpl(
        unlockRequestHandlerImpl: UnlockRequestHandlerImpl
    ) : UnlockRequestHandler

    @Binds
    @AppScope
    fun bindsMdnsManager(
        mdnsManagerImpl: MdnsManagerImpl
    ) : MdnsManager

    @Binds
    @AppScope
    fun bindsBiometricRequestController(biometricRequestManager: BiometricRequestManager) : BiometricRequestController

    @Binds
    @AppScope
    fun bindsBiometricActivityResultReceiver(biometricRequestManager: BiometricRequestManager) : BiometricActivityResultReceiver

    @Binds
    fun bindsBiometricDialogController(
        biometricDialogControllerImpl: BiometricDialogControllerImpl
    ) : BiometricDialogController

    @Binds
    fun bindsUnlockScreenManager(
        unlockScreenManagerImpl: UnlockScreenManagerImpl
    ) : UnlockScreenManager

    @Binds
    fun bindsShortcutManager(
        shortcutManagerImpl: ShortcutManagerImpl
    ) : ShortcutManager

    @Binds
    fun bindsShortcutRepository(
        shortcutRepositoryImpl: ShortcutRepositoryImpl
    ) : ShortcutRepository

    @Binds
    fun bindsQRScannerManager(
        qrScannerManagerImpl: QRScannerManagerImpl
    ) : QRScannerManager

    @Binds
    fun bindsPermissionManager(
        permissionManagerImpl: PermissionManagerImpl
    ) : PermissionManager

    @Binds
    fun bindsBluetoothManager(
        bluetoothManagerImpl: BluetoothManagerImpl
    ) : BluetoothManager

    @Binds
    fun bindsBluetoothPortalApi(
        bluetoothPortalApi: BluetoothPortalApiImpl
    ) : BluetoothPortalApi
}