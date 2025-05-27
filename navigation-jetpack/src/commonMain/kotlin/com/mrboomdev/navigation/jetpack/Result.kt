@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.jetpack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.mrboomdev.navigation.core.InternalNavigationApi
import com.mrboomdev.navigation.core.Navigation
import com.mrboomdev.navigation.core.ResultContract
import com.mrboomdev.navigation.core.currentNavigation
import kotlinx.serialization.json.Json

@Composable
fun <T: Any, A: T, B> Navigation<T>.resultOf(
    contract: ResultContract<A, B>,
    key: String = "result"
): B? {
    if(this !is JetpackNavigation<*>) {
        throw UnsupportedOperationException("This is not an Jetpack Navigation!")
    }
    
    val result = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>(key, null)
        ?.collectAsState()
        ?.value ?: return null
    
    return try {
        @Suppress("UNCHECKED_CAST")
        Json.decodeFromString(contract.resultSerializer, result) as B?
    } catch(_: Throwable) { null }
}

fun <BaseRoute: Any, Route: BaseRoute> Navigation<BaseRoute>.pushForResult(
    contract: ResultContract<out BaseRoute, *>,
    destination: Route,
    key: String = "result"
) {
    if(this !is JetpackNavigation<*>) {
        throw UnsupportedOperationException("This is not an Jetpack Navigation!")
    }
    
    navController.navigate(routeOf(destination, contract, key))
}

fun <BaseRoute: Any, Route: BaseRoute, Result: Any> Navigation<BaseRoute>.setResult(
    contract: ResultContract<Route, Result>,
    result: Result,
    key: String = "result"
) {
    if(this !is JetpackNavigation<*>) {
        throw UnsupportedOperationException("This is not an Jetpack Navigation!")
    }

    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.set<String>(key, Json.encodeToString(contract.resultSerializer, result))
}

@Composable
fun <B> NavigationResult(
    contract: ResultContract<*, B>, 
    key: String = "result", 
    callback: (B) -> Unit
) {
    @Suppress("UNCHECKED_CAST")
    NavigationResult<Any, Any, B>(
        navigation = currentNavigation(),
        contract as ResultContract<Any, B>,
        key,
        callback
    )
}

@Composable
fun <T: Any, A: T, B> NavigationResult(
    navigation: Navigation<T>,
    contract: ResultContract<A, B>,
    key: String = "result",
    callback: (B) -> Unit
) {
    if(navigation !is JetpackNavigation<*>) {
        throw UnsupportedOperationException("This is not an Jetpack Navigation!")
    }
    
    val result = navigation.resultOf(contract, key)

    result?.also {
        navigation.navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>(key)
        
        callback(it)
    }
}