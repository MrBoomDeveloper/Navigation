package com.mrboomdev.navigation.jetpack3

import androidx.compose.runtime.toMutableStateList
import com.mrboomdev.navigation.core.Navigation
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class JetpackNavigation<T : Any> internal constructor(
    initialItems: List<T>
) : Navigation<T> {
    internal val state = initialItems.toMutableStateList()

    override val currentDestinationFlow: Flow<T>
        get() = TODO("Not yet implemented")

    override val currentBackStackFlow: Flow<List<T>>
        get() = TODO("Not yet implemented")

    override val currentDestination: T
        get() = TODO("Not yet implemented")

    override val currentBackStack: List<T>
        get() = TODO("Not yet implemented")

    override val type: KClass<T>
        get() = TODO("Not yet implemented")

    override val parent: Navigation<*>?
        get() = TODO("Not yet implemented")

    override fun push(destination: T) {
        TODO("Not yet implemented")
    }

    override fun pop(): Boolean {
        TODO("Not yet implemented")
    }

    override val canPop: Boolean
        get() = TODO("Not yet implemented")
}