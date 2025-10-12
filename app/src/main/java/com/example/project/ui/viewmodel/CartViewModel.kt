package com.example.project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.api.ApiServices
import com.example.project.model.CartItemDto
import com.example.project.model.CartResponse
import com.example.project.model.AddCartItemRequest
import com.example.project.model.UpdateCartItemRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class CartUiState(
    val items: List<CartItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalPrice: Double = 0.0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val ctx = getApplication<Application>()

    fun loadCart() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val api = ApiServices.getApiService(ctx)
        api.getCart().enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = body.items,
                            totalPrice = body.totalPrice
                        )
                    }
                } else {
                    val msg = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg ?: "Load cart failed: ${response.code()}") }
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error: ${t.message}") }
            }
        })
    }

    fun updateQuantity(cartItemId: Int, newQuantity: Int, onComplete: (success: Boolean, message: String?) -> Unit) {
        if (newQuantity < 1) { onComplete(false, "Quantity must be >= 1"); return }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val api = ApiServices.getApiService(ctx)
        val req = UpdateCartItemRequest(cartItemId,newQuantity)
        api.updateCartItem(cartItemId, req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // reload cart to get updated totals
                    loadCart()
                    onComplete(true, null)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onComplete(false, "Update failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onComplete(false, "Network error: ${t.message}")
            }
        })
    }

    fun removeItem(cartItemId: Int, onComplete: (success: Boolean, message: String?) -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val api = ApiServices.getApiService(ctx)
        api.deleteCartItem(cartItemId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadCart()
                    onComplete(true, null)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onComplete(false, "Delete failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onComplete(false, "Network error: ${t.message}")
            }
        })
    }

    fun addItem(productId: Int, quantity: Int, onComplete: (success: Boolean, message: String?) -> Unit) {
        val api = ApiServices.getApiService(ctx)
        val req = AddCartItemRequest(productId, quantity)
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        api.addCartItem(req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadCart()
                    onComplete(true, null)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onComplete(false, "Add failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onComplete(false, "Network error: ${t.message}")
            }
        })
    }

    fun checkout(onComplete: (success: Boolean, message: String?) -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val api = ApiServices.getApiService(ctx)
        api.checkoutCart().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // after checkout, reload cart (should be empty or new cart)
                    loadCart()
                    onComplete(true, null)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onComplete(false, "Checkout failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onComplete(false, "Network error: ${t.message}")
            }
        })
    }
}