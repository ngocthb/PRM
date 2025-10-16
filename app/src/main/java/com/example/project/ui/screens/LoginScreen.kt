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
import com.example.project.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    viewModel: LoginViewModel,
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

    // show snackbar + navigate on success
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Login success!", actionLabel = SnackbarType.SUCCESS.name)
            }
            viewModel.clearLoginSuccessFlag()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.ic_app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Card login
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
                Text("Sign In", fontSize = 26.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))

                LoginTextField(uiState.email, { viewModel.onEmailChange(it) }, "Email")
                LoginTextField(uiState.password, { viewModel.onPasswordChange(it) }, "Password", true)

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF6588E6))
                } else {
                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6588E6))
                    ) {
                        Text("Sign In", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Don't have an account?")
                TextButton(onClick = onRegisterClick) { Text("Sign Up") }
            }
        }
    }
}

@Composable
fun LoginTextField(
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