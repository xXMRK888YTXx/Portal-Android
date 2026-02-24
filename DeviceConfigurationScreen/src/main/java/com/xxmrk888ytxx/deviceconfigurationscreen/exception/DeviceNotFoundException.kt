package com.xxmrk888ytxx.deviceconfigurationscreen.exception

import com.xxmrk888ytxx.coreandroid.exception.PortalException

class DeviceNotFoundException(val deviceId: String) : PortalException("Device with id $deviceId not found")