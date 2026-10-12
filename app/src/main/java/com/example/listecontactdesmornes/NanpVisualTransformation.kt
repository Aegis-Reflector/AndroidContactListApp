package com.example.listecontactdesmornes

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation



class NanpVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text

        var out = if (trimmed.isNotEmpty()) "(" else ""

        for (i in trimmed.indices) {
            if (i == 3) out += ") "
            if (i == 6) out += "-"
            out += trimmed[i]
        }
        return TransformedText(AnnotatedString(out), phoneNumberOffsetTranslator)
    }

        private val phoneNumberOffsetTranslator = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                // Le texte affiché est limité à 10 chiffres, donc on limite aussi l'offset
                val o = offset.coerceAtMost(10)
                return when (o) {
                    0 -> o

                    in 1..3 -> o + 1

                    in 4..6 -> o + 3

                    else -> o + 4
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val o = when (offset) {
                    0 -> offset
                    in 1..5 -> offset - 1
                    in 6..10 -> offset - 3
                    else -> offset - 4
                }
                // Ne jamais dépasser 10 chiffres
                return o.coerceIn(0, 10)
            }
        }
    }
