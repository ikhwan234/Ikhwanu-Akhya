package com.example.data.repository

import com.example.data.cart.CartStateManager
import com.example.data.model.CartItem
import com.example.data.model.CourierBooking
import com.example.data.model.CourierFleetType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductVariant
import com.example.data.model.ShippingMethod
import com.example.data.model.VillageAddress
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PasarDesaRepository {

    private val defaultAddress = VillageAddress(
        recipientName = "Pak Budi Hartono",
        phoneNumber = "081298765432",
        dusunName = "Dusun Krajan RT 03 / RW 02",
        rtRw = "RT 03 / RW 02",
        villageName = "Sukamaju",
        subdistrictName = "Cilaku",
        landmark = "Rumah pagar hijau dekat Pos Ronda RT 03"
    )

    private val sampleProducts = listOf(
        // PAKAIAN & KONVEKSI
        Product(
            id = "prod_1",
            title = "Batik Tulis Motif Mega Lereng Khas Desa",
            storeName = "Batik Berkah Bu Nur",
            storeVillage = "Dusun Krajan, Desa Sukamaju",
            storePhone = "081233445566",
            category = ProductCategory.PAKAIAN,
            price = 95000L,
            originalPrice = 135000L,
            rating = 4.9f,
            soldCount = 84,
            stock = 25,
            imageUrl = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=500&auto=format&fit=crop&q=60",
            description = "Kain batik tulis tradisional hasil karya kelompok ibu-ibu pengrajin desa Sukamaju. Bahan katun primissima adem, warna tahan lama, cocok untuk kemeja pria maupun kebaya wanita.",
            variants = listOf(
                ProductVariant("Ukuran", listOf("M", "L", "XL", "XXL")),
                ProductVariant("Warna Motif", listOf("Coklat Klasik", "Biru Indigo", "Hijau Daun"))
            ),
            isCodSupported = true,
            isFlashSale = true,
            badgeText = "Terlaris Dusun"
        ),
        Product(
            id = "prod_2",
            title = "Kaos Sablon Komunitas Pemuda Desa Katun Combed 30s",
            storeName = "Konveksi Karang Taruna Mandiri",
            storeVillage = "Dusun Mekarsari, Desa Sukamaju",
            storePhone = "081377889900",
            category = ProductCategory.PAKAIAN,
            price = 55000L,
            originalPrice = 70000L,
            rating = 4.8f,
            soldCount = 210,
            stock = 60,
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=60",
            description = "Kaos sablon combed 30s diproduksi langsung oleh pemuda desa. Jahitan rantai rapi, adem menyerap keringat. Tersedia ukuran lengkap.",
            variants = listOf(
                ProductVariant("Ukuran", listOf("S", "M", "L", "XL")),
                ProductVariant("Warna", listOf("Hitam Solid", "Hijau Botol", "Navy"))
            ),
            isCodSupported = true,
            badgeText = "Produk Lokal"
        ),
        Product(
            id = "prod_3",
            title = "Gamis Bordir Halus Melati Ibu PKK",
            storeName = "Modiste Melati Indah",
            storeVillage = "Dusun Babakan, Desa Sukamaju",
            storePhone = "081299887711",
            category = ProductCategory.PAKAIAN,
            price = 120000L,
            originalPrice = 160000L,
            rating = 5.0f,
            soldCount = 47,
            stock = 15,
            imageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=500&auto=format&fit=crop&q=60",
            description = "Gamis syar'i bordir bunga manual khas sentra jahit desa Sukamaju. Bahan jatuh anggun, wudhu friendly, dan berfuring lembut.",
            variants = listOf(
                ProductVariant("Ukuran", listOf("All Size (L-XL)", "Jumbo")),
                ProductVariant("Warna", listOf("Dusty Pink", "Sage Green", "Mocca"))
            ),
            isCodSupported = true,
            badgeText = "Karya PKK"
        ),

        // MAKANAN & KULINER
        Product(
            id = "prod_4",
            title = "Sambal Bawang Ulek Asli Mbok Darmi (Toples 200g)",
            storeName = "Dapur Mbok Darmi",
            storeVillage = "Dusun Krajan, Desa Sukamaju",
            storePhone = "081255443322",
            category = ProductCategory.MAKANAN,
            price = 22000L,
            originalPrice = 28000L,
            rating = 4.9f,
            soldCount = 380,
            stock = 85,
            imageUrl = "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500&auto=format&fit=crop&q=60",
            description = "Sambal bawang ulek legendaris menggunakan cabai rawit merah segar dari kebun sendiri. Minyak kelapa murni, gurih tanpa pengawet kimia. Tahan 1 bulan di suhu ruang.",
            variants = listOf(
                ProductVariant("Tingkat Kepedasan", listOf("Pedas Sedang (Level 3)", "Pedas Nampol (Level 5)"))
            ),
            isCodSupported = true,
            isFlashSale = true,
            badgeText = "Favorit Warga"
        ),
        Product(
            id = "prod_5",
            title = "Keripik Singkong Gurih Renyah Bumbu Balado Desa",
            storeName = "Camilan Berkah Bu Sri",
            storeVillage = "Dusun Pasir Muncang, Desa Sukamaju",
            storePhone = "081388776655",
            category = ProductCategory.MAKANAN,
            price = 15000L,
            originalPrice = 18000L,
            rating = 4.8f,
            soldCount = 430,
            stock = 120,
            imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&auto=format&fit=crop&q=60",
            description = "Keripik singkong renyah tipis dari singkong mentega kebun desa. Digoreng dengan minyak baru, bumbu balado gurih manis mantap.",
            variants = listOf(
                ProductVariant("Rasa", listOf("Original Asin Gurih", "Balado Pedas Manis", "Jagung Bakar"))
            ),
            isCodSupported = true,
            badgeText = "Camilan Desa"
        ),
        Product(
            id = "prod_6",
            title = "Nasi Liwet Kastrol Komplit Ayam Kampung & Teri Gurih",
            storeName = "Warung Makan Liwet Kang Asep",
            storeVillage = "Dusun Krajan RT 01, Desa Sukamaju",
            storePhone = "081277665544",
            category = ProductCategory.MAKANAN,
            price = 35000L,
            originalPrice = 40000L,
            rating = 5.0f,
            soldCount = 195,
            stock = 30,
            imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&auto=format&fit=crop&q=60",
            description = "Satu porsi lengkap nasi liwet harum daun salam & serai, disajikan dengan ayam goreng kampung empuk, sambal lalap petai, teri medan, dan tahu tempe goreng.",
            variants = listOf(
                ProductVariant("Pilihan Ayam", listOf("Ayam Goreng Lengkuas", "Ayam Bakar Madu")),
                ProductVariant("Sambal", listOf("Sambal Terasi", "Sambal Tomat"))
            ),
            isCodSupported = true,
            isFastDelivery = true,
            badgeText = "Siap Saji Hangat"
        ),

        // HASIL TANI & SAYUR
        Product(
            id = "prod_7",
            title = "Beras Pandan Wangi Organik 5 Kg (Panen Petani Sukamaju)",
            storeName = "Koperasi Tani Makmur Sejahtera",
            storeVillage = "Dusun Sawah Lega, Desa Sukamaju",
            storePhone = "081266554433",
            category = ProductCategory.HASIL_TANI,
            price = 72000L,
            originalPrice = 80000L,
            rating = 4.9f,
            soldCount = 510,
            stock = 70,
            imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=60",
            description = "Beras pandan wangi pulen alami tanpa pemutih dan tanpa pewangi buatan. Dipanen langsung dari sawah irigasi pegunungan desa Sukamaju. Bersih siap masak.",
            variants = listOf(
                ProductVariant("Kemasan", listOf("5 Kilogram", "10 Kilogram"))
            ),
            isCodSupported = true,
            badgeText = "Panen Baru"
        ),
        Product(
            id = "prod_8",
            title = "Paket Sayur Segar Sayur Asem Lengkap Panen Pagi",
            storeName = "Kebun Sayur Organik Kang Dadan",
            storeVillage = "Dusun Lembah Hijau, Desa Sukamaju",
            storePhone = "081322119988",
            category = ProductCategory.HASIL_TANI,
            price = 10000L,
            originalPrice = 12000L,
            rating = 4.8f,
            soldCount = 180,
            stock = 40,
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=500&auto=format&fit=crop&q=60",
            description = "Paket sayuran dipetik subuh hari: jagung manis, labu siam, kacang panjang, daun melinjo, buah melinjo, dan asam muda segar beserta racikan bumbu khas desa.",
            variants = emptyList(),
            isCodSupported = true,
            badgeText = "Petik Pagi"
        ),

        // KERAJINAN DESA
        Product(
            id = "prod_9",
            title = "Tas Anyaman Bambu Alami Ramah Lingkungan",
            storeName = "Sentra Anyaman Bambu Lestari",
            storeVillage = "Dusun Cikondang, Desa Sukamaju",
            storePhone = "081288990011",
            category = ProductCategory.KERAJINAN,
            price = 38000L,
            originalPrice = 45000L,
            rating = 4.9f,
            soldCount = 145,
            stock = 35,
            imageUrl = "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=500&auto=format&fit=crop&q=60",
            description = "Tas jinjing belanja anyaman bambu apus halus. Sangat kuat menampung belanjaan pasar hingga 10 kg, elegan dan ramah lingkungan mengurangi kantong plastik.",
            variants = listOf(
                ProductVariant("Ukuran", listOf("Sedang (30x25 cm)", "Besar (38x32 cm)"))
            ),
            isCodSupported = true,
            badgeText = "Handmade"
        ),
        Product(
            id = "prod_10",
            title = "Cobek Batu Kali Asli Lereng Gunung & Ulekan Halus",
            storeName = "Pahat Batu Alam Pak Joko",
            storeVillage = "Dusun Kali Bening, Desa Sukamaju",
            storePhone = "081399001122",
            category = ProductCategory.KERAJINAN,
            price = 48000L,
            originalPrice = 60000L,
            rating = 5.0f,
            soldCount = 95,
            stock = 20,
            imageUrl = "https://images.unsplash.com/photo-1514986888952-8cd320577b68?w=500&auto=format&fit=crop&q=60",
            description = "Cobek dan ulekan dari batu kali utuh 100% tanpa semen, tidak mudah berpasir saat mengulek sambal. Diamater 20 cm pas untuk dapur keluarga.",
            variants = listOf(
                ProductVariant("Diameter", listOf("Diameter 18 cm", "Diameter 22 cm"))
            ),
            isCodSupported = true,
            badgeText = "Batu Asli"
        ),

        // PENGANTARAN & JASA
        Product(
            id = "prod_11",
            title = "Jasa Kurir Antar Paket & Dokumen Kilat Antar Dusun",
            storeName = "Posko Ojek & Kurir Karang Taruna",
            storeVillage = "Pangkalan Ojek Depan Balai Desa Sukamaju",
            storePhone = "081234567890",
            category = ProductCategory.PENGANTARAN,
            price = 5000L,
            originalPrice = null,
            rating = 4.9f,
            soldCount = 620,
            stock = 999,
            imageUrl = "https://images.unsplash.com/photo-1580674684081-7617fbf3d745?w=500&auto=format&fit=crop&q=60",
            description = "Layanan antar paket, titipan makanan, atau berkas langsung dari rumah ke rumah seluruh pelosok Desa Sukamaju dan sekitarnya oleh armada pemuda karang taruna.",
            variants = listOf(
                ProductVariant("Wilayah", listOf("Satu Dusun (Rp 4.000)", "Antar Dusun (Rp 6.000)", "Ke Luar Desa (Rp 10.000)"))
            ),
            isCodSupported = true,
            badgeText = "Kurir Lokal Kilat"
        )
    )

    // Reactive State Holders
    private val _products = MutableStateFlow<List<Product>>(sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    val cartStateManager = CartStateManager(
        initialItems = listOf(
            CartItem(
                cartId = "cart_init_1",
                product = sampleProducts[3], // Sambal Bawang Mbok Darmi
                selectedVariant = "Pedas Nampol (Level 5)",
                quantity = 2,
                isSelected = true
            ),
            CartItem(
                cartId = "cart_init_2",
                product = sampleProducts[0], // Batik Tulis
                selectedVariant = "L - Coklat Klasik",
                quantity = 1,
                isSelected = true
            )
        )
    )
    val cartItems: StateFlow<List<CartItem>> = cartStateManager.items

    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                orderId = "ORD-DS-20261101",
                createdAt = "06 Nov 2026, 14:20",
                items = listOf(
                    CartItem("c1", sampleProducts[5], "Ayam Goreng Lengkuas", 1, true)
                ),
                subtotal = 35000L,
                shippingFee = 4000L,
                voucherDiscount = 5000L,
                villageServiceFee = 1000L,
                totalAmount = 35000L,
                paymentMethod = PaymentMethod.COD,
                shippingMethod = ShippingMethod.KURIR_LOKAL,
                status = OrderStatus.DIKIRIM,
                destinationAddress = defaultAddress,
                buyerNote = "Sambal dipisah ya mang, nanti bayar uang pas COD ke Kang Maman.",
                courierName = "Kang Maman (Ojek RT 03)",
                courierPhone = "081234567890",
                courierVehicle = "Honda Vario Merah (F 4421 DS)"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _courierBookings = MutableStateFlow<List<CourierBooking>>(
        listOf(
            CourierBooking(
                bookingId = "KRR-0912",
                createdAt = "06 Nov 2026, 11:15",
                serviceType = "Antar Barang / Paket",
                senderName = "Warung Bu Sri",
                senderAddress = "Dusun Pasir Muncang RT 01",
                recipientName = "Bu Lurah Sukamaju",
                recipientAddress = "Kantor Balai Desa Sukamaju",
                itemDescription = "Kue basah tampah pesanan rapat BPD",
                fleetType = CourierFleetType.KURIR_DESA,
                estimatedFee = 5000L,
                paymentMethod = PaymentMethod.COD,
                status = "Selesai",
                assignedCourier = "Ujang Supriatna (Pemuda Dusun 1)",
                assignedCourierPhone = "081234567890",
                notes = "Hati-hati jangan sampai miring"
            )
        )
    )
    val courierBookings: StateFlow<List<CourierBooking>> = _courierBookings.asStateFlow()

    private val _walletBalance = MutableStateFlow<Long>(175000L)
    val walletBalance: StateFlow<Long> = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(
        listOf(
            WalletTransaction("tx1", "Top-Up Saldo via QRIS", "05 Nov 2026", 100000L, true, "Isi saldo lewat QRIS BRI"),
            WalletTransaction("tx2", "Hasil Penjualan Keripik", "04 Nov 2026", 85000L, true, "Pembayaran pesanan ORD-DS-102"),
            WalletTransaction("tx3", "Bayar Kurir Desa", "03 Nov 2026", 10000L, false, "Ongkir antar paket dusun")
        )
    )
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    private val _currentAddress = MutableStateFlow(defaultAddress)
    val currentAddress: StateFlow<VillageAddress> = _currentAddress.asStateFlow()

    private val _myStoreName = MutableStateFlow("Warung Berkah Desa")
    val myStoreName: StateFlow<String> = _myStoreName.asStateFlow()

    private val _isStoreRegistered = MutableStateFlow(true)
    val isStoreRegistered: StateFlow<Boolean> = _isStoreRegistered.asStateFlow()

    // CART OPERATIONS
    fun addToCart(product: Product, selectedVariant: String, quantity: Int = 1) {
        cartStateManager.addItem(product, selectedVariant, quantity)
    }

    fun updateCartQuantity(cartId: String, delta: Int) {
        cartStateManager.updateQuantity(cartId, delta)
    }

    fun toggleCartSelection(cartId: String) {
        cartStateManager.toggleSelection(cartId)
    }

    fun toggleSelectAll(select: Boolean) {
        cartStateManager.toggleSelectAll(select)
    }

    fun removeFromCart(cartId: String) {
        cartStateManager.removeItem(cartId)
    }

    // ORDER CHECKOUT
    fun createOrder(
        shippingMethod: ShippingMethod,
        paymentMethod: PaymentMethod,
        buyerNote: String,
        voucherDiscount: Long = 0L
    ): Order {
        val selectedItems = cartStateManager.items.value.filter { it.isSelected }
        val subtotal = selectedItems.sumOf { it.totalPrice }
        val villageFee = 1000L
        val totalAmount = (subtotal + shippingMethod.cost + villageFee - voucherDiscount).coerceAtLeast(0L)

        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val nowStr = formatter.format(Date())

        val assignedCourierName = when (shippingMethod) {
            ShippingMethod.KURIR_LOKAL -> "Kang Maman (Kurir Ojek RT 03)"
            ShippingMethod.GOJEK_EXTERNAL -> "Driver GoSend Instant"
            ShippingMethod.GRAB_EXTERNAL -> "Driver GrabExpress Instant"
            ShippingMethod.AMBIL_SENDIRI -> "Ambil Mandiri di Toko"
        }

        val initialStatus = if (paymentMethod == PaymentMethod.DOMPET_DESA || paymentMethod == PaymentMethod.COD) {
            OrderStatus.DIKEMAS
        } else {
            OrderStatus.BELUM_BAYAR
        }

        val newOrder = Order(
            orderId = "ORD-DS-${System.currentTimeMillis().toString().takeLast(6)}",
            createdAt = nowStr,
            items = selectedItems,
            subtotal = subtotal,
            shippingFee = shippingMethod.cost,
            voucherDiscount = voucherDiscount,
            villageServiceFee = villageFee,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            shippingMethod = shippingMethod,
            status = initialStatus,
            destinationAddress = _currentAddress.value,
            buyerNote = buyerNote,
            courierName = assignedCourierName,
            courierPhone = "081234567890"
        )

        // If paying with Dompet Desa, deduct balance
        if (paymentMethod == PaymentMethod.DOMPET_DESA) {
            _walletBalance.value = (_walletBalance.value - totalAmount).coerceAtLeast(0L)
            val tx = WalletTransaction(
                id = "tx_${UUID.randomUUID().toString().take(6)}",
                title = "Belanja ${newOrder.orderId}",
                date = nowStr,
                amount = totalAmount,
                isIncome = false,
                description = "Pembayaran belanja produk UMKM Desa"
            )
            _walletTransactions.value = listOf(tx) + _walletTransactions.value
        }

        // Add to orders list
        _orders.value = listOf(newOrder) + _orders.value

        // Remove selected items from cart
        cartStateManager.clearSelectedItems()

        return newOrder
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.value = _orders.value.map {
            if (it.orderId == orderId) it.copy(status = newStatus) else it
        }
    }

    // COURIER HUB BOOKING
    fun bookCourier(
        serviceType: String,
        senderName: String,
        senderAddress: String,
        recipientName: String,
        recipientAddress: String,
        itemDescription: String,
        fleetType: CourierFleetType,
        paymentMethod: PaymentMethod,
        fee: Long,
        notes: String
    ): CourierBooking {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val nowStr = formatter.format(Date())

        val courierName = when (fleetType) {
            CourierFleetType.KURIR_DESA -> "Kang Asep (Ojek Desa Pos RT 02)"
            CourierFleetType.GOJEK_GOSEND -> "Driver Gojek Terdekat"
            CourierFleetType.GRAB_EXPRESS -> "Driver Grab Terdekat"
        }

        val booking = CourierBooking(
            bookingId = "KRR-${System.currentTimeMillis().toString().takeLast(4)}",
            createdAt = nowStr,
            serviceType = serviceType,
            senderName = senderName,
            senderAddress = senderAddress,
            recipientName = recipientName,
            recipientAddress = recipientAddress,
            itemDescription = itemDescription,
            fleetType = fleetType,
            estimatedFee = fee,
            paymentMethod = paymentMethod,
            status = "Kurir Menuju Lokasi",
            assignedCourier = courierName,
            assignedCourierPhone = "081234567890",
            notes = notes
        )

        _courierBookings.value = listOf(booking) + _courierBookings.value
        return booking
    }

    // WALLET TOP UP
    fun topUpWallet(amount: Long) {
        _walletBalance.value += amount
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val dateStr = formatter.format(Date())
        val tx = WalletTransaction(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            title = "Top-Up Saldo QRIS",
            date = dateStr,
            amount = amount,
            isIncome = true,
            description = "Isi saldo lewat QRIS Desa"
        )
        _walletTransactions.value = listOf(tx) + _walletTransactions.value
    }

    // SELLER CENTER (Buka Toko / UMKM Seller Mode)
    fun addNewProduct(
        title: String,
        category: ProductCategory,
        price: Long,
        originalPrice: Long?,
        stock: Int,
        description: String,
        variantsList: List<String>,
        isCod: Boolean,
        badgeText: String
    ) {
        val parsedVariants = if (variantsList.isNotEmpty()) {
            listOf(ProductVariant("Pilihan Varian", variantsList))
        } else emptyList()

        val newProduct = Product(
            id = "prod_usr_${System.currentTimeMillis()}",
            title = title,
            storeName = _myStoreName.value,
            storeVillage = "Dusun Krajan, Desa Sukamaju",
            category = category,
            price = price,
            originalPrice = originalPrice,
            rating = 5.0f,
            soldCount = 0,
            stock = stock,
            imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=60",
            description = description,
            variants = parsedVariants,
            isCodSupported = isCod,
            badgeText = badgeText.ifEmpty { "UMKM Desa Baru" },
            isMyStoreProduct = true
        )

        _products.value = listOf(newProduct) + _products.value
    }

    fun updateProductStock(productId: String, newStock: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stock = newStock) else it
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
    }

    fun updateAddress(newAddress: VillageAddress) {
        _currentAddress.value = newAddress
    }

    fun registerStore(name: String) {
        _myStoreName.value = name
        _isStoreRegistered.value = true
    }
}
