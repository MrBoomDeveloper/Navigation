package com.mrboomdev.navigation.jetpack

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.mrboomdev.navigation.core.*
import kotlinx.serialization.json.Json

@Composable
fun <T: Any> JetpackNavigationHost(
    modifier: Modifier = Modifier,
    navigation: JetpackNavigation<T>,
    
    enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = 
        { fadeIn(animationSpec = tween(700)) },
    
    exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = 
        { fadeOut(animationSpec = tween(700)) },
    
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

    noinline enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },

    noinline exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) },

    noinline graph: NavigationGraph<T>.() -> Unit
) = JetpackNavigationHost(
    modifier = modifier,
    navigation = navigation,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    graph = remember(graph) { navigationGraph(graph) }
)