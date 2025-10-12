package com.example.project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.api.TokenManager
import com.example.project.model.LoginRequest
import com.example.project.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()

    fun onEmailChange(new: String) {
        _uiState.update { it.copy(email = new, errorMessage = null) }
    }

    fun onPasswordChange(new: String) {
        _uiState.update { it.copy(password = new, errorMessage = null) }
    }

    fun resetError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearLoginSuccessFlag() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    fun login() {
        val current = _uiState.value
        if (current.isLoading) return
        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields!") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val request = LoginRequest(current.email, current.password)
        val api = ApiServices.getApiService(ctx) // đảm bảo ApiServices signature nhận Context

        api.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    val userIdLong = body.userId // đã Long theo model

                    if (token.isNotEmpty()) {
                        // Lưu token + userId
                        TokenManager.getInstance(ctx).saveTokenAndUserId(token, userIdLong.toLong())
                        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Server didn't return token") }
                    }
                } else {
                    // đọc thô (chỉ đọc được 1 lần)
                    val raw = try { response.errorBody()?.string() } catch (e: Exception) { null }

                    // parse JSON nhẹ bằng JSONObject, lấy "message" hoặc fallback về raw hoặc thông báo mặc định
                    val parsedMessage = raw?.let {
                        try {
                            val jo = JSONObject(it)
                            // thử các key hay dùng
                            when {
                                jo.has("message") -> jo.getString("message")
                                jo.has("error") -> jo.getString("error")
                                else -> it // fallback: show raw body
                            }
                        } catch (e: Exception) {
                            // không phải JSON -> show nguyên chuỗi
                            it
                        }
                    } ?: "Login failed: ${response.code()}"

                    // cập nhật state để UI hiển thị (snackbar sẽ lấy uiState.errorMessage)
                    _uiState.update { it.copy(isLoading = false, errorMessage = parsedMessage) }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error: ${t.message}") }
            }
        })
    }
}