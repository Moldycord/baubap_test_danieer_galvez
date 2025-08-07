package com.baubaptest.features.onboarding.presentation.views.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baubaptest.features.onboarding.domain.usecase.RegisterAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _registerStatus = MutableStateFlow<RegisterStatus>(RegisterStatus.Idle)
    val registerStatus: StateFlow<RegisterStatus> = _registerStatus.asStateFlow()

    fun onFullNameChanged(value: String) = update { it.copy(fullName = value) }
    fun onEmailChanged(value: String) = update { it.copy(email = value) }
    fun onPhoneChanged(value: String) = update { it.copy(phone = value.take(10)) }
    fun onCurpChanged(value: String) = update { it.copy(curp = value.uppercase()) }

    fun onRegister(nip: String, onSuccess: () -> Unit) {
        val state = _uiState.value

        _registerStatus.value = RegisterStatus.Loading

        viewModelScope.launch {
            val result = registerUseCase(
                fullName = state.fullName,
                email = state.email,
                nip = nip,
                curp = state.curp,
                phone = state.phone
            )

            _registerStatus.value = if (result.isSuccess) {
                onSuccess()
                RegisterStatus.Success
            } else {
                result.exceptionOrNull()?.printStackTrace()
                RegisterStatus.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetStatus() {
        _registerStatus.value = RegisterStatus.Idle
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