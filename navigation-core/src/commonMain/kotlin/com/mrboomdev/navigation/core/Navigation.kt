@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.core

import androidx.compose.runtime.*
import kotlin.reflect.*

@InternalNavigationApi
val LocalNavigation = staticCompositionLocalOf<Navigation<Any>?> { null }

/**
 * @see rootNavigation
 * @throws IllegalStateException If no navigation components were declared
 */
@Composable
fun currentNavigation(): Navigation<*> = LocalNavigation.current
    ?: throw IllegalStateException("No navigation components were declared!")

/**
 * @see currentNavigation
 * @throws IllegalStateException If no navigation components were declared
 */
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

    /**
     * Navigate to the destination.
     */
    fun push(destination: T)

    /**
     * Remove current destination from the stack. You may see a blank screen 
     * if no destination after stack became empty was pushed.
     * @see safePop
     */
    fun pop(): Boolean

    /**
     * @see pop
     */
    val canPop: Boolean

    /**
     * Clear the stack.
     */
    fun clear() {
        @Suppress("ControlFlowWithEmptyBody")
        while(pop()) {}
    }
}

/**
 * Pops current destination and navigates to the [destination].
 */
fun <T: Any> Navigation<T>.replace(destination: T) {
    pop()
    push(destination)
}

/**
 * Attempts to pop from the latest destination would result into no action.
 * You'll just stay on the same screen. Recommended for back buttons.
 */
fun Navigation<*>.safePop() {
    if(canPop) pop()
}