package com.example.project.api;

import com.example.project.model.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("Auth/register")   // hoặc "register" tuỳ backend bạn dùng baseUrl
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("Auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @GET("Cart")
    Call<CartResponse> getCart(); // GET /api/Cart


    // đoạn này chưa làm
    @POST("Cart/items")
    Call<Void> addCartItem(@Body AddCartItemRequest request); // POST /api/Cart/items

    @PUT("Cart/items/{cartItemId}")
    Call<Void> updateCartItem(@Path("cartItemId") int cartItemId, @Body UpdateCartItemRequest request); // PUT /api/Cart/items/{cartItemId}

    @DELETE("Cart/items/{cartItemId}")
    Call<Void> deleteCartItem(@Path("cartItemId") int cartItemId); // DELETE /api/Cart/items/{cartItemId}

    @POST("Cart/checkout")
    Call<Void> checkoutCart(); // POST /api/Cart/checkout
}
