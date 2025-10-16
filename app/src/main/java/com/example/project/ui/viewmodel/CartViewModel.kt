package com.example.project.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.project.api.ApiServices
import com.example.project.model.*
import com.example.project.utils.NotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.leolin.shortcutbadger.ShortcutBadger
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
    private val ctx = getApplication<Application>()
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    private val api = ApiServices.getApiService(ctx)

    /** Load cart từ server */
    fun loadCart() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

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
                    updateCartBadge()
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

    /** Thêm sản phẩm vào cart */
    fun addToCart(productId: Int, quantity: Int, onComplete: ((Boolean, String?) -> Unit)? = null) {
        if (quantity < 1) {
            onComplete?.invoke(false, "Quantity must be >= 1")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val payload = AddCartItemRequest(productId, quantity)

        api.addToCart(payload).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _uiState.update { it.copy(isLoading = false) }
                if (response.isSuccessful) {
                    loadCart()
                    onComplete?.invoke(true, "Added to cart successfully")
                } else {
                    val msg = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    onComplete?.invoke(false, msg ?: "Add to cart failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                onComplete?.invoke(false, "Network error: ${t.message}")
            }
        })
    }

    /** Tăng số lượng sản phẩm */
    fun increaseQuantity(productId: Int) {
        api.increaseToCart(CartRequest(productId)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val updatedItems = _uiState.value.items.map {
                        if (it.productId == productId) {
                            CartItemDto(
                                it.cartItemId,
                                it.productId,
                                it.productName,
                                it.price,
                                it.quantity + 1,
                                it.price * (it.quantity + 1)
                            )
                        } else it
                    }
                    val total = updatedItems.sumOf { it.subTotal }
                    _uiState.update { it.copy(items = updatedItems, totalPrice = total) }
                    updateCartBadge()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    /** Giảm số lượng sản phẩm */
    fun decreaseQuantity(productId: Int) {
        val item = _uiState.value.items.find { it.productId == productId } ?: return

        if (item.quantity <= 1) {
            removeFromCart(productId)
            return
        }

        api.decreaseToCart(CartRequest(productId)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val updatedItems = _uiState.value.items.map {
                        if (it.productId == productId) {
                            CartItemDto(
                                it.cartItemId,
                                it.productId,
                                it.productName,
                                it.price,
                                it.quantity - 1,
                                it.price * (it.quantity - 1)
                            )
                        } else it
                    }
                    val total = updatedItems.sumOf { it.subTotal }
                    _uiState.update { it.copy(items = updatedItems, totalPrice = total) }
                    updateCartBadge()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    /** Xóa sản phẩm khỏi cart */
    fun removeFromCart(productId: Int) {
        api.removeToCart(CartRequest(productId)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val updatedItems = _uiState.value.items.filter { it.productId != productId }
                    val total = updatedItems.sumOf { it.subTotal }
                    _uiState.update { it.copy(items = updatedItems, totalPrice = total) }
                    updateCartBadge()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    /** Cập nhật badge số lượng cart trên icon app */
    private fun updateCartBadge() {
        val count = _uiState.value.items.sumOf { it.quantity }

        if (count > 0) {
            NotificationUtils.showCartBadgeNotification(ctx, count)
        } else {
            ShortcutBadger.removeCount(ctx)
        }
    }



}