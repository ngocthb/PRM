package com.example.project.ui.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.model.RegisterRequest
import com.example.project.model.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()

    fun onUsernameChange(v: String) = _uiState.update { it.copy(username = v, errorMessage = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, errorMessage = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorMessage = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(phoneNumber = v, errorMessage = null) }
    fun onAddressChange(v: String) = _uiState.update { it.copy(address = v, errorMessage = null) }

    fun resetError() = _uiState.update { it.copy(errorMessage = null) }
    fun clearRegisterSuccessFlag() = _uiState.update { it.copy(registerSuccess = false) }

    fun register() {
        val current = _uiState.value
        if (current.isLoading) return
        if (current.username.isBlank() || current.password.isBlank() || current.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields!") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Build request. Make sure RegisterRequest constructor matches your model fields.
        // In your earlier code you used RegisterRequest(username, password, email, phone, address)
        val request = RegisterRequest(
            current.username,
            current.password,
            current.email,
            current.phoneNumber,
            current.address
        )

        val api = ApiServices.getApiService(ctx) // ensure ApiServices has getApiService(context)

        api.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                } else {
                    val msg = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg ?: "Register failed: ${response.code()}") }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error: ${t.message}") }
            }
        })
    }
}