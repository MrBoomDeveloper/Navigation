package com.mrboomdev.navigation.sample

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import com.mrboomdev.navigation.core.*
import com.mrboomdev.navigation.jetpack.*
import kotlinx.serialization.*

val AppNavigation = TypeSafeNavigation(Routes::class)

@Composable
fun App() {
    JetpackNavigation<Routes>(
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
        }
    ) {
        route<Routes.ScreenA> { ScreenA() }
        route<Routes.ScreenB> { ScreenB(it.value) }
        route<Routes.ScreenC> { ScreenC(resulter!!) }
    }
}

sealed interface Routes {
    @Serializable
    data object ScreenA: Routes
    
    @Serializable
    data class ScreenB(val value: String): Routes
    
    @Serializable
    data object ScreenC: Routes {
        val resultContract = ResultContract<ScreenC, String>()
    }
}

@Composable
fun ScreenA() {
    var text by rememberSaveable { mutableStateOf("") }
    val navigation = AppNavigation.current()

    Column {
        Text("Screen A")
        
        TextField(
            value = text,
            onValueChange = { text = it }
        )
        
        Button({ navigation.push(Routes.ScreenB(text)) }) { 
            Text("Go to Screen B")
        }
        
        Button({ navigation.pop() }) {
            Text("Try going back")
        }

        Button({ 
            navigation.clear()
            navigation.push(Routes.ScreenB(text))
        }) {
            Text("Clear and go to Screen B")
        }
    }
}

@Composable
fun ScreenB(value: String) {
    val navigation = AppNavigation.current()
    var resultSaved by rememberSaveable { mutableStateOf("") }

    NavigationResult(Routes.ScreenC.resultContract) { 
        resultSaved = it
    }
    
    Column {
        Text("Screen B")
        Text("Value = $value")
        Text("Result from Screen C = $resultSaved")
        
        Button({ navigation.pop() }) {
            Text("Back")
        }
        
        Button({ navigation.pushForResult(Routes.ScreenC) }) {
            Text("Go to Screen C for a result")
        }
    }
}

@Composable
fun ScreenC(resulter: Resulter) {
    var text by rememberSaveable { mutableStateOf("") }
    val navigation = AppNavigation.current()

    Column {
        Text("Screen C")

        TextField(
            value = text,
            onValueChange = { 
                text = it
                resulter(it)
            }
        )

        Button({ navigation.pop() }) {
            Text("Back")
        }
    }
}