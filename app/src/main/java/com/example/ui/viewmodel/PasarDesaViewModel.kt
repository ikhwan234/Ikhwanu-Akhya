package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.CourierBooking
import com.example.data.model.CourierFleetType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ShippingMethod
import com.example.data.model.VillageAddress
import com.example.data.model.WalletTransaction
import com.example.data.repository.PasarDesaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    BERANDA,
    KURIR_DESA,
    TOKO_SAYA,
    PESANAN,
    SAYA
}

sealed class SubScreen {
    object None : SubScreen()
    data class ProductDetail(val product: Product) : SubScreen()
    object Cart : SubScreen()
    object Checkout : SubScreen()
    data class OrderDetail(val order: Order) : SubScreen()
    object AddProduct : SubScreen()
    data class QrisPayment(val amount: Long, val orderId: String) : SubScreen()
    object TopUpWallet : SubScreen()
    object PastOrders : SubScreen()
    data class ExternalCourierDialog(val providerName: String, val deeplink: String) : SubScreen()
}

class PasarDesaViewModel : ViewModel() {

    private val repository = PasarDesaRepository

    // Navigation state
    private val _currentTab = MutableStateFlow(MainTab.BERANDA)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _currentSubScreen = MutableStateFlow<SubScreen>(SubScreen.None)
    val currentSubScreen: StateFlow<SubScreen> = _currentSubScreen.asStateFlow()

    // Filters & Search
    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Checkout preferences
    private val _selectedShippingMethod = MutableStateFlow(ShippingMethod.KURIR_LOKAL)
    val selectedShippingMethod: StateFlow<ShippingMethod> = _selectedShippingMethod.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.COD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _buyerNote = MutableStateFlow("")
    val buyerNote: StateFlow<String> = _buyerNote.asStateFlow()

    private val _selectedVoucherCode = MutableStateFlow("VOUCHER_DESA_5K")
    val selectedVoucherCode: StateFlow<String> = _selectedVoucherCode.asStateFlow()

    // Products Flow with filtering
    val products: StateFlow<List<Product>> = combine(
        repository.products,
        _selectedCategory,
        _searchQuery
    ) { allProducts, category, query ->
        allProducts.filter { product ->
            val matchCategory = category == ProductCategory.ALL || product.category == category
            val matchQuery = query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.storeName.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsRaw: StateFlow<List<Product>> = repository.products
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val orders: StateFlow<List<Order>> = repository.orders
    val courierBookings: StateFlow<List<CourierBooking>> = repository.courierBookings
    val walletBalance: StateFlow<Long> = repository.walletBalance
    val walletTransactions: StateFlow<List<WalletTransaction>> = repository.walletTransactions
    val currentAddress: StateFlow<VillageAddress> = repository.currentAddress
    val myStoreName: StateFlow<String> = repository.myStoreName

    val cartTotalCount: StateFlow<Int> = repository.cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val selectedCartSubtotal: StateFlow<Long> = repository.cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
        items.filter { it.isSelected }.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        _currentSubScreen.value = SubScreen.None
    }

    fun navigateTo(subScreen: SubScreen) {
        _currentSubScreen.value = subScreen
    }

    fun navigateBack(): Boolean {
        return if (_currentSubScreen.value != SubScreen.None) {
            _currentSubScreen.value = SubScreen.None
            true
        } else {
            false
        }
    }

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShippingMethod(method: ShippingMethod) {
        _selectedShippingMethod.value = method
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun setBuyerNote(note: String) {
        _buyerNote.value = note
    }

    // Cart actions
    fun addToCart(product: Product, selectedVariant: String = "", qty: Int = 1) {
        val variantToUse = if (selectedVariant.isBlank() && product.variants.isNotEmpty()) {
            product.variants.first().options.firstOrNull() ?: ""
        } else selectedVariant

        repository.addToCart(product, variantToUse, qty)
    }

    fun updateCartQuantity(cartId: String, delta: Int) {
        repository.updateCartQuantity(cartId, delta)
    }

    fun toggleCartSelection(cartId: String) {
        repository.toggleCartSelection(cartId)
    }

    fun toggleSelectAll(select: Boolean) {
        repository.toggleSelectAll(select)
    }

    fun removeCartItem(cartId: String) {
        repository.removeFromCart(cartId)
    }

    // Checkout creation
    fun proceedToCheckout() {
        val hasSelected = cartItems.value.any { it.isSelected }
        if (hasSelected) {
            navigateTo(SubScreen.Checkout)
        }
    }

    fun confirmOrder(): Order {
        val discount = if (_selectedVoucherCode.value.isNotBlank()) 5000L else 0L
        val order = repository.createOrder(
            shippingMethod = _selectedShippingMethod.value,
            paymentMethod = _selectedPaymentMethod.value,
            buyerNote = _buyerNote.value,
            voucherDiscount = discount
        )

        if (_selectedPaymentMethod.value == PaymentMethod.QRIS) {
            navigateTo(SubScreen.QrisPayment(order.totalAmount, order.orderId))
        } else {
            navigateTo(SubScreen.OrderDetail(order))
        }
        return order
    }

    fun confirmQrisPayment(orderId: String) {
        repository.updateOrderStatus(orderId, OrderStatus.DIKEMAS)
        val order = orders.value.firstOrNull { it.orderId == orderId }
        if (order != null) {
            navigateTo(SubScreen.OrderDetail(order.copy(status = OrderStatus.DIKEMAS)))
        } else {
            navigateTo(SubScreen.None)
            setTab(MainTab.PESANAN)
        }
    }

    fun bookCourier(
        serviceType: String,
        senderName: String,
        senderAddress: String,
        recipientName: String,
        recipientAddress: String,
        itemDesc: String,
        fleet: CourierFleetType,
        payment: PaymentMethod,
        fee: Long,
        notes: String
    ): CourierBooking {
        return repository.bookCourier(
            serviceType = serviceType,
            senderName = senderName,
            senderAddress = senderAddress,
            recipientName = recipientName,
            recipientAddress = recipientAddress,
            itemDescription = itemDesc,
            fleetType = fleet,
            paymentMethod = payment,
            fee = fee,
            notes = notes
        )
    }

    fun topUpWallet(amount: Long) {
        repository.topUpWallet(amount)
        _currentSubScreen.value = SubScreen.None
    }

    fun addSellerProduct(
        title: String,
        category: ProductCategory,
        price: Long,
        originalPrice: Long?,
        stock: Int,
        desc: String,
        variants: List<String>,
        isCod: Boolean,
        badge: String
    ) {
        repository.addNewProduct(
            title = title,
            category = category,
            price = price,
            originalPrice = originalPrice,
            stock = stock,
            description = desc,
            variantsList = variants,
            isCod = isCod,
            badgeText = badge
        )
        _currentSubScreen.value = SubScreen.None
    }

    fun updateProductStock(id: String, stock: Int) {
        repository.updateProductStock(id, stock)
    }

    fun deleteProduct(id: String) {
        repository.deleteProduct(id)
    }

    fun cancelOrder(orderId: String) {
        repository.updateOrderStatus(orderId, OrderStatus.DIBATALKAN)
    }

    fun completeOrder(orderId: String) {
        repository.updateOrderStatus(orderId, OrderStatus.SELESAI)
    }

    // EXTERNAL INTENTS (Gojek / Grab / WhatsApp / Telepon)
    fun openGojekApp(context: Context, pickupAddress: String, dropoffAddress: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.gojek.app")
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                Toast.makeText(context, "Membuka Aplikasi Gojek untuk kurir...", Toast.LENGTH_SHORT).show()
            } else {
                // Fallback to Web/Play Store
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.gojek.app")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                context.startActivity(playStoreIntent)
                Toast.makeText(context, "Aplikasi Gojek belum terpasang, mengarahkan ke Play Store", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gojek.com/gosend/")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    fun openGrabApp(context: Context) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.grabtaxi.passenger")
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                Toast.makeText(context, "Membuka Aplikasi Grab...", Toast.LENGTH_SHORT).show()
            } else {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.grabtaxi.passenger")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                context.startActivity(playStoreIntent)
                Toast.makeText(context, "Aplikasi Grab belum terpasang, mengarahkan ke Play Store", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.grab.com/id/express/")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    fun contactCourierViaWhatsApp(context: Context, phone: String, message: String) {
        try {
            val cleanPhone = if (phone.startsWith("0")) "62" + phone.substring(1) else phone
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun callCourier(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memanggil nomor telepon", Toast.LENGTH_SHORT).show()
        }
    }
}
