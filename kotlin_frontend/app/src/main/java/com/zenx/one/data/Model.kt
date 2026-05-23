package com.zenx.one.data.model

import com.google.gson.annotations.SerializedName

// ─── Product ───────────────────────────────────────────────────────────────

data class Product(
    val id: String,
    @SerializedName("external_id") val externalId: String,
    val name: String,
    val brand: String?,
    val category: String,
    @SerializedName("price_original") val priceOriginal: Double?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("product_url") val productUrl: String?,
    val description: String?,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("scraped_at") val scrapedAt: String?,
    @SerializedName("created_at") val createdAt: String,
    // discount fields (ProductWithDiscount)
    @SerializedName("discount_percentage") val discountPercentage: Double?,
    @SerializedName("final_price") val finalPrice: Double?,
    val reasoning: String?,
    @SerializedName("discount_expires_at") val discountExpiresAt: String?
)

data class ProductListResponse(
    val total: Int,
    val page: Int,
    val limit: Int,
    val items: List<Product>
)

// ─── User ──────────────────────────────────────────────────────────────────

data class User(
    val id: String,
    @SerializedName("external_id") val externalId: String,
    val name: String,
    val surname: String,
    val email: String,
    val phone: String?,
    @SerializedName("plan_type") val planType: String,
    @SerializedName("plan_name") val planName: String?,
    @SerializedName("monthly_spend_avg") val monthlySpendAvg: Double?,
    @SerializedName("data_usage_gb") val dataUsageGb: Double?,
    @SerializedName("age_group") val ageGroup: String?,
    @SerializedName("preferred_language") val preferredLanguage: String,
    @SerializedName("current_device_model") val currentDeviceModel: String?,
    @SerializedName("current_device_year") val currentDeviceYear: Int?,
    @SerializedName("current_device_brand") val currentDeviceBrand: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

// ─── Discount ──────────────────────────────────────────────────────────────

data class Discount(
    val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("discount_percentage") val discountPercentage: Double,
    @SerializedName("final_price") val finalPrice: Double?,
    val reasoning: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("generated_at") val generatedAt: String,
    @SerializedName("expires_at") val expiresAt: String?,
    val product: Product?
)

// ─── Payment ───────────────────────────────────────────────────────────────

enum class PaymentMethod { PRE_PAY, POST_PAY }

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)
