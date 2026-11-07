package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DesaGreenPrimary
import com.example.ui.theme.ShopeeOrangeSecondary

/**
 * Model representing a past order entry.
 */
data class PastOrderEntry(
    val orderId: String,
    val date: String,
    val totalAmount: Long,
    val status: String,
    val itemsSummary: String,
    val storeName: String,
    val paymentMethod: String,
    val isCod: Boolean = false
) {
    val formattedTotal: String
        get() = "Rp %,d".format(totalAmount).replace(',', '.')
}

/**
 * Local mock dataset of past orders for the village marketplace.
 */
val defaultMockPastOrders = listOf(
    PastOrderEntry(
        orderId = "ORD-DS-20261101",
        date = "06 Nov 2026, 14:20",
        totalAmount = 35000L,
        status = "Sedang Diantar",
        itemsSummary = "1x Nasi Liwet Kastrol Komplit Ayam Kampung",
        storeName = "Warung Makan Kang Asep",
        paymentMethod = "COD (Bayar di Tempat)",
        isCod = true
    ),
    PastOrderEntry(
        orderId = "ORD-DS-20261028",
        date = "28 Okt 2026, 10:15",
        totalAmount = 145000L,
        status = "Selesai",
        itemsSummary = "1x Batik Tulis Motif Mega Lereng, 2x Sambal Bawang",
        storeName = "Batik Berkah Bu Nur",
        paymentMethod = "QRIS Desa (Digital)",
        isCod = false
    ),
    PastOrderEntry(
        orderId = "ORD-DS-20261022",
        date = "22 Okt 2026, 16:40",
        totalAmount = 72000L,
        status = "Selesai",
        itemsSummary = "1x Beras Pandan Wangi Organik 5 Kg",
        storeName = "Koperasi Tani Makmur",
        paymentMethod = "COD (Bayar di Tempat)",
        isCod = true
    ),
    PastOrderEntry(
        orderId = "ORD-DS-20261015",
        date = "15 Okt 2026, 08:30",
        totalAmount = 30000L,
        status = "Selesai",
        itemsSummary = "2x Keripik Singkong Gurih Balado",
        storeName = "Camilan Berkah Bu Sri",
        paymentMethod = "Saldo DompetDesa (Digital)",
        isCod = false
    ),
    PastOrderEntry(
        orderId = "ORD-DS-20261009",
        date = "09 Okt 2026, 11:05",
        totalAmount = 48000L,
        status = "Selesai",
        itemsSummary = "1x Cobek Batu Kali Asli Lereng Gunung",
        storeName = "Pahat Batu Alam Pak Joko",
        paymentMethod = "QRIS Desa (Digital)",
        isCod = false
    ),
    PastOrderEntry(
        orderId = "ORD-DS-20261002",
        date = "02 Okt 2026, 15:50",
        totalAmount = 10000L,
        status = "Dibatalkan",
        itemsSummary = "1x Jasa Antar Paket Antar Dusun",
        storeName = "Posko Ojek Karang Taruna",
        paymentMethod = "COD (Bayar di Tempat)",
        isCod = true
    )
)

/**
 * A simple screen that displays a list of past orders using a local list of mock data,
 * showing the date, total amount, and order status for each entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastOrdersScreen(
    modifier: Modifier = Modifier,
    orders: List<PastOrderEntry> = defaultMockPastOrders,
    onBackClick: (() -> Unit)? = null,
    onOrderClick: (PastOrderEntry) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        modifier = modifier.testTag("past_orders_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8F5))
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders, key = { it.orderId }) { order ->
                PastOrderCard(
                    order = order,
                    onClick = { onOrderClick(order) },
                    modifier = Modifier.testTag("past_order_card_${order.orderId}")
                )
            }
        }
    }
}

/**
 * Individual card item displaying order details:
 * Date, Total Amount, Order Status, and summary.
 */
@Composable
fun PastOrderCard(
    order: PastOrderEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Order ID, Date, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = order.orderId,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E201E)
                        )
                        if (order.isCod) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "COD",
                                    color = ShopeeOrangeSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Order Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                // Order Status Badge
                OrderStatusBadge(status = order.status)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            // Store Name & Items Summary
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = DesaGreenPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.storeName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF555555),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Payment Method and Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.paymentMethod,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                // Total Amount
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = order.formattedTotal,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShopeeOrangeSecondary
                    )
                }
            }
        }
    }
}

/**
 * Status indicator badge with contextual background and text colors.
 */
@Composable
fun OrderStatusBadge(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "Selesai" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Sedang Diantar" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.DeliveryDining)
        "Dikemas" -> Triple(Color(0xFFE1F5FE), Color(0xFF0277BD), Icons.Default.Schedule)
        "Dibatalkan" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Schedule)
        else -> Triple(Color(0xFFF5F5F5), Color.DarkGray, Icons.Default.Schedule)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
