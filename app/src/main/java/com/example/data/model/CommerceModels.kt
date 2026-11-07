package com.example.data.model

data class CartItem(
    val cartId: String,
    val product: Product,
    val selectedVariant: String = "",
    val quantity: Int = 1,
    val isSelected: Boolean = true
) {
    val totalPrice: Long
        get() = product.price * quantity

    fun formattedTotalPrice(): String = "Rp %,d".format(totalPrice).replace(',', '.')
}

enum class PaymentMethod(val title: String, val subtitle: String, val iconKey: String) {
    COD("COD (Bayar di Tempat)", "Bayar tunai ke Kurir Desa saat pesanan sampai", "LocalAtm"),
    QRIS("QRIS Desa & Nasional", "Scan barcode QRIS (GoPay, OVO, ShopeePay, BCA, DANA)", "QrCodeScanner"),
    DOMPET_DESA("Saldo DompetDesa", "Pembayaran instan dipotong dari saldo aplikasi", "AccountBalanceWallet"),
    TRANSFER_BANK("Transfer Bank (BRI/BCA/BSI)", "Virtual Account transfer otomatis", "AccountBalance")
}

enum class ShippingMethod(
    val title: String,
    val subtitle: String,
    val cost: Long,
    val estimatedTime: String,
    val badge: String
) {
    KURIR_LOKAL(
        title = "Kurir Lokal Desa (Ojek Pemuda RT)",
        subtitle = "Langsung diantar tetangga/karang taruna dusun",
        cost = 4000L,
        estimatedTime = "15 - 30 Menit",
        badge = "Rekomendasi Warga"
    ),
    AMBIL_SENDIRI(
        title = "Ambil Sendiri di Warung / Toko",
        subtitle = "Ambil langsung ke rumah penjual tanpa ongkir",
        cost = 0L,
        estimatedTime = "Siap dalam 10 Menit",
        badge = "Gratis"
    ),
    GOJEK_EXTERNAL(
        title = "GoSend / Gojek Instant (Eksternal)",
        subtitle = "Terhubung dengan kurir Gojek luar desa",
        cost = 14000L,
        estimatedTime = "20 - 45 Menit",
        badge = "Integrasi Gojek"
    ),
    GRAB_EXTERNAL(
        title = "GrabExpress Instant (Eksternal)",
        subtitle = "Terhubung dengan aplikasi Grab",
        cost = 15000L,
        estimatedTime = "20 - 45 Menit",
        badge = "Integrasi Grab"
    )
}

enum class OrderStatus(val label: String, val stepIndex: Int) {
    BELUM_BAYAR("Menunggu Pembayaran", 0),
    DIKEMAS("Pesanan Dikemas Penjual", 1),
    DIKIRIM("Sedang Diantar Kurir", 2),
    SELESAI("Pesanan Selesai", 3),
    DIBATALKAN("Dibatalkan", -1)
}

data class Order(
    val orderId: String,
    val createdAt: String,
    val items: List<CartItem>,
    val subtotal: Long,
    val shippingFee: Long,
    val voucherDiscount: Long = 0L,
    val villageServiceFee: Long = 1000L, // Kas Desa
    val totalAmount: Long,
    val paymentMethod: PaymentMethod,
    val shippingMethod: ShippingMethod,
    val status: OrderStatus,
    val destinationAddress: VillageAddress,
    val buyerNote: String = "",
    val courierName: String = "Kang Maman (Kurir RT 03)",
    val courierPhone: String = "6281234567890",
    val courierVehicle: String = "Honda Beat Hitam (B 3482 DES)"
) {
    fun formattedTotal(): String = "Rp %,d".format(totalAmount).replace(',', '.')
    fun formattedShippingFee(): String = "Rp %,d".format(shippingFee).replace(',', '.')
}

data class VillageAddress(
    val recipientName: String,
    val phoneNumber: String,
    val dusunName: String,
    val rtRw: String,
    val villageName: String,
    val subdistrictName: String,
    val landmark: String
) {
    fun fullAddressString(): String =
        "$recipientName ($phoneNumber)\n$dusunName, $rtRw, Desa $villageName, Kec. $subdistrictName\nPatokan: $landmark"
}

enum class CourierFleetType(val title: String, val provider: String, val baseFee: Long) {
    KURIR_DESA("Kurir Lokal Desa", "Karang Taruna / Ojek Pangkalan", 5000L),
    GOJEK_GOSEND("GoSend Instant", "Gojek Indonesia", 14000L),
    GRAB_EXPRESS("GrabExpress", "Grab Indonesia", 15000L)
}

data class CourierBooking(
    val bookingId: String,
    val createdAt: String,
    val serviceType: String, // "Antar Barang / Paket", "Titip Belanja Pasar", "Ojek Penumpang"
    val senderName: String,
    val senderAddress: String,
    val recipientName: String,
    val recipientAddress: String,
    val itemDescription: String,
    val fleetType: CourierFleetType,
    val estimatedFee: Long,
    val paymentMethod: PaymentMethod,
    val status: String, // "Mencari Kurir", "Kurir Menuju Lokasi", "Barang Diantar", "Selesai"
    val assignedCourier: String,
    val assignedCourierPhone: String,
    val notes: String = ""
) {
    fun formattedFee(): String = "Rp %,d".format(estimatedFee).replace(',', '.')
}

data class WalletTransaction(
    val id: String,
    val title: String,
    val date: String,
    val amount: Long,
    val isIncome: Boolean,
    val description: String
) {
    fun formattedAmount(): String {
        val sign = if (isIncome) "+" else "-"
        return "$sign Rp %,d".format(amount).replace(',', '.')
    }
}
