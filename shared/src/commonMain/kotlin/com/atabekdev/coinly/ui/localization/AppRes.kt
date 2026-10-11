package com.atabekdev.coinly.ui.localization

import androidx.compose.runtime.staticCompositionLocalOf

// Russian is the only language for now; localization is postponed.
val LocalStrings = staticCompositionLocalOf<StringResources> { Russian }

object AppRes {
    val strings: StringResources = Russian
}
