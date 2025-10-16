package com.example.project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.api.TokenManager
import com.example.project.model.LoginRequest
import com.example.project.model.LoginResponse
import com.example.project.model.UserResponse
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
    val loginSuccess: Boolean = false,
    val token: String? = null,
    val user: UserResponse? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()
    private val tokenManager = TokenManager.getInstance(ctx)

    init {
        // Kiểm tra token còn hạn hay không
        val savedToken = tokenManager.getToken()
        val savedUserId = tokenManager.getUserId()
        if (!savedToken.isNullOrEmpty() && savedUserId != -1L) {
            // token còn hạn -> tự động fetch user info
            _uiState.update { it.copy(token = savedToken) }
            fetchUserInfo(savedUserId.toInt())
        } else {
            // token hết hạn hoặc không có -> clear và reset state
            tokenManager.clear()
            _uiState.update { it.copy(token = null, loginSuccess = false) }
        }
    }

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
        val api = ApiServices.getApiService(ctx)

        api.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    val userIdLong = body.userId

                    if (token.isNotEmpty()) {
                        // Lưu token + userId + thời gian hết hạn 3h
                        tokenManager.saveTokenAndUserId(token, userIdLong.toLong())

                        // Fetch thông tin user
                        fetchUserInfo(userIdLong)
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Server didn't return token") }
                    }
                } else {
                    val raw = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    val parsedMessage = raw?.let {
                        try {
                            val jo = JSONObject(it)
                            when {
                                jo.has("message") -> jo.getString("message")
                                jo.has("error") -> jo.getString("error")
                                else -> it
                            }
                        } catch (e: Exception) {
                            it
                        }
                    } ?: "Login failed: ${response.code()}"

                    _uiState.update { it.copy(isLoading = false, errorMessage = parsedMessage) }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error: ${t.message}") }
            }
        })
    }

    private fun fetchUserInfo(userId: Int) {
        val api = ApiServices.getApiService(ctx)
        api.getUserById(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            user = user
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load user info"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${t.message}"
                    )
                }
            }
        })
    }

    // Hàm tiện ích: kiểm tra token còn hợp lệ, logout nếu hết hạn
    fun checkTokenValidity() {
        if (!tokenManager.isTokenValid()) {
            tokenManager.clear()
            _uiState.update { it.copy(token = null, loginSuccess = false, user = null) }
        }
    }
}
