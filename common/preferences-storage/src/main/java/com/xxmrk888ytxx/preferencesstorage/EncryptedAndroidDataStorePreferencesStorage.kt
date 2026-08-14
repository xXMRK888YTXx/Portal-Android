package com.xxmrk888ytxx.preferencesstorage

import androidx.datastore.preferences.core.Preferences
import com.xxmrk888ytxx.preferencesstorage.utils.Base64Converter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class EncryptedAndroidDataStorePreferencesStorage(
    private val preferencesStorage: PreferencesStorage,
    private val cipher: Cipher
) : EncryptedPreferencesStorage() {

    private val base64Converter by lazy {
        Base64Converter()
    }

    override suspend fun writeProperty(key: Preferences.Key<String>, value: ByteArray) = withContext(Dispatchers.IO) {
        val encryptedValue = cipher.encrypt(value)
        val base64String = base64Converter.bytesToString(encryptedValue)

        preferencesStorage.writeProperty(key,base64String)
    }
    override fun getPropertyOrNull(key: Preferences.Key<String>): Flow<ByteArray?> {
        return preferencesStorage.getPropertyOrNull(key).map { data ->
            if(data == null) return@map data

            try {
                val encryptedBytes = base64Converter.stringToBytes(data)

                cipher.decrypt(encryptedBytes)
            }catch (e:Exception) {
                return@map null
            }
        }
    }

    override suspend fun removeProperty(key: Preferences.Key<String>) = withContext(Dispatchers.IO) {
        preferencesStorage.removeProperty(key)
    }

    override fun isPropertyExist(key: Preferences.Key<String>): Flow<Boolean> {
        return preferencesStorage.isPropertyExist(key)
    }

}