package com.example.projectm.utils.constants

import java.text.NumberFormat
import java.util.Locale

class CurrencyUtils {
    companion object {
        fun Double.toDollarFormat(): String {
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            return format.format(this)
        }
    }
}
