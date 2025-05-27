package com.mrboomdev.navigation.jetpack

import android.net.Uri

internal actual fun encodeUri(uri: String): String =
    Uri.encode(uri)

internal actual fun decodeUri(uri: String): String =
    Uri.decode(uri)