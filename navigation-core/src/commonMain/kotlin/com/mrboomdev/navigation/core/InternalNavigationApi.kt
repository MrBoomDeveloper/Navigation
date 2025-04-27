package com.mrboomdev.navigation.core

@RequiresOptIn(
    message = "This api is not intended for regular app usage. " +
            "It may change or be removed at any time!", 
    level = RequiresOptIn.Level.ERROR)
annotation class InternalNavigationApi