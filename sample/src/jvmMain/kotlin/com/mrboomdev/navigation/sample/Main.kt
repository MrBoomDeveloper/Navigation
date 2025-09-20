package com.mrboomdev.navigation.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Desktop navigation sample"
    ) {
        MaterialTheme(
            colorScheme = when {
                isSystemInDarkTheme() -> darkColorScheme()
                else -> lightColorScheme()
            }
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground
            ) {
                App()
            }
        }
    }
}