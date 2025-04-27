@file:OptIn(InternalNavigationApi::class)

package com.mrboomdev.navigation.jetpack

import com.mrboomdev.navigation.core.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.*
import kotlin.io.encoding.*
import kotlin.reflect.*

@OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal fun routeOf(
    destination: Any,
    resultContract: ResultContract<*, *>?,
    resultKey: String?
) = buildString {
    append(destination::class.qualifiedName)
    append("?data=")
    
    append(URLEncoder.encode(Json.encodeToString(
        destination::class.serializer() as KSerializer<Any>, destination), "UTF-8"))
    
    if(resultContract != null) {
        append("&resultContract=")

        // ResultContract<String, String> is being used because serialization
        // framework requires all parameters to be serializable. In this particular class
        // they do nothing so we may use any serializable type 
        // just so that this compiler could shut the fuck up :)
        @Suppress("UNCHECKED_CAST")
        append(URLEncoder.encode(Json.encodeToString<ResultContract<String, String>>(
            resultContract as ResultContract<String, String>), "UTF-8"))
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
) = Json.decodeFromString(clazz.serializer(), URLDecoder.decode(data, "UTF-8"))