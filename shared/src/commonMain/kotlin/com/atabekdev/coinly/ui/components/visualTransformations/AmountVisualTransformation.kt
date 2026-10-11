package com.atabekdev.coinly.ui.components.visualTransformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Shows raw digits with spaces between thousands: "300000" → "300 000".
class AmountVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (hasSeparatorBefore(index, digits.length)) append(SEPARATOR)
                append(char)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + (0 until offset).count { hasSeparatorBefore(it, digits.length) }

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count { it != SEPARATOR }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun hasSeparatorBefore(index: Int, length: Int) = index > 0 && (length - index) % 3 == 0

    private companion object {
        const val SEPARATOR = ' '
    }
}
