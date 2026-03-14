package com.xxmrk888ytxx.coreandroid.uiText

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class StringValue(val value: String) : UiText()
    data class ResourceValue(@param:StringRes val resId: Int) : UiText()
}

@JvmName("stringUiText")
fun uiText(stringValue: String): UiText = UiText.StringValue(stringValue)
@JvmName("resIdUiText")
fun uiText(@StringRes resId: Int): UiText = UiText.ResourceValue(resId)
@JvmName("resIdUiTextExt")
fun @receiver:StringRes Int.uiText(): UiText = UiText.ResourceValue(this)
@JvmName("stringUiTextExt")
fun String.uiText(): UiText = UiText.StringValue(this)

fun UiText.asString(context: Context): String = when(this) {
    is UiText.ResourceValue -> context.getString(resId)
    is UiText.StringValue -> value
}