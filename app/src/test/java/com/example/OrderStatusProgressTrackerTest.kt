package com.example

import com.example.data.model.OrderStatus
import com.example.ui.components.defaultOrderTrackingSteps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderStatusProgressTrackerTest {

    @Test
    fun testDefaultOrderTrackingSteps_matchesExpectedWorkflow() {
        val steps = defaultOrderTrackingSteps
        assertEquals(4, steps.size)

        // Step 0: Ordered
        assertEquals("Ordered", steps[0].title)
        assertEquals("Pesanan Dibuat", steps[0].subtitle)

        // Step 1: Preparing
        assertEquals("Preparing", steps[1].title)
        assertEquals("Sedang Disiapkan", steps[1].subtitle)

        // Step 2: On Delivery
        assertEquals("On Delivery", steps[2].title)
        assertEquals("Sedang Diantar", steps[2].subtitle)

        // Step 3: Delivered
        assertEquals("Delivered", steps[3].title)
        assertEquals("Pesanan Selesai", steps[3].subtitle)

        // Ensure all steps have non-blank descriptions
        steps.forEach { step ->
            assertTrue("Description must not be blank", step.description.isNotBlank())
            assertNotNull("Icon must be defined", step.icon)
        }
    }

    @Test
    fun testOrderStatus_stepIndexMapping() {
        assertEquals(0, OrderStatus.BELUM_BAYAR.stepIndex)
        assertEquals(1, OrderStatus.DIKEMAS.stepIndex)
        assertEquals(2, OrderStatus.DIKIRIM.stepIndex)
        assertEquals(3, OrderStatus.SELESAI.stepIndex)
    }
}
