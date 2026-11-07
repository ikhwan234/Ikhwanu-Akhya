package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.DesaGreenPrimary
import com.example.ui.theme.ShopeeOrangeSecondary

/**
 * Data model for a step in the order tracking progress timeline.
 */
data class OrderTrackingStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val description: String,
    val defaultTime: String = ""
)

/**
 * Standard 4-step progress definition matching 'Ordered', 'Preparing', 'On Delivery', and 'Delivered'.
 */
val defaultOrderTrackingSteps = listOf(
    OrderTrackingStep(
        title = "Ordered",
        subtitle = "Pesanan Dibuat",
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        description = "Pesanan telah diterima oleh sistem PasarDesa dan diteruskan ke warung/penjual.",
        defaultTime = "14:20 WIB"
    ),
    OrderTrackingStep(
        title = "Preparing",
        subtitle = "Sedang Disiapkan",
        icon = Icons.Default.Inventory2,
        description = "Penjual UMKM sedang mengemas dan menyiapkan pesanan dengan teliti.",
        defaultTime = "14:25 WIB"
    ),
    OrderTrackingStep(
        title = "On Delivery",
        subtitle = "Sedang Diantar",
        icon = Icons.Default.DeliveryDining,
        description = "Kurir Lokal Desa membawa pesanan dan menuju ke alamat tujuan pengantaran.",
        defaultTime = "14:38 WIB"
    ),
    OrderTrackingStep(
        title = "Delivered",
        subtitle = "Pesanan Selesai",
        icon = Icons.Default.CheckCircle,
        description = "Paket telah sampai di tujuan dan diterima dengan baik oleh pembeli.",
        defaultTime = "14:52 WIB"
    )
)

/**
 * UI Component that displays a step-by-step progress tracker for a selected order,
 * showing statuses: 'Ordered', 'Preparing', 'On Delivery', and 'Delivered'.
 *
 * @param currentStep 0 for 'Ordered', 1 for 'Preparing', 2 for 'On Delivery', 3 for 'Delivered'
 * @param steps List of steps to display (defaults to standard 4 steps)
 * @param courierInfo Optional info about the courier for the 'On Delivery' step
 */
@Composable
fun OrderStatusProgressTracker(
    currentStep: Int,
    modifier: Modifier = Modifier,
    steps: List<OrderTrackingStep> = defaultOrderTrackingSteps,
    courierInfo: String? = null,
    createdAt: String = "",
    showDetailedTimeline: Boolean = true
) {
    var isTimelineExpanded by remember { mutableStateOf(showDetailedTimeline) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_progress_tracker"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Current Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pelacakan Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E201E)
                    )
                    if (createdAt.isNotBlank()) {
                        Text(
                            text = createdAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                val currentStepData = steps.getOrNull(currentStep.coerceIn(0, steps.lastIndex))
                Surface(
                    color = when (currentStep) {
                        3 -> Color(0xFFE8F5E9)
                        2 -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE1F5FE)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = currentStepData?.title ?: "Processing",
                        color = when (currentStep) {
                            3 -> DesaGreenPrimary
                            2 -> ShopeeOrangeSecondary
                            else -> Color(0xFF0277BD)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Horizontal Step Stepper
            HorizontalProgressStepper(
                currentStep = currentStep,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Active Step Highlight Card
            val activeStep = steps.getOrNull(currentStep.coerceIn(0, steps.lastIndex))
            if (activeStep != null) {
                Surface(
                    color = when (currentStep) {
                        3 -> Color(0xFFF1F8E9)
                        2 -> Color(0xFFFFF8E1)
                        else -> Color(0xFFF0F7FF)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = when (currentStep) {
                                        3 -> DesaGreenPrimary
                                        2 -> ShopeeOrangeSecondary
                                        else -> Color(0xFF0288D1)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = activeStep.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Status Saat Ini: ${activeStep.title} (${activeStep.subtitle})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = if (currentStep == 2 && !courierInfo.isNullOrBlank()) {
                                    "$courierInfo sedang mengantar pesanan Anda."
                                } else {
                                    activeStep.description
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF555555),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Expandable Detailed Vertical Timeline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTimelineExpanded = !isTimelineExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rincian Riwayat Pengantaran",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Icon(
                    imageVector = if (isTimelineExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Timeline",
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = isTimelineExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    steps.forEachIndexed { index, step ->
                        VerticalTimelineStepItem(
                            stepIndex = index,
                            step = step,
                            isPassed = index < currentStep,
                            isCurrent = index == currentStep,
                            isLast = index == steps.lastIndex,
                            customCourierNote = if (index == 2) courierInfo else null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Overload of OrderStatusProgressTracker that directly accepts the app's Order model.
 */
@Composable
fun OrderStatusProgressTracker(
    order: Order,
    modifier: Modifier = Modifier
) {
    val step = when (order.status) {
        OrderStatus.BELUM_BAYAR -> 0
        OrderStatus.DIKEMAS -> 1
        OrderStatus.DIKIRIM -> 2
        OrderStatus.SELESAI -> 3
        OrderStatus.DIBATALKAN -> 0
    }

    OrderStatusProgressTracker(
        currentStep = step,
        courierInfo = order.courierName,
        createdAt = order.createdAt,
        modifier = modifier
    )
}

/**
 * Horizontal step stepper bar showing step nodes and connecting progress lines.
 */
@Composable
fun HorizontalProgressStepper(
    currentStep: Int,
    steps: List<OrderTrackingStep>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(modifier = modifier) {
        // Row of Nodes & Connecting Lines
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep
                val isPending = index > currentStep

                val nodeBgColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> DesaGreenPrimary
                        isCurrent -> ShopeeOrangeSecondary
                        else -> Color(0xFFE0E0E0)
                    },
                    label = "node_bg"
                )

                // Step Node Indicator
                Box(
                    modifier = Modifier
                        .testTag("step_indicator_$index")
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCurrent) {
                        // Outer animated halo
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .scale(pulseScale)
                                .background(ShopeeOrangeSecondary.copy(alpha = 0.25f), CircleShape)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(nodeBgColor, CircleShape)
                            .then(
                                if (isPending) Modifier.border(1.5.dp, Color(0xFFBDBDBD), CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isCompleted -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            isCurrent -> {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Text(
                                    text = "${index + 1}",
                                    color = Color(0xFF757575),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Connecting Line between Nodes (except last node)
                if (index < steps.lastIndex) {
                    val linePassed = index < currentStep
                    val lineColor by animateColorAsState(
                        targetValue = if (linePassed) DesaGreenPrimary else Color(0xFFE0E0E0),
                        label = "line_color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(lineColor, RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row of Labels below nodes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep

                Text(
                    text = step.title,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> ShopeeOrangeSecondary
                        isCompleted -> DesaGreenPrimary
                        else -> Color.Gray
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(68.dp)
                )
            }
        }
    }
}

/**
 * Vertical timeline item showing detailed descriptions and timestamps for each step.
 */
@Composable
fun VerticalTimelineStepItem(
    stepIndex: Int,
    step: OrderTrackingStep,
    isPassed: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    customCourierNote: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        // Vertical indicator column (Node + vertical line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        color = when {
                            isPassed -> DesaGreenPrimary
                            isCurrent -> ShopeeOrangeSecondary
                            else -> Color(0xFFE0E0E0)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPassed) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(44.dp)
                        .background(if (isPassed) DesaGreenPrimary else Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Step text details
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${step.title} • ${step.subtitle}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = when {
                        isCurrent -> ShopeeOrangeSecondary
                        isPassed -> Color(0xFF1E201E)
                        else -> Color.Gray
                    }
                )

                if (isPassed || isCurrent) {
                    Text(
                        text = step.defaultTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = if (stepIndex == 2 && !customCourierNote.isNullOrBlank()) {
                    "Kurir: $customCourierNote sedang membawa pesanan Anda."
                } else {
                    step.description
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrent || isPassed) Color(0xFF555555) else Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
