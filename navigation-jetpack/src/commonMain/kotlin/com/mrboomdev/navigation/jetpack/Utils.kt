@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.jetpack

import com.mrboomdev.navigation.core.InternalNavigationApi
import com.mrboomdev.navigation.core.ResultContract
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass

internal expect fun encodeUri(uri: String): String
internal expect fun decodeUri(uri: String): String

@OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal fun routeOf(
    destination: Any,
    resultContract: ResultContract<*, *>?,
    resultKey: String?
) = buildString {
    append(destination::class.qualifiedName)
    append("?data=")
    
    append(encodeUri(Json.encodeToString(
        destination::class.serializer() as KSerializer<Any>, destination)))
    
    if(resultContract != null) {
        append("&resultContract=")

        // ResultContract<String, String> is being used because serialization
        // framework requires all parameters to be serializable. In this particular class
        // they do nothing so we may use any serializable type 
        // just so that this compiler could shut the fuck up :)
        @Suppress("UNCHECKED_CAST")
        append(encodeUri(Json.encodeToString<ResultContract<String, String>>(
            resultContract as ResultContract<String, String>)))
    }
    
    if(resultKey != null) {
        append("&resultKey=")
        append(resultKey)
    }
}

@OptIn(ExperimentalEncodingApi::class, InternalSerializationApi::class)
internal fun <T: Any> fromRouteData(
    clazz: KClass<T>,
    data: String
) = Json.decodeFromString(clazz.serializer(), decodeUri(data))