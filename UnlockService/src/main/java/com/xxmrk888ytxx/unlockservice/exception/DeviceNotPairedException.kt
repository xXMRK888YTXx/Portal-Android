package com.xxmrk888ytxx.unlockservice.exception

class DeviceNotPairedException(val macAddress: String) : UnlockServiceException("Device $macAddress not paired")