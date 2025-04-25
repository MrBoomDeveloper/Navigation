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
import com.mrboomdev.navigation.core.NavigationGraph
import com.mrboomdev.navigation.core.navigationGraph

/**
 * Useful for multi-module applications.
 */
@Composable
fun JetpackNavigation(
    modifier: Modifier = Modifier,
    state: NavHostController = rememberNavController(),
    initialRoute: Any,
    graph: NavigationGraph,
    
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },
    
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) }
) {
    val currentNav = LocalNavigation.current

    val navigation = remember(currentNav, state) {
        JetpackNavigationImpl(currentNav, state)
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
                        type = NavType.StringType
                    })
                ) { entry ->
                    routeContent(fromRouteData(routeClass,
                        entry.arguments?.read { getString("data") }!!))
                }
            }
        }
    }
    
    CompositionLocalProvider(
        LocalNavigation provides navigation
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

/**
 * Useful for single-module applications.
 */
@Composable
fun JetpackNavigation(
    modifier: Modifier = Modifier,
    state: NavHostController = rememberNavController(),
    initialRoute: Any,
    
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },

    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) },
    
    scope: NavigationGraph.() -> Unit
) = JetpackNavigation(
    modifier = modifier,
    state = state,
    initialRoute = initialRoute,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    graph = remember(scope) { navigationGraph(scope) }
)