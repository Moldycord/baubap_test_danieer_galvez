package com.baubaptest.features.onboarding.presentation.views.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(


) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onFullNameChanged(value: String) = update { it.copy(fullName = value) }
    fun onEmailChanged(value: String) = update { it.copy(email = value) }
    fun onPhoneChanged(value: String) = update { it.copy(phone = value.take(10)) }
    fun onCurpChanged(value: String) = update { it.copy(curp = value.uppercase()) }

    inline fun onRegister(nip: String, onSuccess: () -> Unit) {
        onSuccess()
    }

    private fun update(transform: (RegisterUiState) -> RegisterUiState) {
        _uiState.update(transform)
    }
}

data class RegisterUiState(
    val phone: String = "",
    val curp: String = "",
    val fullName: String = "",
    val email: String = ""
)

sealed interface RegisterStatus {
    object Idle : RegisterStatus
    object Loading : RegisterStatus
    object Success : RegisterStatus
    data class Error(val message: String) : RegisterStatus
}