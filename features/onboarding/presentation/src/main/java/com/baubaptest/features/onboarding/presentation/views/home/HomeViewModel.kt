package com.baubaptest.features.onboarding.presentation.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baubaptest.core.model.UiState
import com.baubaptest.core.model.User
import com.baubaptest.core.model.asUiState
import com.baubaptest.features.onboarding.domain.usecase.LogoutUseCase
import com.baubaptest.features.onboarding.domain.usecase.ObserveLoggedInUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeUserUseCase: ObserveLoggedInUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Empty)

    val loggedUserState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        observeUserUseCase()
            .onEach { result ->
                _loginState.value = result.asUiState()
            }
            .launchIn(viewModelScope)
    }

    fun closeSession(onLogout: () -> Unit) {
        viewModelScope.launch() {
            logoutUseCase().collect { result ->
                when {
                    result.isSuccess -> {
                        onLogout()
                    }

                    result.isFailure -> {
                        _loginState.value =
                            UiState.Error(
                                result.exceptionOrNull()?.message ?: "No se pudo cerrar la sesión"
                            )
                    }
                }
            }

        }

    }

}