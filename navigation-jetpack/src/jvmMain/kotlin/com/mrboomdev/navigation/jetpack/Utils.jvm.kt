package com.mrboomdev.navigation.jetpack

import java.net.URLDecoder
import java.net.URLEncoder

internal actual fun encodeUri(uri: String): String =
    URLEncoder.encode(uri, Charsets.UTF_8)

internal actual fun decodeUri(uri: String): String =
    URLDecoder.decode(uri, Charsets.UTF_8)