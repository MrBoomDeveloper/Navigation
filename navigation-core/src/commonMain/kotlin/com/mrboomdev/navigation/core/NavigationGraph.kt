@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.core

import androidx.compose.runtime.*
import kotlin.reflect.*

class NavigationGraph<T: Any> internal constructor(
    @property:InternalNavigationApi val routes: MutableList<Pair<KClass<*>, @Composable (RouteScope.(Any) -> Unit)>>
) {
    internal var isClosed = false
    
    fun graph(
        graph: NavigationGraph<T>
    ) {
        if(isClosed) {
            throw IllegalStateException("Navigation graph cannot be modified after it's creation!")
        }
        
        routes += graph.routes
    }
    
    fun route(
        clazz: KClass<T>, 
        content: @Composable RouteScope.(T) -> Unit
    ) {
        if(isClosed) {
            throw IllegalStateException("Navigation graph cannot be modified after it's creation!")
        }
        
        @Suppress("UNCHECKED_CAST")
        routes += (clazz to content) as Pair<KClass<*>, @Composable RouteScope.(Any) -> Unit>
    }
    
    inline fun <reified R: T> route(
        noinline content: @Composable RouteScope.(R) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        routes += (R::class to content) as Pair<KClass<*>, @Composable RouteScope.(Any) -> Unit>
    }
}

fun <T: Any> navigationGraph(
    scope: NavigationGraph<T>.() -> Unit
) = NavigationGraph<T>(mutableListOf()).apply { scope() }