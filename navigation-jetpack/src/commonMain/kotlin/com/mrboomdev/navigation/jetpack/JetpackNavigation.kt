package com.mrboomdev.navigation.jetpack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import com.mrboomdev.navigation.core.InternalNavigationApi
import com.mrboomdev.navigation.core.Navigation
import com.mrboomdev.navigation.core.currentNavigationOrNull
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

/**
 * @see rememberJetpackNavigation
 */
class JetpackNavigation<T: Any> @PublishedApi internal constructor(
    override val type: KClass<T>,
    override val parent: Navigation<*>?,
    val navController: NavHostController,
    val initialRoute: T
): Navigation<T> {
    override val currentDestination = navController.currentBackStackEntryFlow.map {
        @Suppress("UNCHECKED_CAST")
        fromRouteData(
            entryToClass(it) as KClass<T>, 
            it.arguments!!.read { getString("data") }
        )
    }

    override val currentBackStack = navController.currentBackStack.map { backStack ->
        backStack.mapNotNull { entry ->
            if(entry.destination.route == null) {
                return@mapNotNull null
            }
            
            @Suppress("UNCHECKED_CAST")
            fromRouteData(
                entryToClass(entry) as KClass<T>, 
                entry.arguments!!.read { getString("data") }
            )
        }
    }

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

@Composable
inline fun <reified T: Any> rememberJetpackNavigation(
    initialRoute: T
): JetpackNavigation<T> {
    @OptIn(InternalNavigationApi::class)
    val parent = currentNavigationOrNull()
    val navController = rememberNavController()

    return remember(T::class, parent, navController, initialRoute) {
        JetpackNavigation(T::class, parent, navController, initialRoute)
    }
}