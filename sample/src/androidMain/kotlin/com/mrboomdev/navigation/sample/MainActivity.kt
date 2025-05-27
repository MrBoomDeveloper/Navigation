package com.mrboomdev.navigation.sample

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme(
                colorScheme = when {
                    supportsMaterialYou() && isSystemInDarkTheme() ->
                        dynamicDarkColorScheme(this)

                    supportsMaterialYou() ->
                        dynamicLightColorScheme(this)

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

    private fun supportsMaterialYou() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}