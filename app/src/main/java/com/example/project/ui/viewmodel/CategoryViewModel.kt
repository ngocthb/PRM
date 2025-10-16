package com.example.project.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.model.CategoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 🔹 UI State
data class CategoryUiState(
    val categories: List<CategoryResponse> = emptyList(),
    val selectedCategoryId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// 🔹 ViewModel
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()
    private val api = ApiServices.getApiService(ctx)

    // Load danh sách category từ API
    fun loadCategories() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        api.getCategories().enqueue(object : Callback<List<CategoryResponse>> {
            override fun onResponse(
                call: Call<List<CategoryResponse>>,
                response: Response<List<CategoryResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val categories = response.body()!!
                    Log.d("CategoryVM", "Loaded categories: ${categories.size}")
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed: ${response.code()}"
                        )
                    }
                    Log.e("CategoryVM", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CategoryResponse>>, t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${t.message}"
                    )
                }
                Log.e("CategoryVM", "Failure: ${t.message}")
            }
        })
    }

    // Khi user chọn 1 category
    fun onCategorySelected(id: Int) {
        _uiState.update { it.copy(selectedCategoryId = id) }
    }

    // Reset lỗi (nếu có)
    fun resetError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
