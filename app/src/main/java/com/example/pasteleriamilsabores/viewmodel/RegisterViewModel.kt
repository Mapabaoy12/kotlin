package com.example.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = name,
                nameError = ""
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = ""
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = ""
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = ""
            )
        }
    }

    fun register() {
        val currentState = _uiState.value

        // Validaciones
        val nameError = if (currentState.name.isBlank()) "El nombre es requerido" else ""
        val emailError = if (currentState.email.isBlank()) "El email es requerido" else ""
        val passwordError = if (currentState.password.isBlank()) "La contraseña es requerida" else ""
        val confirmPasswordError = when {
            currentState.confirmPassword.isBlank() -> "Debe confirmar la contraseña"
            currentState.confirmPassword != currentState.password -> "Las contraseñas no coinciden"
            else -> ""
        }

        if (nameError.isNotEmpty() || emailError.isNotEmpty() ||
            passwordError.isNotEmpty() || confirmPasswordError.isNotEmpty()) {
            _uiState.update { it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )}
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        // Simulación de registro exitoso
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = true
            )
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegisterViewModel()
            }
        }
    }
}
