@file:Suppress("INVISIBLE_REFERENCE")

package com.mrboomdev.navigation.jetpack

import android.content.Context
import android.net.http.SslCertificate.restoreState
import androidx.compose.runtime.saveable.Saver
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.ComposeNavGraphNavigator
import androidx.navigation.compose.createNavController
import kotlin.reflect.KClass

inline fun <reified T: Any> JetpackNavigation(
    context: Context,
    initialRoute: T,
    parent: com.mrboomdev.navigation.core.Navigation<*>? = null
) = JetpackNavigation(context, T::class, initialRoute, parent)

fun <T: Any> JetpackNavigation(
    context: Context,
    type: KClass<T>,
    initialRoute: T,
    parent: com.mrboomdev.navigation.core.Navigation<*>? = null
): JetpackNavigation<T> = JetpackNavigation(
    type = type,
    parent = parent,
    initialRoute = initialRoute,
    navController = createNavController(context)
)

inline fun <reified T: Any> JetpackNavigation.Companion.Saver(
    context: Context,
    parent: com.mrboomdev.navigation.core.Navigation<*>? = null
) = Saver(context, T::class, parent)

fun <T: Any> JetpackNavigation.Companion.Saver(
    context: Context,
    type: KClass<T>,
    parent: com.mrboomdev.navigation.core.Navigation<*>? = null
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
                navController = createNavController(context).apply {
                    restoreState(it)
                }
            )
        }
    )
}

private fun createNavController(context: Context): NavHostController {
    return NavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavGraphNavigator(navigatorProvider))
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
    }
}