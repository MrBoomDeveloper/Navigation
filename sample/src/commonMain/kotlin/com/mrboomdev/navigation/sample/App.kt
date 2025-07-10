package com.mrboomdev.navigation.sample

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrboomdev.navigation.core.*
import com.mrboomdev.navigation.jetpack.JetpackNavigationHost
import com.mrboomdev.navigation.jetpack.NavigationResult
import com.mrboomdev.navigation.jetpack.pushForResult
import com.mrboomdev.navigation.jetpack.rememberJetpackNavigation
import kotlinx.serialization.Serializable

val AppNavigation = TypeSafeNavigation<Routes>()

@Composable
fun App() {
    val navigation = rememberJetpackNavigation<Routes>(Routes.ScreenA)
    val currentBackStack by navigation.currentBackStack.collectAsState(emptyList())
    val currentDestination by navigation.currentDestination.collectAsState(null)
    
    Column {
        Text(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .fillMaxWidth()
                .padding(8.dp),
            text = "Current backStack = ${currentBackStack.joinToString(", ")}"
        )
        
        Text(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .fillMaxWidth()
                .padding(8.dp),
            text = "Current destination = $currentDestination"
        )
        
        JetpackNavigationHost(
            navigation = navigation,
            
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

            graph = sealedNavigationGraph {
                when(it) {
                    Routes.ScreenA -> ScreenA()
                    is Routes.ScreenB -> ScreenB(it.value)
                    Routes.ScreenC -> ScreenC(resulter!!)
                }
            }
        )
    }
}

@Serializable
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
            Text("navigation.pop()")
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