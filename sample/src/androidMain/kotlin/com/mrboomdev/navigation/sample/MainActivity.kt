package com.mrboomdev.navigation.sample

import android.content.res.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme(
                colorScheme = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDarkTheme -> dynamicDarkColorScheme(this)
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(this)
                    isDarkTheme -> darkColorScheme()
                    else -> lightColorScheme()
                }
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides Color.White
                ) {
                    App()
                }
            }
        }
    }
    
    private val isDarkTheme get() = (
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    ) == Configuration.UI_MODE_NIGHT_YES
}