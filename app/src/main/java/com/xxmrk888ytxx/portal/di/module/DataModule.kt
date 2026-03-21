package com.xxmrk888ytxx.portal.di.module

import android.content.Context
import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.biometricauthentication.create
import com.xxmrk888ytxx.database.PortalDataBase
import com.xxmrk888ytxx.database.dao.BluetoothDeviceDao
import com.xxmrk888ytxx.database.dao.WifiDeviceDao
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockMessage
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Module
interface DataModule {
    companion object {
        const val PREFERENCES_NAME_FILE = "preferences_storage"

        @Provides
        @AppScope
        fun providePreferencesStorage(context: Context): PreferencesStorage =
            PreferencesStorage.Factory().create(PREFERENCES_NAME_FILE, context)

        @Provides
        @AppScope
        fun providesPortalDatabase(context: Context): PortalDataBase =
            PortalDataBase.createDatabase(context)

        @Provides
        fun providesDeviceDao(portalDataBase: PortalDataBase) = portalDataBase.deviceDao

        @Provides
        fun providesDeviceSettingsDao(portalDataBase: PortalDataBase) =
            portalDataBase.deviceSettingsDao

        @Provides
        fun providesShortcutDao(portalDataBase: PortalDataBase) = portalDataBase.shortcutDao

        @Provides
        fun providesBiometricAuthManager(context: Context) =
            BiometricAuthManager.create(context)

        @Provides
        @AppScope
        fun provideJson() = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                polymorphic(WifiRemoteUnlockMessage::class) {
                    subclass(WifiRemoteUnlockMessage.ApproveUnlockWifi::class)
                    subclass(WifiRemoteUnlockMessage.RejectUnlockWifi::class)
                }
            }
        }

        @Provides
        fun providesWifiDeviceDao(portalDataBase: PortalDataBase): WifiDeviceDao = portalDataBase.wifiDeviceDao

        @Provides
        fun providesBluetoothDeviceDao(portalDataBase: PortalDataBase): BluetoothDeviceDao = portalDataBase.bluetoothDeviceDao
    }
}