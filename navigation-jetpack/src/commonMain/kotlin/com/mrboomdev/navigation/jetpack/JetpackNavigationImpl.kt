package com.mrboomdev.navigation.jetpack

import androidx.navigation.NavHostController
import com.mrboomdev.navigation.core.Navigation
import kotlin.reflect.KClass

internal class JetpackNavigationImpl<T: Any>(
    override val parent: Navigation<*>?,
    override val type: KClass<T>,
    val navController: NavHostController
): Navigation<T> {
    override fun push(destination: T) {
        navController.navigate(routeOf(destination))
    }

    override fun pop(destination: T?): Boolean {
        return if(destination == null) {
            navController.popBackStack()
        } else {
            navController.popBackStack(routeOf(destination), true)
        }
    }

    override val canPop: Boolean
        get() = navController.previousBackStackEntry != null
}