package com.mrboomdev.navigation.core

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

class NavigationGraph internal constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    val routes: MutableList<Pair<KClass<*>, @Composable ((Any) -> Unit)>>
) {
    fun <T: Any> route(
        clazz: KClass<T>, 
        content: @Composable (T) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        routes += (clazz to content) as Pair<KClass<*>, @Composable (Any) -> Unit>
    }

    inline fun <reified T: Any> route(
        noinline content: @Composable (T) -> Unit
    ) = route(T::class, content)
}

fun navigationGraph(
    scope: NavigationGraph.() -> Unit
) = NavigationGraph(mutableListOf()).apply { scope() }

operator fun NavigationGraph.plus(
    graph: NavigationGraph
) = NavigationGraph(this.routes.toMutableList().apply { addAll(graph.routes) })