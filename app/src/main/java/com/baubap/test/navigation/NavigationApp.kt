package com.baubap.test.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baubaptest.features.onboarding.presentation.model.ScreenGraph
import com.baubaptest.features.onboarding.presentation.views.home.HomeScreen
import com.baubaptest.features.onboarding.presentation.views.onboarding.OnboardingScreen
import com.baubaptest.features.onboarding.presentation.views.login.LoginScreen
import com.baubaptest.features.onboarding.presentation.views.register.RegisterScreen
import com.baubaptest.features.onboarding.presentation.views.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ScreenGraph.Splash.route
    ) {
        composable(ScreenGraph.Splash.route) { SplashScreen(navController) }
        composable(ScreenGraph.Onboarding.route) { OnboardingScreen(navController) }
        composable(ScreenGraph.Login.route) { LoginScreen(navController) }
        composable(ScreenGraph.Register.route) { RegisterScreen(navController) }
        composable(ScreenGraph.Home.route) { HomeScreen(navController) }
    }
}