package com.zenx.one.data.network

import com.zenx.one.data.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface OneApiService {

    // Products
    @GET("products/")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductListResponse>

    // Users
    @POST("users/sync")
    suspend fun syncUser(@Body user: Map<String, Any>): Response<User>

    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: String): Response<User>

    @PATCH("users/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: String,
        @Body payload: Map<String, Any>
    ): Response<User>

    // Discounts
    @GET("discounts/user/{user_id}")
    suspend fun getDiscounts(@Path("user_id") userId: String): Response<List<Discount>>

    @POST("discounts/user/{user_id}/regenerate")
    suspend fun regenerateDiscounts(@Path("user_id") userId: String): Response<Map<String, Int>>

    companion object {
        // Update this to your deployed backend URL
        private const val BASE_URL = "http://10.0.2.2:8000/"

        fun create(): OneApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OneApiService::class.java)

        val instance: OneApiService by lazy { create() }
    }
}
