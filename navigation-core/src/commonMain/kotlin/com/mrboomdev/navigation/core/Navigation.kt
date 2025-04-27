package com.mrboomdev.navigation.core

import androidx.annotation.*
import androidx.compose.runtime.*
import kotlin.reflect.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
val LocalNavigation = staticCompositionLocalOf<Navigation<Any>?> { null }

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun currentNavigation(): Navigation<*> = LocalNavigation.current
    ?: throw IllegalStateException("No navigation components were declared!")

@Composable
fun rootNavigation(): Navigation<*> {
    var current = currentNavigation()
    
    while(true) {
        current = current.parent ?: break
    }
    
    return current
}

interface Navigation<T: Any> {
    val type: KClass<T>
    val parent: Navigation<*>?
    fun push(destination: T)
    fun pop(): Boolean
    val canPop: Boolean

    fun clear() {
        @Suppress("ControlFlowWithEmptyBody")
        while(pop()) {}
    }
}

fun <T: Any> Navigation<T>.replace(destination: T) {
    pop()
    push(destination)
}