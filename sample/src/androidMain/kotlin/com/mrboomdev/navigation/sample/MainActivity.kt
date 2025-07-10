package com.mrboomdev.navigation.sample

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val initialRoute = handleDeeplink {
//            data {
//                scheme("https") {
//                    host("mrboomdev.ru") {
//                        ""
//                    }
//
//                    ""
//                }
//            }
//
//            orElse {
//                ""
//            }
//        }
        
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