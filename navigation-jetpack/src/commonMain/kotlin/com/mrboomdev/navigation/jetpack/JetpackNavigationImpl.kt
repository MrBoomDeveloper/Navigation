package com.mrboomdev.navigation.jetpack

import androidx.navigation.NavHostController
import com.mrboomdev.navigation.core.Navigation

internal class JetpackNavigationImpl(
    override val parent: Navigation?,
    val navController: NavHostController
): Navigation {
    override fun push(destination: Any) {
        navController.navigate(routeOf(destination))
    }

    override fun pop(destination: Any?): Boolean {
        return if(destination == null) {
            navController.popBackStack()
        } else {
            navController.popBackStack(routeOf(destination), true)
        }
    }
}