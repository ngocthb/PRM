package com.example.project.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.model.StoreLocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ---- UI STATE ----
data class StoreLocationUiState(
    val isLoading: Boolean = false,
    val locations: List<StoreLocationResponse> = emptyList(),
    val error: String? = null
)

class StoreLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StoreLocationUiState())
    val uiState: StateFlow<StoreLocationUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()

    // Gọi API lấy tất cả store locations
    fun loadStoreLocations() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val api = ApiServices.getApiService(ctx)

        api.getAllStoreLocation().enqueue(object : Callback<List<StoreLocationResponse>> {
            override fun onResponse(
                call: Call<List<StoreLocationResponse>>,
                response: Response<List<StoreLocationResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StoreLocationUiState(
                        isLoading = false,
                        locations = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = StoreLocationUiState(
                        isLoading = false,
                        error = "Load failed: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<StoreLocationResponse>>, t: Throwable) {
                Log.e("StoreLocationVM", "loadStoreLocations failed", t)
                _uiState.value = StoreLocationUiState(
                    isLoading = false,
                    error = "Network error: ${t.message}"
                )
            }
        })
    }
}
