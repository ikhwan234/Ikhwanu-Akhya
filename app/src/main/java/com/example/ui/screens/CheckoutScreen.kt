package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CartItem
import com.example.data.model.PaymentMethod
import com.example.data.model.ShippingMethod
import com.example.data.model.VillageAddress
import com.example.ui.theme.DesaGreenPrimary
import com.example.ui.theme.GojekGreen
import com.example.ui.theme.ShopeeOrangeSecondary
import com.example.ui.viewmodel.CartViewModel

/**
 * Top-level payment category options requested by the user:
 * COD (Cash on Delivery) vs Digital Payment (QRIS, E-Wallet, Bank Transfer).
 */
enum class PaymentTypeSelection(val title: String, val subtitle: String) {
    COD(
        title = "COD (Bayar di Tempat)",
        subtitle = "Bayar tunai ke Kurir Desa saat pesanan sampai di rumah"
    ),
    DIGITAL(
        title = "Pembayaran Digital",
        subtitle = "Scan QRIS, Saldo DompetDesa, atau Transfer Bank Otomatis"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    address: VillageAddress,
    selectedShipping: ShippingMethod,
    onShippingSelected: (ShippingMethod) -> Unit,
    selectedPayment: PaymentMethod,
    onPaymentSelected: (PaymentMethod) -> Unit,
    buyerNote: String,
    onBuyerNoteChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onOpenExternalCourierDialog: () -> Unit,
    onConfirmOrder: () -> Unit,
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = viewModel()
) {
    BackHandler { onBackClick() }

    // Observe cart state from ViewModel if available, or fall back to items parameter
    val cartUiState by cartViewModel.uiState.collectAsState()
    val activeItems = if (items.isNotEmpty()) items.filter { it.isSelected } else cartUiState.items.filter { it.isSelected }

    val subtotal = activeItems.sumOf { it.totalPrice }
    val voucherDiscount = if (activeItems.isNotEmpty()) 5000L else 0L
    val villageKasFee = if (activeItems.isNotEmpty()) 1000L else 0L
    val totalPayment = (subtotal + selectedShipping.cost + villageKasFee - voucherDiscount).coerceAtLeast(0L)

    // Manage high-level selection between COD and DIGITAL PAYMENT
    var paymentType by remember(selectedPayment) {
        mutableStateOf(
            if (selectedPayment == PaymentMethod.COD) PaymentTypeSelection.COD else PaymentTypeSelection.DIGITAL
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Bar with Total & Confirm Order Button
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Rp %,d".format(totalPayment).replace(',', '.'),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShopeeOrangeSecondary
                        )
                    }

                    // Confirm Order button that clears the cart via the ViewModel
                    Button(
                        onClick = {
                            // Clear cart items via the existing CartViewModel as requested
                            cartViewModel.clearSelectedItems()
                            onConfirmOrder()
                        },
                        enabled = activeItems.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShopeeOrangeSecondary),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("confirm_order_button")
                    ) {
                        Text(
                            text = "Confirm Order",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("checkout_screen")
    ) { innerPadding ->
        if (activeItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tidak Ada Barang untuk Checkout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DesaGreenPrimary)
                    ) {
                        Text("Kembali Belanja")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F8F5))
                    .padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Alamat Pengiriman
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = DesaGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Alamat Pengiriman Desa",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${address.recipientName} (${address.phoneNumber})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${address.dusunName}, Desa ${address.villageName}, Kec. ${address.subdistrictName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF424242)
                            )
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "Patokan: ${address.landmark}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DesaGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Ringkasan Pesanan (Order Summary)
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_summary_section")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = DesaGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ringkasan Pesanan (${activeItems.size} Produk)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Items List loaded with Coil
                            activeItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(item.product.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.product.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF0F0F0))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.product.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (item.selectedVariant.isNotBlank()) {
                                            Text(
                                                text = "Varian: ${item.selectedVariant}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = "${item.quantity}x @ ${item.product.formattedPrice()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Text(
                                        text = item.formattedTotalPrice(),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = buyerNote,
                                onValueChange = onBuyerNoteChange,
                                placeholder = { Text("Pesan untuk penjual / kurir desa...", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("checkout_buyer_note_input")
                            )
                        }
                    }
                }

                // 3. Pilihan Jasa Pengiriman
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = DesaGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Opsi Pengiriman",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = GojekGreen.copy(alpha = 0.12f),
                                    modifier = Modifier.clickable { onOpenExternalCourierDialog() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = null,
                                            tint = GojekGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Konek Gojek/Grab",
                                            color = GojekGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            ShippingMethod.values().forEach { method ->
                                val isSelected = method == selectedShipping
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onShippingSelected(method) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFFF1F8E9) else Color(0xFFFAFAFA),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, DesaGreenPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onShippingSelected(method) },
                                            colors = RadioButtonDefaults.colors(selectedColor = DesaGreenPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = method.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = if (method == ShippingMethod.KURIR_LOKAL) DesaGreenPrimary else ShopeeOrangeSecondary,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = method.badge,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${method.subtitle} • Estimasi: ${method.estimatedTime}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = if (method.cost == 0L) "GRATIS" else "Rp %,d".format(method.cost).replace(',', '.'),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (method.cost == 0L) DesaGreenPrimary else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Pilihan Metode Pembayaran (COD vs DIGITAL PAYMENT)
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalAtm,
                                    contentDescription = null,
                                    tint = ShopeeOrangeSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pilih Metode Pembayaran",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // OPTION 1: COD (Bayar di Tempat)
                            val isCodSelected = paymentType == PaymentTypeSelection.COD
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        paymentType = PaymentTypeSelection.COD
                                        onPaymentSelected(PaymentMethod.COD)
                                    }
                                    .testTag("payment_option_cod"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCodSelected) Color(0xFFFFF3E0) else Color(0xFFFAFAFA),
                                border = if (isCodSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ShopeeOrangeSecondary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isCodSelected,
                                        onClick = {
                                            paymentType = PaymentTypeSelection.COD
                                            onPaymentSelected(PaymentMethod.COD)
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = ShopeeOrangeSecondary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(ShopeeOrangeSecondary.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalAtm,
                                            contentDescription = null,
                                            tint = ShopeeOrangeSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "COD (Cash on Delivery)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCodSelected) Color(0xFFBF360C) else Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = ShopeeOrangeSecondary,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Bayar di Tempat",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Bayar tunai ke Kurir Desa saat pesanan tiba",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // OPTION 2: DIGITAL PAYMENT
                            val isDigitalSelected = paymentType == PaymentTypeSelection.DIGITAL
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        paymentType = PaymentTypeSelection.DIGITAL
                                        if (selectedPayment == PaymentMethod.COD) {
                                            onPaymentSelected(PaymentMethod.QRIS)
                                        }
                                    }
                                    .testTag("payment_option_digital"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDigitalSelected) Color(0xFFE8F5E9) else Color(0xFFFAFAFA),
                                border = if (isDigitalSelected) androidx.compose.foundation.BorderStroke(1.5.dp, DesaGreenPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isDigitalSelected,
                                            onClick = {
                                                paymentType = PaymentTypeSelection.DIGITAL
                                                if (selectedPayment == PaymentMethod.COD) {
                                                    onPaymentSelected(PaymentMethod.QRIS)
                                                }
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = DesaGreenPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(DesaGreenPrimary.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = null,
                                                tint = DesaGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Digital Payment",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDigitalSelected) Color(0xFF1B5E20) else Color.Black
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = DesaGreenPrimary,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Instan & Bebas Biaya",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Scan QRIS (GoPay/ShopeePay/DANA/OVO/BCA) atau Saldo Desa",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Sub-options when Digital Payment is chosen
                                    AnimatedVisibility(visible = isDigitalSelected) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp, start = 12.dp, end = 4.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            HorizontalDivider(color = Color(0xFFE0E0E0))

                                            val digitalOptions = listOf(
                                                PaymentMethod.QRIS,
                                                PaymentMethod.DOMPET_DESA,
                                                PaymentMethod.TRANSFER_BANK
                                            )

                                            digitalOptions.forEach { method ->
                                                val isSubSelected = selectedPayment == method
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { onPaymentSelected(method) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (isSubSelected) Color(0xFFCEF5D5) else Color.Transparent
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = when (method) {
                                                                PaymentMethod.QRIS -> Icons.Default.QrCodeScanner
                                                                PaymentMethod.DOMPET_DESA -> Icons.Default.AccountBalanceWallet
                                                                else -> Icons.Default.AccountBalance
                                                            },
                                                            contentDescription = null,
                                                            tint = if (isSubSelected) DesaGreenPrimary else Color.Gray,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = method.title,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isSubSelected) Color(0xFF003917) else Color.DarkGray
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Rincian Biaya (Breakdown)
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Rincian Pembayaran",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal Produk (${activeItems.size} barang)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("Rp %,d".format(subtotal).replace(',', '.'), style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ongkos Kirim", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("Rp %,d".format(selectedShipping.cost).replace(',', '.'), style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biaya Layanan & Kas Desa", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("Rp %,d".format(villageKasFee).replace(',', '.'), style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Voucher Diskon Desa", style = MaterialTheme.typography.bodySmall, color = DesaGreenPrimary)
                                Text(
                                    "-Rp %,d".format(voucherDiscount).replace(',', '.'),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DesaGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Tagihan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    "Rp %,d".format(totalPayment).replace(',', '.'),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ShopeeOrangeSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
