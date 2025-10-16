package com.example.project.api;

import com.example.project.model.*;

import java.util.List;

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

    @GET("User/{id}")
    Call<UserResponse> getUserById(
            @Path("id") int id

    );

    @GET("product/{id}")
   Call<ProductDetailResponse> getProductById(
           @Path("id") int id
    );



    @GET("Product/all")
    Call<List<ProductResponse>> getProducts();

    @GET("Category/all")
    Call<List<CategoryResponse>> getCategories();


    @GET("Cart")
    Call<CartResponse> getCart(); // GET /api/Cart


    @POST("Cart/add")
    Call<Void> addToCart(@Body AddCartItemRequest request); // POST /api/Cart/items


    @POST("Cart/increase")
    Call<Void> increaseToCart (@Body CartRequest request);

    @POST("Cart/decrease")
    Call<Void> decreaseToCart (@Body CartRequest request);

    @POST("Cart/remove")
    Call<Void> removeToCart (@Body CartRequest request);


}
