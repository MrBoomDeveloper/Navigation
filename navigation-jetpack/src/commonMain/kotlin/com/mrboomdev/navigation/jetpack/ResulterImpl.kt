package com.mrboomdev.navigation.jetpack

import androidx.navigation.*
import com.mrboomdev.navigation.core.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal class ResulterImpl(
    private val key: String,
    private val navEntry: NavBackStackEntry
) : Resulter {
    @OptIn(InternalSerializationApi::class)
    override fun invoke(result: Any) {
        navEntry.savedStateHandle[key] = 
            Json.encodeToString(result::class.serializer() as KSerializer<Any>, result)
    }
}