package com.xxmrk888ytxx.coreandroid.uiText

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

@Stable
sealed class UiText {
    data class StringValue(val value: String) : UiText()
    data class ResourceValue(@param:StringRes val resId: Int) : UiText()
    data class Builder(
        val onBuild: (UiTextProvider) -> String
    ) : UiText()
}

@JvmName("stringUiText")
fun uiText(stringValue: String): UiText = UiText.StringValue(stringValue)

@JvmName("resIdUiText")
fun uiText(@StringRes resId: Int): UiText = UiText.ResourceValue(resId)

@JvmName("resIdUiTextExt")
fun @receiver:StringRes Int.uiText(): UiText = UiText.ResourceValue(this)

@JvmName("stringUiTextExt")
fun String.uiText(): UiText = UiText.StringValue(this)
fun buildUiText(onBuild: (UiTextProvider) -> String) = UiText.Builder(onBuild)

interface UiTextProvider {
    fun provide(uiText: UiText): String
}

fun UiText.asString(context: Context): String = when (this) {
    is UiText.ResourceValue -> context.getString(resId)
    is UiText.StringValue -> value
    is UiText.Builder -> onBuild(
        object : UiTextProvider {
            override fun provide(uiText: UiText) = when (uiText) {
                is UiText.Builder -> uiText.asString(context)
                is UiText.ResourceValue -> context.getString(uiText.resId)
                is UiText.StringValue -> uiText.value
            }
            }
    )
}