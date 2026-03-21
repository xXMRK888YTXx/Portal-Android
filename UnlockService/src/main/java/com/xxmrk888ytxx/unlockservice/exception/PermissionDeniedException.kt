package com.xxmrk888ytxx.unlockservice.exception

class PermissionDeniedException(val deniedPermissions: List<String>): UnlockServiceException("Service can't work without permission: ${deniedPermissions.joinToString(",")}")