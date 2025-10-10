@file:Suppress("INVISIBLE_REFERENCE")

package com.mrboomdev.navigation.jetpack

import androidx.compose.runtime.saveable.Saver
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavGraphNavigator
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.mrboomdev.navigation.core.Navigation
import javax.naming.Context
import kotlin.reflect.KClass

inline fun <reified T: Any> JetpackNavigation(
    initialRoute: T,
    parent: Navigation<*>? = null
) = JetpackNavigation(T::class, initialRoute, parent)

fun <T: Any> JetpackNavigation(
    type: KClass<T>,
    initialRoute: T,
    parent: Navigation<*>? = null
): JetpackNavigation<T> = JetpackNavigation(
    type = type,
    parent = parent,
    initialRoute = initialRoute,
    navController = createNavController()
)

inline fun <reified T: Any> JetpackNavigation.Companion.Saver(
    parent: Navigation<*>? = null
) = Saver(T::class, parent)

fun <T: Any> JetpackNavigation.Companion.Saver(
    type: KClass<T>,
    parent: Navigation<*>? = null
): Saver<JetpackNavigation<T>, *> {
    return Saver(
        save = {
            routeOf(it.initialRoute, null, null) to it.navController.saveState()
        },

        restore = { (initialRoute, it) ->
            JetpackNavigation(
                type = type,
                initialRoute = fromRoute(initialRoute),
                parent = parent,
                navController = createNavController().apply {
                    restoreState(it)
                }
            )
        }
    )
}

private fun createNavController(): NavHostController {
    return NavHostController().apply {
        navigatorProvider.addNavigator(ComposeNavGraphNavigator(navigatorProvider))
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
    }
}