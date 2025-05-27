package com.mrboomdev.navigation.jetpack

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.mrboomdev.navigation.core.InternalNavigationApi
import com.mrboomdev.navigation.core.NavigationGraph
import com.mrboomdev.navigation.core.ResultContract
import com.mrboomdev.navigation.core.RouteScope
import com.mrboomdev.navigation.core.navigationGraph
import com.mrboomdev.navigation.core.provideCurrentNavigation
import kotlinx.serialization.json.Json

@Composable
@PublishedApi
internal fun <T: Any> JetpackNavigationHostImpl(
    modifier: Modifier,
    navigation: JetpackNavigation<T>,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition,
    graph: NavigationGraph<T>
) {
    @OptIn(InternalNavigationApi::class)
    provideCurrentNavigation(navigation) {
        NavHost(
            modifier = modifier,
            navController = navigation.navController,
            enterTransition = enterTransition,
            popEnterTransition = enterTransition,
            exitTransition = exitTransition,
            popExitTransition = exitTransition,

            startDestination = routeOf(
                destination = navigation.initialRoute,
                resultContract = null,
                resultKey = null
            )
        ) {
            for((routeClass, routeContent) in graph.routes) {
                composable(
                    route = buildString {
                        append(routeClass.qualifiedName)
                        append("?data={data}&resultContract={resultContract}&resultKey={resultKey}")
                    },

                    arguments = listOf(
                        navArgument("data") {
                            type = NavType.StringType
                            nullable = false
                        },

                        navArgument("resultContract") {
                            type = NavType.StringType
                            nullable = true
                        },

                        navArgument("resultKey") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) { entry ->
                    val data = remember(entry) {
                        fromRouteData(routeClass, entry.arguments?.read { getString("data") }!!)
                    }

                    val resulter = remember(entry) {
                        val resultKey = entry.arguments?.read { getStringOrNull("resultKey") }
                        val prevEntry = navigation.navController.previousBackStackEntry

                        // ResultContract<String, String> is being used because serialization
                        // framework requires all parameters to be serializable. In this particular class
                        // they do nothing so we may use any serializable type
                        // just so that this compiler could shut the fuck up :)
                        val contract = entry.arguments?.read {
                            getStringOrNull("resultContract")
                        }?.let { Json.decodeFromString<ResultContract<String, String>>(decodeUri(it)) }

                        if(resultKey != null && prevEntry != null && contract != null) {
                            ResulterImpl(contract, resultKey, prevEntry)
                        } else null
                    }

                    val scope = remember(resulter) {
                        object : RouteScope {
                            override val resulter = resulter
                        }
                    }

                    routeContent(scope, data)
                }
            }
        }
    }
}

@Composable
inline fun <reified T: Any> JetpackNavigationHost(
    modifier: Modifier = Modifier,
    navigation: JetpackNavigation<T>,

    noinline enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },

    noinline exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) },

    noinline scope: NavigationGraph<T>.() -> Unit
) = JetpackNavigationHostImpl(
    modifier = modifier,
    navigation = navigation,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    graph = remember(scope) { navigationGraph(scope) }
)