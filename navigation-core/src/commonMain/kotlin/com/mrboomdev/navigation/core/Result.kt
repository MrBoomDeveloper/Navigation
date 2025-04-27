package com.mrboomdev.navigation.core

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.*

interface Resulter {
    // KClass isn't serializable
    // Uncomment once a solution for all platforms will be found
    // val type: KClass<*>
    operator fun invoke(result: Any)
}

@Serializable
class ResultContract<Route, Result>(private val result: KType) {
    val resultSerializer by lazy { Json.serializersModule.serializer(result) }
}

inline fun <reified Route: Any, reified Result: Any> ResultContract() = 
    ResultContract<Route, Result>(typeOf<Result>())