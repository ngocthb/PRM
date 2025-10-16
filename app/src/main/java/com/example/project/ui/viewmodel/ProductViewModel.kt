package com.example.project.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.model.AddCartItemRequest
import com.example.project.model.ProductResponse
import com.example.project.model.ProductDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ---- UI STATE CHO DANH SÁCH SẢN PHẨM ----
data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: Int? = null
)

// ---- UI STATE CHO CHI TIẾT SẢN PHẨM ----
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: ProductDetailResponse? = null,
    val error: String? = null
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(ProductDetailUiState())
    val detailState: StateFlow<ProductDetailUiState> = _detailState.asStateFlow()

    private val ctx = getApplication<Application>()

    // ---------------- DANH SÁCH ----------------
    fun loadProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val api = ApiServices.getApiService(ctx)
        api.getProducts().enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(
                call: Call<List<ProductResponse>>,
                response: Response<List<ProductResponse>>
            ) {
                Log.d("ProductViewModel", "onResponse: ${response.body()}")
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = data
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Load failed: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${t.message}"
                )
            }
        })
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onCategorySelected(categoryId: Int?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun getFilteredProducts(): List<ProductResponse> {
        val state = _uiState.value
        return state.products.filter { product ->
            (state.selectedCategoryId == null || product.getCategoryId() == state.selectedCategoryId) &&
                    (state.searchQuery.isBlank() || product.getProductName().contains(state.searchQuery, ignoreCase = true))
        }
    }

    // ---------------- CHI TIẾT ----------------
    fun getProductDetail(productId: Int) {
        if (productId <= 0) return
        _detailState.value = ProductDetailUiState(isLoading = true)

        val api = ApiServices.getApiService(ctx)
        api.getProductById(productId).enqueue(object : Callback<ProductDetailResponse> {
            override fun onResponse(
                call: Call<ProductDetailResponse>,
                response: Response<ProductDetailResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    _detailState.value = ProductDetailUiState(product = product)
                } else {
                    _detailState.value = ProductDetailUiState(error = "Lỗi: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProductDetailResponse>, t: Throwable) {
                Log.e("ProductViewModel", "getProductDetail failed", t)
                _detailState.value = ProductDetailUiState(error = t.message)
            }
        })
    }




}
