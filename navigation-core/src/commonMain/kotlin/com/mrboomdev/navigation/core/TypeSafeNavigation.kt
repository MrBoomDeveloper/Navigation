@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.core

import androidx.compose.runtime.*
import kotlin.reflect.*

class TypeSafeNavigation<T: Any>(private val type: KClass<T>) {
    @Composable
    fun current(): Navigation<T> = currentNavigation(type)

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

@Composable
fun <T: Any> currentNavigation(type: KClass<T>): Navigation<T> {
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
inline fun <reified T: Any> currentNavigation() = currentNavigation(T::class)