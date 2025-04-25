package com.mrboomdev.navigation.core

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

@RestrictTo(RestrictTo.Scope.LIBRARY)
val LocalNavigation = staticCompositionLocalOf<Navigation?> { null }

@Composable
fun currentNavigation() = LocalNavigation.current
    ?: throw IllegalStateException("No navigation components were declared!")

interface Navigation {
    val parent: Navigation?
    fun push(destination: Any)
    fun pop(destination: Any? = null): Boolean
}

fun Navigation.clear() {
    while(true) {
        if(!pop()) break
    }
}

fun Navigation.replace(from: Any, to: Any) {
    pop(from)
    push(to)
}

fun Navigation.replace(destination: Any) {
    pop()
    push(destination)
}

val Navigation.root: Navigation
    get() {
        var current = this
        
        while(true) {
            current = current.parent ?: break
        }
        
        return current
    }