package com.xxmrk888ytxx.preferencesstorage

interface Cipher {

    fun encrypt(byteArray: ByteArray) : ByteArray

    fun decrypt(byteArray: ByteArray) : ByteArray
}