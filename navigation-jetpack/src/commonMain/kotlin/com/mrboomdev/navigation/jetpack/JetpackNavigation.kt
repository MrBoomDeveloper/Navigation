package com.mrboomdev.navigation.jetpack

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.mrboomdev.navigation.core.LocalNavigation
import com.mrboomdev.navigation.core.Navigation
import com.mrboomdev.navigation.core.NavigationGraph
import com.mrboomdev.navigation.core.navigationGraph
import kotlin.reflect.KClass

@Composable
@PublishedApi
internal fun <T: Any> JetpackNavigation(
    modifier: Modifier = Modifier,
    state: NavHostController = rememberNavController(),
    initialRoute: T,
    type: KClass<T>,
    graph: NavigationGraph<T>,
    
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },
    
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) }
) {
    val currentNav = LocalNavigation.current

    val navigation = remember(currentNav, state) {
        JetpackNavigationImpl<T>(currentNav, type, state)
    }
    
    val jetpackGraph = remember(currentNav, state, initialRoute) {
        state.createGraph(routeOf(initialRoute)) {
            for((routeClass, routeContent) in graph.routes) {
                composable(
                    route = buildString {
                        append(routeClass.qualifiedName)
                        append("/{data}")
                    },

                    arguments = listOf(navArgument("data") {
                        this.type = NavType.StringType
                    })
                ) { entry ->
                    routeContent(fromRouteData(routeClass,
                        entry.arguments?.read { getString("data") }!!))
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    CompositionLocalProvider(
        LocalNavigation provides (navigation as Navigation<Any>?)
    ) {
        NavHost(
            modifier = modifier,
            navController = state,
            enterTransition = enterTransition,
            popEnterTransition = enterTransition,
            exitTransition = exitTransition,
            popExitTransition = exitTransition,
            graph = jetpackGraph
        )
    }
}

@Composable
inline fun <reified T: Any> JetpackNavigation(
    modifier: Modifier = Modifier,
    state: NavHostController = rememberNavController(),
    initialRoute: T,

    noinline enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },

    noinline exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) },

    noinline scope: NavigationGraph<T>.() -> Unit
) = JetpackNavigation(
    modifier = modifier,
    state = state,
    initialRoute = initialRoute,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    type = T::class,
    graph = remember(scope) { navigationGraph(scope) }
)