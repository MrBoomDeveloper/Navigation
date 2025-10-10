package com.mrboomdev.navigation.jetpack

import androidx.navigation.*
import com.mrboomdev.navigation.core.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal class ResulterImpl(
    override val contract: ResultContract<*, *>,
    private val key: String,
    private val navEntry: NavBackStackEntry
) : Resulter {
    @OptIn(InternalSerializationApi::class)
    override fun invoke(result: Any) {
        @Suppress("UNCHECKED_CAST")
        val serializer = result::class.serializer() as KSerializer<Any>
        navEntry.savedStateHandle[key] = Json.encodeToString(serializer, result)
    }
}