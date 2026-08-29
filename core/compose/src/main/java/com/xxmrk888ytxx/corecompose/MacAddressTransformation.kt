package com.xxmrk888ytxx.corecompose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MacAddressTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val out = StringBuilder()
        val cleanText = text.text.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }

        for (i in cleanText.indices) {
            out.append(cleanText[i].uppercaseChar())
            if (i % 2 == 1 && i < 11 && i != cleanText.lastIndex) {
                out.append(":")
            }
        }

        val macOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset
                val colons = (offset - 1) / 2
                return (offset + colons).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return offset
                val colons = offset / 3
                return (offset - colons).coerceAtMost(cleanText.length)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), macOffsetMapping)
    }
}