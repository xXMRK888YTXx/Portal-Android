package com.xxmrk888ytxx.unlockservice.exception

class InvalidClientIdException(clientId: String) : UnlockServiceException(
    "Invalid client id: $clientId"
)