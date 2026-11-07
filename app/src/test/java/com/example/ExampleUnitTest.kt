package com.example

import com.example.data.model.PaymentMethod
import com.example.data.model.ProductCategory
import com.example.data.model.ShippingMethod
import com.example.data.repository.PasarDesaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun repository_initialProducts_isNotEmpty() {
        val products = PasarDesaRepository.products.value
        assertTrue("Products catalog should not be empty", products.isNotEmpty())
        assertTrue("Should have fashion category", products.any { it.category == ProductCategory.PAKAIAN })
        assertTrue("Should have food category", products.any { it.category == ProductCategory.MAKANAN })
        assertTrue("Should have courier service", products.any { it.category == ProductCategory.PENGANTARAN })
    }

    @Test
    fun repository_orderCreation_calculatesTotalAndCOD() {
        val initialOrderCount = PasarDesaRepository.orders.value.size
        val order = PasarDesaRepository.createOrder(
            shippingMethod = ShippingMethod.KURIR_LOKAL,
            paymentMethod = PaymentMethod.COD,
            buyerNote = "COD ke rumah"
        )
        assertNotNull(order)
        assertEquals(PaymentMethod.COD, order.paymentMethod)
        assertEquals(ShippingMethod.KURIR_LOKAL, order.shippingMethod)
        assertTrue(PasarDesaRepository.orders.value.size >= initialOrderCount + 1)
    }
}
