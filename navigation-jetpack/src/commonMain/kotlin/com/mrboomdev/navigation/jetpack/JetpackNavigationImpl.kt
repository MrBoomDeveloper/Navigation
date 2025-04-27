package com.mrboomdev.navigation.jetpack

import androidx.navigation.*
import com.mrboomdev.navigation.core.Navigation
import kotlin.reflect.*

internal class JetpackNavigationImpl<T: Any>(
    override val parent: Navigation<*>?,
    override val type: KClass<T>,
    internal val navController: NavHostController
): Navigation<T> {
    override fun push(destination: T) {
        navController.navigate(routeOf(destination, null, null))
    }

    override fun pop(): Boolean {
        return navController.popBackStack()
    }

    override fun clear() {
        @Suppress("ControlFlowWithEmptyBody")
        while(navController.popBackStack()) {}
    }

    override val canPop: Boolean
        get() = navController.previousBackStackEntry != null
}