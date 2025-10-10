package com.mrboomdev.navigation.jetpack

import android.net.Uri
import android.os.Build
import java.net.URLDecoder
import java.net.URLEncoder

internal actual fun encodeUri(uri: String): String =
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLEncoder.encode(uri, Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLEncoder.encode(uri)
    }

internal actual fun decodeUri(uri: String): String =
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLDecoder.decode(uri, Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLDecoder.decode(uri)
    }