package com.xxmrk888ytxx.database.model

enum class UnlockMethod(val id: Int) {
     AUTOMATIC(0), NOTIFICATION(1), CONFIRMATION_SCREEN(2);

     companion object {
          const val AUTOMATIC_METHOD_ID = 0
     }
}