@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.core

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

class NavigationGraph<T: Any> internal constructor(
    @property:InternalNavigationApi val routes: MutableList<Pair<KClass<*>, @Composable (RouteScope.(Any) -> Unit)>>
) {
    internal var isClosed = false
    
    fun graph(graph: NavigationGraph<T>) {
        if(isClosed) {
            throw IllegalStateException("Navigation graph cannot be modified after it's creation!")
        }
        
        routes += graph.routes
    }
    
    fun <R: T> route(type: KClass<R>, content: @Composable RouteScope.(R) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        routes += (type to content) as Pair<KClass<*>, @Composable RouteScope.(Any) -> Unit>
    }

    inline fun <reified R: T> route(
        noinline content: @Composable RouteScope.(R) -> Unit
    ) = route(R::class, content)
}

fun <T: Any> navigationGraph(
    scope: NavigationGraph<T>.() -> Unit
) = NavigationGraph<T>(mutableListOf()).apply { scope() }

inline fun <reified T: Any> sealedNavigationGraph(
    noinline content: @Composable RouteScope.(T) -> Unit
): NavigationGraph<T> {
    return navigationGraph { 
        val subtypes: List<KClass<out T>> = T::class.sealedSubclasses
        
        for(subtype in subtypes) {
            route(subtype, content)
        }
    }
}