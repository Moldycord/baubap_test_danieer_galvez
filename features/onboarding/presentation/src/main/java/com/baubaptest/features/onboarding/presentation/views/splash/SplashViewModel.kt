package com.baubaptest.features.onboarding.presentation.views.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.UiState
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val authState: StateFlow<UiState<Boolean>> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            loginRepository.observeUser()
                .map { result ->
                    when (result) {
                        is CustomResult.Success -> UiState.Success(true)
                        is CustomResult.Error -> UiState.Success(false)
                        is CustomResult.Loading -> UiState.Loading
                    }
                }
                .catch {  _authState.value = UiState.Success(false) }
                .collect { _authState.value = it }
        }
    }
}