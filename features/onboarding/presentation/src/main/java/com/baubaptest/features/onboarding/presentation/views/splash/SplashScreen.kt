package com.baubaptest.features.onboarding.presentation.views.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baubaptest.core.model.UiState
import com.baubaptest.features.onboarding.presentation.views.general.Loader
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {

    Loader()
    val state by viewModel.authState.collectAsState()

    when (state) {
        is UiState.Loading -> Loader()

        is UiState.Success -> {
            LaunchedEffect(Unit) {
                delay(2500)

                if ((state as UiState.Success<Boolean>).data) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }

        is UiState.Error -> navController.navigate("onboarding") {
            popUpTo("splash") { inclusive = true }
        }

        else -> navController.navigate("onboarding") {
            popUpTo("splash") { inclusive = true }
        }
    }
}