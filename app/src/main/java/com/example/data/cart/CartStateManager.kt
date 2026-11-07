package com.example.data.cart

import com.example.data.model.CartItem
import com.example.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * UI State representation for the user's shopping cart.
 */
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalSelectedPrice: Long = 0L,
    val totalItemsCount: Int = 0,
    val selectedItemsCount: Int = 0,
    val isAllSelected: Boolean = false,
    val formattedTotalPrice: String = "Rp 0"
)

/**
 * State-management class to handle adding, removing, updating,
 * and calculating the total price of items in the user's cart.
 */
class CartStateManager(initialItems: List<CartItem> = emptyList()) {

    private val _items = MutableStateFlow(initialItems)
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _uiState = MutableStateFlow(calculateUiState(initialItems))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    /**
     * Adds an item to the cart. If the product and variant already exist,
     * it increases the quantity.
     */
    fun addItem(product: Product, selectedVariant: String = "", quantity: Int = 1) {
        if (quantity <= 0) return

        val currentList = _items.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.product.id == product.id && it.selectedVariant == selectedVariant
        }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            val newItem = CartItem(
                cartId = "cart_${UUID.randomUUID()}",
                product = product,
                selectedVariant = selectedVariant,
                quantity = quantity,
                isSelected = true
            )
            currentList.add(newItem)
        }

        updateItems(currentList)
    }

    /**
     * Removes an item from the cart by its unique cartId.
     */
    fun removeItem(cartId: String) {
        val filtered = _items.value.filter { it.cartId != cartId }
        updateItems(filtered)
    }

    /**
     * Updates the quantity of a specific cart item by delta (+1 or -1).
     * If the resulting quantity is 0 or less, the item is removed.
     */
    fun updateQuantity(cartId: String, delta: Int) {
        val currentList = _items.value.toMutableList()
        val index = currentList.indexOfFirst { it.cartId == cartId }
        if (index >= 0) {
            val item = currentList[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = item.copy(quantity = newQty)
            }
            updateItems(currentList)
        }
    }

    /**
     * Sets an explicit quantity for a cart item.
     */
    fun setQuantity(cartId: String, quantity: Int) {
        val currentList = _items.value.toMutableList()
        val index = currentList.indexOfFirst { it.cartId == cartId }
        if (index >= 0) {
            if (quantity <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = currentList[index].copy(quantity = quantity)
            }
            updateItems(currentList)
        }
    }

    /**
     * Toggles the selection state of a cart item for checkout.
     */
    fun toggleSelection(cartId: String) {
        val updated = _items.value.map {
            if (it.cartId == cartId) it.copy(isSelected = !it.isSelected) else it
        }
        updateItems(updated)
    }

    /**
     * Selects or deselects all items in the cart.
     */
    fun toggleSelectAll(select: Boolean) {
        val updated = _items.value.map { it.copy(isSelected = select) }
        updateItems(updated)
    }

    /**
     * Calculates the total price of all selected items in the cart.
     */
    fun calculateTotalPrice(): Long {
        return _items.value
            .filter { it.isSelected }
            .sumOf { it.totalPrice }
    }

    /**
     * Clears all items from the cart.
     */
    fun clearCart() {
        updateItems(emptyList())
    }

    /**
     * Removes only the currently selected items (e.g., after successful checkout).
     */
    fun clearSelectedItems() {
        val remaining = _items.value.filter { !it.isSelected }
        updateItems(remaining)
    }

    private fun updateItems(newList: List<CartItem>) {
        _items.value = newList
        _uiState.value = calculateUiState(newList)
    }

    private fun calculateUiState(itemList: List<CartItem>): CartUiState {
        val selectedItems = itemList.filter { it.isSelected }
        val totalSelectedPrice = selectedItems.sumOf { it.totalPrice }
        val totalCount = itemList.sumOf { it.quantity }
        val selectedCount = selectedItems.sumOf { it.quantity }
        val isAllSelected = itemList.isNotEmpty() && itemList.all { it.isSelected }

        return CartUiState(
            items = itemList,
            totalSelectedPrice = totalSelectedPrice,
            totalItemsCount = totalCount,
            selectedItemsCount = selectedCount,
            isAllSelected = isAllSelected,
            formattedTotalPrice = "Rp %,d".format(totalSelectedPrice).replace(',', '.')
        )
    }
}
