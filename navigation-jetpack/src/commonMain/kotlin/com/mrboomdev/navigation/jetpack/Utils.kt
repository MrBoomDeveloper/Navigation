package com.mrboomdev.navigation.jetpack

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.*
import kotlin.io.encoding.*
import kotlin.reflect.*

@OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal fun routeOf(
    destination: Any,
    resultKey: String?
) = buildString {
    append(destination::class.qualifiedName)
    append("?data=")
    
    append(URLEncoder.encode(Json.encodeToString(
        destination::class.serializer() as KSerializer<Any>, destination), "UTF-8"))
    
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