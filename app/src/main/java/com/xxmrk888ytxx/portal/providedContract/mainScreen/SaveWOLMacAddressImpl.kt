package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SaveWOLMacAddress
import javax.inject.Inject

class SaveWOLMacAddressImpl @Inject constructor() : SaveWOLMacAddress {
    override suspend fun save(deviceId: String, macAddress: String) {

    }
}