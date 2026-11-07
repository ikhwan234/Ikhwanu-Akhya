package com.example

import com.example.ui.screens.defaultMockPastOrders
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PastOrdersTest {

    @Test
    fun testDefaultMockPastOrders_hasRequiredFields() {
        val mockOrders = defaultMockPastOrders
        assertNotNull(mockOrders)
        assertTrue("Past orders list should not be empty", mockOrders.isNotEmpty())

        mockOrders.forEach { order ->
            assertFalse("Order ID should not be blank", order.orderId.isBlank())
            assertFalse("Date should not be blank", order.date.isBlank())
            assertTrue("Total amount must be greater than 0", order.totalAmount > 0)
            assertFalse("Order status should not be blank", order.status.isBlank())
            assertTrue("Formatted total should contain Rp", order.formattedTotal.contains("Rp"))
        }

        // Verify distinct statuses exist in mock list
        val statuses = mockOrders.map { it.status }.toSet()
        assertTrue("Should contain Selesai status", statuses.contains("Selesai"))
        assertTrue("Should contain Sedang Diantar status", statuses.contains("Sedang Diantar"))
    }
}
