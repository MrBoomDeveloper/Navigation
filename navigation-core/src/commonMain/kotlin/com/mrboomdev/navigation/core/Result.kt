@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.core

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.*

/**
 * Used to return an result to the previous route.
 */
interface Resulter {
    /**
     * Can be used to check what result type was requested
     */
    val contract: ResultContract<*, *>
    
    operator fun invoke(result: Any)
}

@Serializable
class ResultContract<Route, Result> @PublishedApi internal constructor(
    private val routeType: String,
    @property:InternalNavigationApi @Transient val resultType: KType? = null,
    private val stringifiedResultType: String
) {
    @InternalNavigationApi
    val resultSerializer by lazy { Json.serializersModule.serializer(resultType 
        ?: throw UnsupportedOperationException("This result contract doesn't support " +
                "all features because it is not intended for such usages.")) }

    override fun toString() = buildString { 
        append("route = ")
        append(routeType)
        append(", result = ")
        append(stringifiedResultType)
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ResultContract<*, *>) return false
        
        if(other.routeType != routeType) {
            return false
        }
        
        if(other.resultType != null && resultType != null) {
            return other.resultType == resultType
        }
        
        return other.stringifiedResultType == stringifiedResultType
    }

    override fun hashCode(): Int {
        var result = routeType.hashCode()
        result = 31 * result + stringifiedResultType.hashCode()
        return result
    }
}

inline fun <reified Route: Any, reified Result: Any> ResultContract() = ResultContract<Route, Result>(
    routeType = typeOf<Route>().toString(),
    resultType = typeOf<Result>(),
    stringifiedResultType = typeOf<Result>().toString()
)

@InternalNavigationApi
fun ResultContract(routeType: String, resultType: String): ResultContract<*, *> = ResultContract<Any, Any>(
    routeType = routeType, 
    resultType = null, 
    stringifiedResultType = resultType
)