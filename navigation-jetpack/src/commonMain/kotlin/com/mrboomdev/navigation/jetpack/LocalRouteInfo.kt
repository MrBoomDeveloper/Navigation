package com.mrboomdev.navigation.jetpack

import androidx.compose.runtime.staticCompositionLocalOf
import com.mrboomdev.navigation.core.Resulter

interface RouteInfo {
    val resulter: Resulter?
    val destination: Any
}

val LocalRouteInfo = staticCompositionLocalOf<RouteInfo> {
    throw IllegalStateException("LocalRouteInfo not provided! You are not in the JetpackNavigationHost scope!")
}