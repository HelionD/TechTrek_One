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
    val selectedCategory: String? = null,
    val page: Int = 1,
    val totalPages: Int = 1
)

class ShopViewModel(
    private val productRepo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState(isLoading = true))
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init { loadProducts() }

    fun loadProducts(category: String? = null) {
        val effectiveCategory = category ?: _uiState.value.selectedCategory
        viewModelScope.launch {
            // Load products first
            _uiState.update { it.copy(isLoading = true, error = null) }
            val productResult = productRepo.getProducts(category = effectiveCategory)
            when (productResult) {
                is Result.Success -> {
                    val productData = productResult.data
                    // Directly assign products (already contain discount fields)
                    _uiState.update {
                        it.copy(
                            products = productData.items,
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
}

// ─── Cart ViewModel ────────────────────────────────────────────────────────

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val paymentConfirmed: Boolean = false,
    val selectedProduct: Product? = null,
    val paymentMethod: PaymentMethod? = null
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
            state.copy(items = updated)
        }
    }

    fun selectProduct(product: Product) {
        _uiState.update { it.copy(selectedProduct = product) }
    }

    fun confirmPayment(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method, paymentConfirmed = true) }
    }

    fun resetPayment() {
        _uiState.update { it.copy(paymentConfirmed = false, paymentMethod = null) }
    }

    fun totalItems() = _uiState.value.items.sumOf { it.quantity }
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
