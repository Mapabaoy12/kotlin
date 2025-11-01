package com.example.pasteleriamilsabores.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import com.example.pasteleriamilsabores.viewmodel.RegisterViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    paddingValues: PaddingValues,
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // NOMBRE
        TextField(
            value = uiState.name,
            onValueChange = { viewModel.updateName(it) },
            label = {
                Text(
                    uiState.nameError.ifEmpty { "Nombre completo" },
                    color = if (uiState.nameError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            leadingIcon = { Icon(Icons.Rounded.AccountCircle, contentDescription = "Nombre Icon") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // EMAIL
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = {
                Text(
                    uiState.emailError.ifEmpty { "Email" },
                    color = if (uiState.emailError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = "Email Icon") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // PASSWORD
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = {
                Text(
                    uiState.passwordError.ifEmpty { "Contraseña" },
                    color = if (uiState.passwordError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Password Icon") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // CONFIRM PASSWORD
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = {
                Text(
                    uiState.confirmPasswordError.ifEmpty { "Confirmar contraseña" },
                    color = if (uiState.confirmPasswordError.isNotEmpty()) Color.Red else Color.Unspecified
                )
            },
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Confirm Password Icon") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        // BOTÓN CREAR CUENTA
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.register()
                if (uiState.isSuccess) {
                    onRegisterSuccess()
                }
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 90.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Crear Cuenta")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(text = "¿Ya tienes una cuenta? ")
            Text(
                text = "Inicia sesión",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }

        // Mostrar mensajes de error si existen
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage ?: "",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}
