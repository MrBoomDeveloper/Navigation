package com.mrboomdev.navigation.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.mrboomdev.navigation.jetpack.JetpackNavigation
import com.mrboomdev.navigation.jetpack.Saver

@Composable
internal actual inline fun <reified T : Any> customRememberJetpackNavigation(initialRoute: T): JetpackNavigation<T> {
    val context = LocalContext.current
    return rememberSaveable(saver = JetpackNavigation.Saver(context)) { JetpackNavigation(context, initialRoute) }
}