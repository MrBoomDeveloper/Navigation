@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.jetpack

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.savedstate.*
import com.mrboomdev.navigation.core.*
import com.mrboomdev.navigation.core.Navigation
import kotlinx.serialization.json.*
import java.net.*
import kotlin.reflect.*

class JetpackNavigationState internal constructor(internal val navController: NavHostController)

@Composable
fun rememberJetpackNavigationState(): JetpackNavigationState {
    val navController = rememberNavController()
    return remember(navController) { JetpackNavigationState(navController) }
}

@Composable
@PublishedApi
internal fun <T: Any> JetpackNavigation(
    modifier: Modifier = Modifier,
    state: JetpackNavigationState = rememberJetpackNavigationState(),
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
        JetpackNavigationImpl<T>(currentNav, type, state.navController)
    }

    @Suppress("UNCHECKED_CAST")
    CompositionLocalProvider(
        LocalNavigation provides (navigation as Navigation<Any>?)
    ) {
        NavHost(
            modifier = modifier,
            navController = state.navController,
            enterTransition = enterTransition,
            popEnterTransition = enterTransition,
            exitTransition = exitTransition,
            popExitTransition = exitTransition,
            
            startDestination = routeOf(
                destination = initialRoute,
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
                            this.type = NavType.StringType
                        },
                        
                        navArgument("resultContract") { 
                            this.type = NavType.StringType
                            this.nullable = true
                        },

                        navArgument("resultKey") {
                            this.type = NavType.StringType
                            this.nullable = true
                        }
                    )
                ) { entry ->
                    val data = remember(entry) {
                        fromRouteData(routeClass, entry.arguments?.read { getString("data") }!!) 
                    }
                    
                    val resulter = remember(entry) {
                        val resultKey = entry.arguments?.read { getStringOrNull("resultKey") }
                        val prevEntry = state.navController.previousBackStackEntry

                        // ResultContract<String, String> is being used because serialization
                        // framework requires all parameters to be serializable. In this particular class
                        // they do nothing so we may use any serializable type 
                        // just so that this compiler could shut the fuck up :)
                        val contract = entry.arguments?.read {
                            getStringOrNull("resultContract")
                        }?.let { Json.decodeFromString<ResultContract<String, String>>(
                            URLDecoder.decode(it, "UTF-8")
                        ) }

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
inline fun <reified T: Any> JetpackNavigation(
    modifier: Modifier = Modifier,
    state: JetpackNavigationState = rememberJetpackNavigationState(),
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