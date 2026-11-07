package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.cart.CartStateManager
import com.example.data.cart.CartUiState
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.repository.PasarDesaRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel responsible for managing user shopping cart state,
 * adding and removing items, modifying quantities, and calculating totals.
 */
class CartViewModel(
    private val cartStateManager: CartStateManager = PasarDesaRepository.cartStateManager
) : ViewModel() {

    /**
     * Observable UI State containing cart items, selection status,
     * quantities, and calculated total price.
     */
    val uiState: StateFlow<CartUiState> = cartStateManager.uiState

    /**
     * Observable list of cart items.
     */
    val cartItems: StateFlow<List<CartItem>> = cartStateManager.items

    /**
     * Adds a product to the cart with specified variant and quantity.
     */
    fun addItem(product: Product, selectedVariant: String = "", quantity: Int = 1) {
        val variantToUse = if (selectedVariant.isBlank() && product.variants.isNotEmpty()) {
            product.variants.first().options.firstOrNull() ?: ""
        } else selectedVariant

        cartStateManager.addItem(product, variantToUse, quantity)
    }

    /**
     * Removes an item from the cart by its cartId.
     */
    fun removeItem(cartId: String) {
        cartStateManager.removeItem(cartId)
    }

    /**
     * Increments or decrements item quantity by delta.
     */
    fun updateQuantity(cartId: String, delta: Int) {
        cartStateManager.updateQuantity(cartId, delta)
    }

    /**
     * Sets an explicit quantity for a cart item.
     */
    fun setQuantity(cartId: String, quantity: Int) {
        cartStateManager.setQuantity(cartId, quantity)
    }

    /**
     * Toggles the selection of an item in the cart.
     */
    fun toggleSelection(cartId: String) {
        cartStateManager.toggleSelection(cartId)
    }

    /**
     * Selects or deselects all items in the cart.
     */
    fun toggleSelectAll(select: Boolean) {
        cartStateManager.toggleSelectAll(select)
    }

    /**
     * Returns the current total price of all selected items.
     */
    fun calculateTotalPrice(): Long {
        return cartStateManager.calculateTotalPrice()
    }

    /**
     * Clears all items in the cart.
     */
    fun clearCart() {
        cartStateManager.clearCart()
    }

    /**
     * Clears selected items from the cart (e.g. after order checkout).
     */
    fun clearSelectedItems() {
        cartStateManager.clearSelectedItems()
    }
}
