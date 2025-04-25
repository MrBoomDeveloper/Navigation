package com.mrboomdev.navigation.sample

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mrboomdev.navigation.core.currentNavigation
import com.mrboomdev.navigation.jetpack.JetpackNavigation
import kotlinx.serialization.Serializable

@Composable
fun App() {
    JetpackNavigation(
        initialRoute = Routes.ScreenA,
        
        enterTransition = {
            fadeIn(tween(500)) +
                    slideInHorizontally(tween(350)) { it / 2 } +
                    scaleIn(tween(250), initialScale = .95f)
        },

        exitTransition = {
            fadeOut(tween(500)) +
                    slideOutHorizontally(tween(350)) +
                    scaleOut(tween(250), targetScale = .95f)
        },
    ) { 
        route<Routes.ScreenA> { ScreenA() }
        route<Routes.ScreenB> { ScreenB(it.value) }
    }
}

sealed interface Routes {
    @Serializable
    data object ScreenA
    
    @Serializable
    data class ScreenB(val value: String)
}

@Composable
fun ScreenA() {
    var text by rememberSaveable { mutableStateOf("") }
    val navigation = currentNavigation()

    Column {
        Text("Screen A")
        
        TextField(
            value = text,
            onValueChange = { text = it }
        )
        
        Button({ navigation.push(Routes.ScreenB(text)) }) { 
            Text("Go to Screen B")
        }
    }
}

@Composable
fun ScreenB(value: String) {
    val navigation = currentNavigation()
    
    Column {
        Text("Screen B")
        Text("Value = $value")
        
        Button({ navigation.pop() }) {
            Text("Back")
        }
    }
}