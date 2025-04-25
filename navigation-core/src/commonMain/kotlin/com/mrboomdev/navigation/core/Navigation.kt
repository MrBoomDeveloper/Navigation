package com.mrboomdev.navigation.core

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.reflect.KClass

@RestrictTo(RestrictTo.Scope.LIBRARY)
val LocalNavigation = staticCompositionLocalOf<Navigation<Any>?> { null }

class TypeSafeNavigation<T: Any>(private val type: KClass<T>) {
    @Composable
    fun current(): Navigation<T> {
        var current = LocalNavigation.current 
            ?: throw IllegalStateException("No navigation components were declared!")
        
        while(true) {
            if(current.type == type) {
                @Suppress("UNCHECKED_CAST")
                return current as Navigation<T>
            }

            @Suppress("UNCHECKED_CAST")
            current = current.parent as Navigation<Any>?
                ?: throw IllegalStateException("No navigation components with required type were declared!")
        }
    }
    
    @Composable
    fun root(): Navigation<T> {
        var latestOk: Navigation<T>? = null
        
        var current = LocalNavigation.current
            ?: throw IllegalStateException("No navigation components were declared!")

        while(true) {
            if(current.type == type) {
                @Suppress("UNCHECKED_CAST")
                latestOk = current as Navigation<T>
                continue
            }

            @Suppress("UNCHECKED_CAST")
            current = current.parent as Navigation<Any>? ?: break
        }
        
        return latestOk ?: throw IllegalStateException("No navigation components with required type were declared!")
    }
}

interface Navigation<T: Any> {
    val type: KClass<T>
    val parent: Navigation<*>?
    fun push(destination: T)
    fun pop(destination: T? = null): Boolean
    val canPop: Boolean
}

fun <T: Any> Navigation<T>.clear() {
    while(true) {
        if(!pop()) break
    }
}

fun <T: Any> Navigation<T>.replace(from: T, to: T) {
    pop(from)
    push(to)
}

fun <T: Any> Navigation<T>.replace(destination: T) {
    pop()
    push(destination)
}