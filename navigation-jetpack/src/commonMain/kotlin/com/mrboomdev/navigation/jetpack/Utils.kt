package com.mrboomdev.navigation.jetpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass

@OptIn(ExperimentalEncodingApi::class, ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal fun routeOf(
    destination: Any
) = buildString {
    append(destination::class.qualifiedName)
    append("/")
        
    append(Json.encodeToString(
        destination::class.serializer() as KSerializer<Any>, destination
    ).let { URLEncoder.encode(it, "UTF-8") })
}

@OptIn(ExperimentalEncodingApi::class, InternalSerializationApi::class)
internal fun <T: Any> fromRouteData(
    clazz: KClass<T>,
    data: String
): T {
    @Suppress("UNCHECKED_CAST")
    return Json.decodeFromString(clazz.serializer(), URLDecoder.decode(data, "UTF-8"))
}