package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.PaymentMethod
import com.example.ui.components.ExternalCourierConnectorModal
import com.example.ui.components.PasarDesaTopBar
import com.example.ui.components.QrisPaymentModal
import com.example.ui.components.TopUpWalletModal
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KurirDesaScreen
import com.example.ui.screens.MitraUmkmScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.PastOrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileWalletScreen
import com.example.ui.theme.DesaGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ShopeeOrangeSecondary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.PasarDesaViewModel
import com.example.ui.viewmodel.SubScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PasarDesaApp()
            }
        }
    }
}

@Composable
fun PasarDesaApp(viewModel: PasarDesaViewModel = viewModel()) {
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsState()
    val currentSubScreen by viewModel.currentSubScreen.collectAsState()
    val products by viewModel.products.collectAsState()
    val allProducts by viewModel.allProductsRaw.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount by viewModel.cartTotalCount.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val courierBookings by viewModel.courierBookings.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val walletTransactions by viewModel.walletTransactions.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val myStoreName by viewModel.myStoreName.collectAsState()
    val selectedShipping by viewModel.selectedShippingMethod.collectAsState()
    val selectedPayment by viewModel.selectedPaymentMethod.collectAsState()
    val buyerNote by viewModel.buyerNote.collectAsState()

    var showExternalCourierModal by remember { mutableStateOf(false) }

    // Subscreen routing
    when (val screen = currentSubScreen) {
        is SubScreen.ProductDetail -> {
            ProductDetailScreen(
                product = screen.product,
                onBackClick = { viewModel.navigateBack() },
                onAddToCart = { variant, qty ->
                    viewModel.addToCart(screen.product, variant, qty)
                },
                onBuyNow = { variant, qty ->
                    viewModel.addToCart(screen.product, variant, qty)
                    viewModel.navigateTo(SubScreen.Checkout)
                },
                onChatSeller = { phone, msg ->
                    viewModel.contactCourierViaWhatsApp(context, phone, msg)
                },
                onOpenCart = { viewModel.navigateTo(SubScreen.Cart) }
            )
        }

        is SubScreen.Cart -> {
            CartScreen(
                cartItems = cartItems,
                onBackClick = { viewModel.navigateBack() },
                onToggleSelect = { viewModel.toggleCartSelection(it) },
                onToggleSelectAll = { viewModel.toggleSelectAll(it) },
                onQuantityChange = { id, delta -> viewModel.updateCartQuantity(id, delta) },
                onRemoveItem = { viewModel.removeCartItem(it) },
                onCheckoutClick = { viewModel.proceedToCheckout() }
            )
        }

        is SubScreen.Checkout -> {
            CheckoutScreen(
                items = cartItems,
                address = currentAddress,
                selectedShipping = selectedShipping,
                onShippingSelected = { viewModel.setShippingMethod(it) },
                selectedPayment = selectedPayment,
                onPaymentSelected = { viewModel.setPaymentMethod(it) },
                buyerNote = buyerNote,
                onBuyerNoteChange = { viewModel.setBuyerNote(it) },
                onBackClick = { viewModel.navigateBack() },
                onOpenExternalCourierDialog = { showExternalCourierModal = true },
                onConfirmOrder = { viewModel.confirmOrder() }
            )
        }

        is SubScreen.OrderDetail -> {
            OrderDetailScreen(
                order = screen.order,
                onBackClick = {
                    viewModel.navigateBack()
                    viewModel.setTab(MainTab.PESANAN)
                },
                onContactCourierWA = { phone, msg ->
                    viewModel.contactCourierViaWhatsApp(context, phone, msg)
                },
                onCallCourier = { phone ->
                    viewModel.callCourier(context, phone)
                },
                onOpenGojek = {
                    viewModel.openGojekApp(context, "", "")
                },
                onConfirmComplete = { orderId ->
                    viewModel.completeOrder(orderId)
                }
            )
        }

        is SubScreen.QrisPayment -> {
            QrisPaymentModal(
                amount = screen.amount,
                orderId = screen.orderId,
                onDismiss = {
                    viewModel.navigateBack()
                    viewModel.setTab(MainTab.PESANAN)
                },
                onConfirmPaid = {
                    viewModel.confirmQrisPayment(screen.orderId)
                }
            )
        }

        is SubScreen.TopUpWallet -> {
            TopUpWalletModal(
                onDismiss = { viewModel.navigateBack() },
                onTopUpSuccess = { amount -> viewModel.topUpWallet(amount) }
            )
        }

        is SubScreen.PastOrders -> {
            PastOrdersScreen(
                onBackClick = { viewModel.navigateBack() }
            )
        }

        is SubScreen.None, is SubScreen.AddProduct, is SubScreen.ExternalCourierDialog -> {
            // Main Bottom Tabbed Interface
            Scaffold(
                topBar = {
                    if (currentTab == MainTab.BERANDA) {
                        PasarDesaTopBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            cartCount = cartCount,
                            onCartClick = { viewModel.navigateTo(SubScreen.Cart) },
                            onWalletClick = { viewModel.navigateTo(SubScreen.TopUpWallet) },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("main_bottom_nav")
                    ) {
                        // 1. Beranda
                        NavigationBarItem(
                            selected = currentTab == MainTab.BERANDA,
                            onClick = { viewModel.setTab(MainTab.BERANDA) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.BERANDA) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                    contentDescription = "Beranda"
                                )
                            },
                            label = { Text("Beranda", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.BERANDA) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DesaGreenPrimary,
                                selectedTextColor = DesaGreenPrimary,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )

                        // 2. Kurir Desa
                        NavigationBarItem(
                            selected = currentTab == MainTab.KURIR_DESA,
                            onClick = { viewModel.setTab(MainTab.KURIR_DESA) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.KURIR_DESA) Icons.Filled.DeliveryDining else Icons.Outlined.DeliveryDining,
                                    contentDescription = "Kurir Desa"
                                )
                            },
                            label = { Text("Kurir Desa", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.KURIR_DESA) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DesaGreenPrimary,
                                selectedTextColor = DesaGreenPrimary,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )

                        // 3. Toko UMKM
                        NavigationBarItem(
                            selected = currentTab == MainTab.TOKO_SAYA,
                            onClick = { viewModel.setTab(MainTab.TOKO_SAYA) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.TOKO_SAYA) Icons.Filled.Store else Icons.Outlined.Store,
                                    contentDescription = "Toko UMKM"
                                )
                            },
                            label = { Text("Toko UMKM", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.TOKO_SAYA) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ShopeeOrangeSecondary,
                                selectedTextColor = ShopeeOrangeSecondary,
                                indicatorColor = Color(0xFFFFF3E0)
                            )
                        )

                        // 4. Pesanan
                        NavigationBarItem(
                            selected = currentTab == MainTab.PESANAN,
                            onClick = { viewModel.setTab(MainTab.PESANAN) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        val activeOrders = orders.count { it.status == com.example.data.model.OrderStatus.DIKIRIM || it.status == com.example.data.model.OrderStatus.BELUM_BAYAR }
                                        if (activeOrders > 0) {
                                            Badge(containerColor = ShopeeOrangeSecondary) {
                                                Text("$activeOrders", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.PESANAN) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                        contentDescription = "Pesanan"
                                    )
                                }
                            },
                            label = { Text("Pesanan", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.PESANAN) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DesaGreenPrimary,
                                selectedTextColor = DesaGreenPrimary,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )

                        // 5. Saya & Dompet
                        NavigationBarItem(
                            selected = currentTab == MainTab.SAYA,
                            onClick = { viewModel.setTab(MainTab.SAYA) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.SAYA) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                    contentDescription = "Saya"
                                )
                            },
                            label = { Text("Dompet", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.SAYA) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DesaGreenPrimary,
                                selectedTextColor = DesaGreenPrimary,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainTab.BERANDA -> {
                            HomeScreen(
                                products = products,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                walletBalance = walletBalance,
                                onProductClick = { viewModel.navigateTo(SubScreen.ProductDetail(it)) },
                                onAddToCartClick = { viewModel.addToCart(it) },
                                onTopUpClick = { viewModel.navigateTo(SubScreen.TopUpWallet) },
                                onQrisClick = { viewModel.navigateTo(SubScreen.QrisPayment(50000L, "QRIS-SCAN")) },
                                onCourierBannerClick = { viewModel.setTab(MainTab.KURIR_DESA) }
                            )
                        }

                        MainTab.KURIR_DESA -> {
                            KurirDesaScreen(
                                courierBookings = courierBookings,
                                onBookCourier = { type, sender, senderAddr, recip, recipAddr, desc, fleet, pay, fee, notes ->
                                    viewModel.bookCourier(type, sender, senderAddr, recip, recipAddr, desc, fleet, pay, fee, notes)
                                },
                                onOpenGojek = { viewModel.openGojekApp(context, "", "") },
                                onOpenGrab = { viewModel.openGrabApp(context) },
                                onContactCourierWhatsApp = { phone, msg -> viewModel.contactCourierViaWhatsApp(context, phone, msg) },
                                onCallCourier = { phone -> viewModel.callCourier(context, phone) }
                            )
                        }

                        MainTab.TOKO_SAYA -> {
                            MitraUmkmScreen(
                                storeName = myStoreName,
                                products = allProducts,
                                onAddNewProduct = { title, cat, price, orig, stock, desc, vars, cod, badge ->
                                    viewModel.addSellerProduct(title, cat, price, orig, stock, desc, vars, cod, badge)
                                },
                                onDeleteProduct = { viewModel.deleteProduct(it) },
                                onUpdateStock = { id, stock -> viewModel.updateProductStock(id, stock) }
                            )
                        }

                        MainTab.PESANAN -> {
                            OrderHistoryScreen(
                                orders = orders,
                                onOrderClick = { viewModel.navigateTo(SubScreen.OrderDetail(it)) },
                                onContactCourier = { phone, msg -> viewModel.contactCourierViaWhatsApp(context, phone, msg) },
                                onConfirmComplete = { viewModel.completeOrder(it) },
                                onViewPastOrders = { viewModel.navigateTo(SubScreen.PastOrders) }
                            )
                        }

                        MainTab.SAYA -> {
                            ProfileWalletScreen(
                                walletBalance = walletBalance,
                                transactions = walletTransactions,
                                address = currentAddress,
                                onTopUpClick = { viewModel.navigateTo(SubScreen.TopUpWallet) },
                                onQrisClick = { viewModel.navigateTo(SubScreen.QrisPayment(50000L, "QRIS-TOPUP")) },
                                onSwitchToSellerMode = { viewModel.setTab(MainTab.TOKO_SAYA) }
                            )
                        }
                    }
                }
            }
        }
    }

    // External Courier Dialog (Gojek / Grab)
    if (showExternalCourierModal) {
        ExternalCourierConnectorModal(
            onDismiss = { showExternalCourierModal = false },
            onOpenGojek = {
                showExternalCourierModal = false
                viewModel.openGojekApp(context, "", "")
            },
            onOpenGrab = {
                showExternalCourierModal = false
                viewModel.openGrabApp(context)
            }
        )
    }
}
