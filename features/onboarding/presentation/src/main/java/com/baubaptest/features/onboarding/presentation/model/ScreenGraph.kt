package com.baubaptest.features.onboarding.presentation.model

sealed class ScreenGraph(val route: String) {
    data object Splash : ScreenGraph("splash")

    data object Onboarding : ScreenGraph("onboarding")

    data object Login : ScreenGraph("login")

    data object Register : ScreenGraph("register")
}