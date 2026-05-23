package com.zenx.one.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenx.one.data.model.*
import com.zenx.one.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ─── Shop ViewModel ────────────────────────────────────────────────────────

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val selectedCategory: String? = null,
    val page: Int = 1,
    val totalPages: Int = 1
)

class ShopViewModel(
    private val productRepo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState(isLoading = true))
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    // Demo user ID — in production, from auth session
    private val demoUserId: String? = null // set via SharedPreferences or argument

    init { loadProducts() }

    fun loadProducts(category: String? = null) {
        val effectiveCategory = category ?: _uiState.value.selectedCategory
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val productResult = productRepo.getProducts(
                category = effectiveCategory,
                userId = demoUserId,
            )
            when (productResult) {
                is Result.Success -> {
                    val productData = productResult.data
                    val itemsWithImagesAndDiscounts = productData.items.mapIndexed { index, product ->
                        val random = java.util.Random(product.id.hashCode().toLong())
                        
                        // 1. Force a discount for most items if not present
                        var finalPrice = product.finalPrice
                        var originalPrice = product.priceOriginal ?: 0.0
                        var discountPct = product.discountPercentage

                        if (finalPrice == null || finalPrice >= originalPrice) {
                            val pct = 10 + random.nextInt(21)
                            discountPct = pct.toDouble()
                            val basePrice = if (originalPrice > 0) originalPrice else (100 + random.nextInt(900)).toDouble()
                            originalPrice = basePrice
                            finalPrice = basePrice * (1.0 - (pct / 100.0))
                        }

                        // 2. HARDCODED PHONE IMAGES FOR FIRST 6
                        val hardcodedPhones = listOf(
                            "https://images.unsplash.com/photo-1616348436168-de43ad0db179?w=800", // iPhone
                            "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800", // Android
                            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800", // Smartphone white
                            "https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=800", // Multiple phones
                            "https://images.unsplash.com/photo-1556656793-062ff98782ee?w=800", // iPhone pink
                            "https://images.unsplash.com/photo-1580910051074-3eb6948865c5?w=800"  // Samsung
                        )

                        val placeholderImage = if (index < 6) {
                            hardcodedPhones[index]
                        } else {
                            when (product.category.lowercase()) {
                                "telefona", "phones" -> {
                                    val urls = listOf(
                                        "https://images.unsplash.com/photo-1565849906461-0e443bdad9ca?w=800",
                                        "https://images.unsplash.com/photo-1533228891584-49926318bb38?w=800",
                                        "https://images.unsplash.com/photo-1591333139265-2fd213efefd1?w=800",
                                        "https://images.unsplash.com/photo-1573148195900-7845dcb9b127?w=800"
                                    )
                                    urls[random.nextInt(urls.size)]
                                }
                                "wearables" -> {
                                    val urls = listOf(
                                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800",
                                        "https://images.unsplash.com/photo-1544117518-30dd5ff7a4b0?w=800",
                                        "https://images.unsplash.com/photo-1508685096489-7aac29683950?w=800"
                                    )
                                    urls[random.nextInt(urls.size)]
                                }
                                else -> "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800"
                            }
                        }
                        
                        product.copy(
                            imageUrl = placeholderImage,
                            priceOriginal = originalPrice,
                            finalPrice = finalPrice,
                            discountPercentage = discountPct
                        )
                    }
                    _uiState.update {
                        it.copy(
                            products = itemsWithImagesAndDiscounts,
                            isLoading = false,
                            selectedCategory = effectiveCategory,
                            totalPages = (productData.total + productData.limit - 1) / productData.limit
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = productResult.message) }
                Result.Loading -> {}
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts(category)
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

// ─── Cart ViewModel ────────────────────────────────────────────────────────

enum class DeliveryOption {
    STORE_PICKUP,
    COURIER
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val wishlist: List<Product> = emptyList(),
    val paymentConfirmed: Boolean = false,
    val selectedProduct: Product? = null,
    val paymentMethod: PaymentMethod? = null,
    val showCardPopup: Boolean = false,
    val showDeliveryOption: Boolean = false,
    val deliveryOption: DeliveryOption? = null,
    val message: String? = null
)

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addToCart(product: Product) {
        _uiState.update { state ->
            val existing = state.items.find { it.product.id == product.id }
            val updated = if (existing != null) {
                state.items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                state.items + CartItem(product)
            }
            state.copy(items = updated, message = "Added ${product.name} to cart")
        }
    }

    fun addToWishlist(product: Product) {
        _uiState.update { state ->
            if (state.wishlist.none { it.id == product.id }) {
                state.copy(wishlist = state.wishlist + product, message = "Added ${product.name} to wishlist")
            } else {
                state.copy(message = "${product.name} is already in wishlist")
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun selectProduct(product: Product) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun onPrepayClick() {
        _uiState.update { it.copy(showCardPopup = true) }
    }

    fun onPostpayClick() {
        _uiState.update { it.copy(paymentMethod = PaymentMethod.POST_PAY, showDeliveryOption = true) }
    }

    fun onCardDetailsSubmitted(cardNumber: String, expiry: String, cvv: String) {
        // Mock validation
        if (cardNumber.isNotBlank()) {
            _uiState.update { it.copy(showCardPopup = false, paymentMethod = PaymentMethod.PRE_PAY, showDeliveryOption = true) }
        }
    }

    fun dismissCardPopup() {
        _uiState.update { it.copy(showCardPopup = false) }
    }

    fun selectDeliveryOption(option: DeliveryOption) {
        _uiState.update { it.copy(deliveryOption = option, showDeliveryOption = false, paymentConfirmed = true) }
    }

    fun dismissDeliveryOption() {
        _uiState.update { it.copy(showDeliveryOption = false) }
    }

    fun resetPayment() {
        _uiState.update { it.copy(paymentConfirmed = false, paymentMethod = null, deliveryOption = null) }
    }

    fun totalItems() = _uiState.value.items.sumOf { it.quantity }

    fun totalPrice() = _uiState.value.items.sumOf { (it.product.finalPrice ?: it.product.priceOriginal ?: 0.0) * it.quantity }
}

// ─── Discount ViewModel ────────────────────────────────────────────────────

data class DiscountUiState(
    val discounts: List<Discount> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DiscountViewModel(
    private val repo: DiscountRepository = DiscountRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscountUiState())
    val uiState: StateFlow<DiscountUiState> = _uiState.asStateFlow()

    fun loadDiscounts(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val r = repo.getDiscounts(userId)) {
                is Result.Success -> _uiState.update { it.copy(discounts = r.data, isLoading = false) }
                is Result.Error   -> _uiState.update { it.copy(error = r.message, isLoading = false) }
                Result.Loading    -> {}
            }
        }
    }
}
