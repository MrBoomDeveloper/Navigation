package com.mrboomdev.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.reflect.KClass

private val LocalNavigation = staticCompositionLocalOf<Navigation<*>?> { null }

@InternalNavigationApi
@Composable
fun provideCurrentNavigation(
    navigation: Navigation<*>,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    value = (LocalNavigation provides navigation),
    content = content
)

@InternalNavigationApi
@Composable
fun currentNavigationOrNull(): Navigation<Any>? {
    @Suppress("UNCHECKED_CAST")
    return LocalNavigation.current as Navigation<Any>?
}

/**
 * @see rootNavigationSafe
 * @throws IllegalStateException If no navigation components were declared
 */
@Composable
fun currentNavigation(): Navigation<Any> {
    @Suppress("UNCHECKED_CAST")
    return LocalNavigation.current as Navigation<Any>?
        ?: throw IllegalStateException("No navigation components were declared!")
}

/**
 * @see currentNavigationSafe
 * @throws IllegalStateException If no navigation components were declared
 */
@Composable
fun rootNavigation(): Navigation<Any> {
    var current = currentNavigation()

    while(true) {
        @Suppress("UNCHECKED_CAST")
        current = current.parent as Navigation<Any>? ?: break
    }

    return current
}

@Composable
@PublishedApi
internal fun <T: Any> currentNavigationSafe(type: KClass<T>): Navigation<T> {
    var current = LocalNavigation.current
        ?: throw IllegalStateException("No navigation components were declared!")

    if(type == Any::class) {
        @Suppress("UNCHECKED_CAST")
        return current as Navigation<T>
    }

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
@PublishedApi
internal fun <T: Any> rootNavigationSafe(type: KClass<T>): Navigation<T> {
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

@Composable
@JvmName("rootNavigationGeneric")
inline fun <reified T: Any> rootNavigation() = rootNavigationSafe(T::class)

@Composable
@JvmName("currentNavigationGeneric")
inline fun <reified T: Any> currentNavigation() = currentNavigationSafe(T::class)

inline fun <reified T: Any> TypeSafeNavigation() = TypeSafeNavigation(T::class)

class TypeSafeNavigation<T: Any>(private val type: KClass<T>) {
    @Composable
    fun current(): Navigation<T> = currentNavigationSafe(type)

    @Composable
    fun root(): Navigation<T> = rootNavigationSafe(type)
}