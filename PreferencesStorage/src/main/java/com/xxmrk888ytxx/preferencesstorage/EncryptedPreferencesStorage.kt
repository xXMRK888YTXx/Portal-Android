package com.xxmrk888ytxx.preferencesstorage

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

abstract class EncryptedPreferencesStorage() {


    /**
     * [Ru]
     * Записывает значение по ключу
     *
     * @param key - Ключ по которому будет записано заначение
     * @param value - Значение которое будет записано
     */

    /**
     * [En]
     * Writes value by key
     *
     * @param key - The key by which the value will be written
     * @param value - Value to be written
     */
    abstract suspend fun writeProperty(key: Preferences.Key<String>, value:ByteArray)


    /**
     * [Ru]
     * Возвращает значение по ключу, если значение не будет найдено или значение не может быть расшифровано возвращает null
     *
     * @param key - Ключ по которому будет записано заначение
     */

    /**
     * [En]
     * Returns a value by key, if the value is not found or value can't be decrypted returns null
     *
     * @param key - The key by which the value will be written
     */
    abstract fun  getPropertyOrNull(key: Preferences.Key<String>) : Flow<ByteArray?>

    /**
     * [Ru]
     * Удаляет значение по ключу.
     *
     * @param key - Ключ, по которому будут удалены данные.
     */

    /**
     * [En]
     * Removes a value by key.
     *
     * @param key - The key by which the data will be deleted.
     */
    abstract suspend fun removeProperty(key: Preferences.Key<String>)

    /**
     * [Ru]
     * Метод для проверки, существуют ли данные по определённому ключу.
     *
     * @param key - ключ для проверки
     */

    /**
     * [En]
     * A method for checking if data exists for a specific key.
     *
     * @param key - key to check
     */
    abstract fun isPropertyExist(key: Preferences.Key<String>) : Flow<Boolean>

    companion object {
        fun create(preferencesStorage: PreferencesStorage,cipher: Cipher) : EncryptedPreferencesStorage {
            return EncryptedAndroidDataStorePreferencesStorage(preferencesStorage, cipher)
        }
    }
}