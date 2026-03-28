package com.xxmrk888ytxx.unlockservice.exception

class DeviceNotPairedException(val macAddress: String) : Exception("Device $macAddress not paired")