package com.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.R
import com.example.project.ui.components.SnackbarType

import com.example.project.ui.viewmodel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState, // passed from parent
    scope: CoroutineScope, // passed from parent
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // show snackbar for errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(message = msg, actionLabel = SnackbarType.ERROR.name)
            }
            viewModel.resetError()
        }
    }

    // on success -> show snackbar then notify parent
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Register success!", actionLabel = SnackbarType.SUCCESS.name)
            }
            viewModel.clearRegisterSuccessFlag()
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sign Up", fontSize = 26.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))

                RegisterTextField(uiState.username, { viewModel.onUsernameChange(it) }, "Username")
                RegisterTextField(uiState.password, { viewModel.onPasswordChange(it) }, "Password", true)
                RegisterTextField(uiState.email, { viewModel.onEmailChange(it) }, "Email")
                RegisterTextField(uiState.phoneNumber, { viewModel.onPhoneChange(it) }, "Phone")
                RegisterTextField(uiState.address, { viewModel.onAddressChange(it) }, "Address")

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF6588E6))
                } else {
                    Button(
                        onClick = { viewModel.register() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6588E6))
                    ) {
                        Text("Sign Up", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Already have an account?")
                TextButton(onClick = onBackToLogin) { Text("Sign In") }
            }
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Spacer(modifier = Modifier.height(12.dp))
    TextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(color = Color.Black),
    )
}