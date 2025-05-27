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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mrboomdev.navigation.core.ResultContract
import com.mrboomdev.navigation.core.Resulter
import com.mrboomdev.navigation.core.TypeSafeNavigation
import com.mrboomdev.navigation.core.safePop
import com.mrboomdev.navigation.jetpack.JetpackNavigationHost
import com.mrboomdev.navigation.jetpack.NavigationResult
import com.mrboomdev.navigation.jetpack.pushForResult
import com.mrboomdev.navigation.jetpack.rememberJetpackNavigation
import kotlinx.serialization.Serializable

val AppNavigation = TypeSafeNavigation<Routes>()

@Composable
fun App() {
    JetpackNavigationHost<Routes>(
        navigation = rememberJetpackNavigation(Routes.ScreenA),
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
        val resultContract2 = ResultContract<ScreenC, Boolean>()
    }
}

@Composable
fun ScreenA() {
    var text by rememberSaveable { mutableStateOf("") }
    var resultSaved by rememberSaveable { mutableStateOf(false) }
    val navigation = AppNavigation.current()

    NavigationResult(Routes.ScreenC.resultContract2) {
        resultSaved = it
    }

    Column {
        Text("Screen A")
        Text("Result from Screen C = $resultSaved")
        
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

        Button({
            navigation.pushForResult(Routes.ScreenC.resultContract2, Routes.ScreenC)
        }) {
            Text("Go to Screen C for a result")
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
        
        Button({ navigation.safePop() }) {
            Text("Back")
        }
        
        Button({ 
            navigation.pushForResult(Routes.ScreenC.resultContract, Routes.ScreenC) 
        }) {
            Text("Go to Screen C for a result")
        }
    }
}

@Composable
fun ScreenC(resulter: Resulter) {
    var text by rememberSaveable { mutableStateOf("") }
    var toggle by remember { mutableStateOf(false) }
    val navigation = AppNavigation.current()
    
    LaunchedEffect(Unit) {
        when(resulter.contract) {
            Routes.ScreenC.resultContract -> resulter("")
            Routes.ScreenC.resultContract2 -> resulter(false)
        }
    }

    Column {
        Text("Screen C")

        when(resulter.contract) {
            Routes.ScreenC.resultContract -> TextField(
                value = text,
                onValueChange = {
                    text = it
                    resulter(it)
                }
            )
            
            Routes.ScreenC.resultContract2 -> Switch(
                checked = toggle,
                onCheckedChange = { 
                    toggle = it 
                    resulter(it)
                }
            )
        }
        
        Button({ navigation.safePop() }) {
            Text("Back")
        }
    }
}