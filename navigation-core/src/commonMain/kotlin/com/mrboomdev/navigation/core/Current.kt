package com.mrboomdev.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

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
internal fun <T: Any, N: Navigation<T>> currentNavigationSafe(
    navigationType: KClass<N>,
    type: KClass<T>
): N {
    var current = LocalNavigation.current
        ?: throw IllegalStateException("No navigation components were declared!")

    if(type == Any::class) {
        @Suppress("UNCHECKED_CAST")
        return current as N
    }

    while(true) {
        fun crash(): Nothing {
            throw IllegalStateException("No navigation components with required type were declared!")
        }
        
        if(current::class.isSubclassOf(navigationType) && current.type == type) {
            @Suppress("UNCHECKED_CAST") 
            return current as N
        }

        @Suppress("UNCHECKED_CAST")
        current = current.parent as N? ?: crash()
    }
}

@Composable
@PublishedApi
internal fun <T: Any, N: Navigation<T>> rootNavigationSafe(
    navigationType: KClass<N>,
    type: KClass<T>
): N {
    var latestOk: N? = null

    var current = LocalNavigation.current
        ?: throw IllegalStateException("No navigation components were declared!")

    fun crash(): Nothing {
        throw IllegalStateException("No navigation components with required type were declared!")
    }

    while(true) {
        if(current::class.isSubclassOf(navigationType) && current.type == type) {
            @Suppress("UNCHECKED_CAST")
            latestOk = current as N
            continue
        }

        @Suppress("UNCHECKED_CAST")
        current = current.parent as N? ?: break
    }

    return latestOk ?: crash()
}

@Composable
@JvmName("rootNavigationGeneric")
inline fun <reified T: Any, reified N: Navigation<T>> rootNavigation() = rootNavigationSafe(N::class, T::class)

@Composable
@JvmName("currentNavigationGeneric")
inline fun <reified T: Any, reified N: Navigation<T>> currentNavigation() = currentNavigationSafe(N::class, T::class)

inline fun <reified T: Any, reified N: Navigation<T>> TypeSafeNavigation() = TypeSafeNavigation(N::class, T::class)

@Suppress("UNCHECKED_CAST")
@JvmName("TypeSafeNavigationCustom")
inline fun <reified T: Any> TypeSafeNavigation() = TypeSafeNavigation(Navigation::class as KClass<Navigation<T>>, T::class)

class TypeSafeNavigation<T: Any, N: Navigation<T>>(
    private val navigationType: KClass<N>,
    private val type: KClass<T>
) {
    @Composable
    fun current(): N = currentNavigationSafe(navigationType, type)

    @Composable
    fun root(): N = rootNavigationSafe(navigationType, type)
}