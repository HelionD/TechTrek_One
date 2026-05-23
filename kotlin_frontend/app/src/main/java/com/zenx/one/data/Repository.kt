package com.zenx.one.data.repository

import com.zenx.one.data.model.*
import com.zenx.one.data.network.OneApiService

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class ProductRepository(private val api: OneApiService = OneApiService.instance) {

    suspend fun getProducts(
        category: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<ProductListResponse> {
        return try {
            val response = api.getProducts(category, page, limit)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to load products: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}

class UserRepository(private val api: OneApiService = OneApiService.instance) {

    suspend fun getUser(userId: String): Result<User> {
        return try {
            val response = api.getUser(userId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("User not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}

class DiscountRepository(private val api: OneApiService = OneApiService.instance) {

    suspend fun getDiscounts(userId: String): Result<List<Discount>> {
        return try {
            val response = api.getDiscounts(userId)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Failed to load discounts")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    suspend fun regenerate(userId: String): Result<Unit> {
        return try {
            val response = api.regenerateDiscounts(userId)
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("Regeneration failed")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}
