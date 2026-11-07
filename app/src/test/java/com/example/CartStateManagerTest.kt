package com.example

import com.example.data.cart.CartStateManager
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.viewmodel.CartViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartStateManagerTest {

    private lateinit var cartStateManager: CartStateManager
    private lateinit var sampleProductA: Product
    private lateinit var sampleProductB: Product

    @Before
    fun setUp() {
        cartStateManager = CartStateManager(initialItems = emptyList())
        sampleProductA = Product(
            id = "test_p1",
            title = "Batik Tulis Sukamaju",
            storeName = "Batik Bu Nur",
            storeVillage = "Dusun Krajan",
            category = ProductCategory.PAKAIAN,
            price = 50000L,
            description = "Batik katun halus"
        )
        sampleProductB = Product(
            id = "test_p2",
            title = "Sambal Bawang Mbok Darmi",
            storeName = "Dapur Mbok Darmi",
            storeVillage = "Dusun Krajan",
            category = ProductCategory.MAKANAN,
            price = 20000L,
            description = "Sambal pedas"
        )
    }

    @Test
    fun testAddItem_addsNewItemSuccessfully() {
        cartStateManager.addItem(sampleProductA, selectedVariant = "L", quantity = 2)

        val items = cartStateManager.items.value
        assertEquals(1, items.size)
        assertEquals(sampleProductA.id, items[0].product.id)
        assertEquals("L", items[0].selectedVariant)
        assertEquals(2, items[0].quantity)
        assertEquals(100000L, items[0].totalPrice)
    }

    @Test
    fun testAddItem_sameItemIncrementsQuantity() {
        cartStateManager.addItem(sampleProductA, selectedVariant = "L", quantity = 1)
        cartStateManager.addItem(sampleProductA, selectedVariant = "L", quantity = 2)

        val items = cartStateManager.items.value
        assertEquals(1, items.size)
        assertEquals(3, items[0].quantity)
        assertEquals(150000L, items[0].totalPrice)
    }

    @Test
    fun testRemoveItem_removesCorrectItem() {
        cartStateManager.addItem(sampleProductA, selectedVariant = "L", quantity = 1)
        cartStateManager.addItem(sampleProductB, selectedVariant = "Level 3", quantity = 1)
        assertEquals(2, cartStateManager.items.value.size)

        val itemAToRemove = cartStateManager.items.value[0].cartId
        cartStateManager.removeItem(itemAToRemove)

        val remaining = cartStateManager.items.value
        assertEquals(1, remaining.size)
        assertEquals(sampleProductB.id, remaining[0].product.id)
    }

    @Test
    fun testUpdateQuantity_decrementToZeroRemovesItem() {
        cartStateManager.addItem(sampleProductA, selectedVariant = "M", quantity = 1)
        val cartId = cartStateManager.items.value[0].cartId

        cartStateManager.updateQuantity(cartId, delta = -1)
        assertTrue(cartStateManager.items.value.isEmpty())
    }

    @Test
    fun testCalculateTotalPrice_computesOnlySelectedItems() {
        cartStateManager.addItem(sampleProductA, selectedVariant = "L", quantity = 2) // 50,000 * 2 = 100,000
        cartStateManager.addItem(sampleProductB, selectedVariant = "", quantity = 3)  // 20,000 * 3 = 60,000

        // Both selected
        assertEquals(160000L, cartStateManager.calculateTotalPrice())
        assertEquals(160000L, cartStateManager.uiState.value.totalSelectedPrice)

        // Unselect Product B
        val cartBId = cartStateManager.items.value[1].cartId
        cartStateManager.toggleSelection(cartBId)

        assertEquals(100000L, cartStateManager.calculateTotalPrice())
        assertEquals(100000L, cartStateManager.uiState.value.totalSelectedPrice)
        assertFalse(cartStateManager.uiState.value.isAllSelected)
    }

    @Test
    fun testCartViewModel_delegatesCorrectly() {
        val viewModel = CartViewModel(cartStateManager)
        viewModel.addItem(sampleProductA, "XL", 1)

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(50000L, state.totalSelectedPrice)
        assertEquals(50000L, viewModel.calculateTotalPrice())

        viewModel.removeItem(state.items[0].cartId)
        assertEquals(0, viewModel.uiState.value.items.size)
        assertEquals(0L, viewModel.calculateTotalPrice())
    }

    @Test
    fun testCheckout_clearCartViaViewModel() {
        val viewModel = CartViewModel(cartStateManager)
        viewModel.addItem(sampleProductA, "M", 2)
        viewModel.addItem(sampleProductB, "Pedas", 1)
        assertEquals(2, viewModel.cartItems.value.size)

        // Simulates CheckoutScreen Confirm Order clearing the cart
        viewModel.clearSelectedItems()
        assertTrue(viewModel.cartItems.value.isEmpty())
        assertEquals(0L, viewModel.calculateTotalPrice())
    }
}
