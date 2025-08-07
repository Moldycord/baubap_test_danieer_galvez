package com.baubaptest.features.onboarding.presentation.views.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baubaptest.core.model.UiState
import com.baubaptest.core.model.asUiState
import com.baubaptest.features.onboarding.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        loginUseCase(email, password)
            .onEach { result ->
                _loginState.value = result.asUiState()
            }
            .launchIn(viewModelScope)
    }

}