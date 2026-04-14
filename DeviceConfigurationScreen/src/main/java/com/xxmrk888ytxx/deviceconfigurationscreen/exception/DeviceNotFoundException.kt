package com.xxmrk888ytxx.deviceconfigurationscreen.exception

import com.xxmrk888ytxx.coreandroid.exception.PortalException

class DeviceNotFoundException(val clientId: String) : PortalException("Device with id $clientId not found")