package com.mrboomdev.navigation.jetpack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mrboomdev.navigation.core.InternalNavigationApi
import com.mrboomdev.navigation.core.Navigation
import com.mrboomdev.navigation.core.currentNavigationOrNull
import kotlin.reflect.KClass

/**
 * @see rememberJetpackNavigation
 */
class JetpackNavigation<T: Any> internal constructor(
    override val type: KClass<T>,
    override val parent: Navigation<*>?,
    val navController: NavHostController,
    val initialRoute: T
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

@OptIn(InternalNavigationApi::class)
@Composable
@PublishedApi
internal fun <T: Any> rememberJetpackNavigationImpl(
    type: KClass<T>,
    initialRoute: T
): JetpackNavigation<T> {
    val parent = currentNavigationOrNull()
    val navController = rememberNavController()

    return remember(type, parent, navController, initialRoute) {
        JetpackNavigation(type, parent, navController, initialRoute)
    }
}

@Composable
inline fun <reified T: Any> rememberJetpackNavigation(
    initialRoute: T
) = rememberJetpackNavigationImpl(T::class, initialRoute)