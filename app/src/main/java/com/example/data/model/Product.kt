package com.example.data.model

enum class ProductCategory(val displayName: String, val iconName: String) {
    ALL("Semua", "Storefront"),
    PAKAIAN("Pakaian & Konveksi", "Checkroom"),
    MAKANAN("Makanan & Minuman", "Restaurant"),
    HASIL_TANI("Hasil Tani & Sayur", "Agriculture"),
    KERAJINAN("Kerajinan Desa", "Handyman"),
    PENGANTARAN("Jasa & Antar", "DeliveryDining")
}

data class ProductVariant(
    val name: String,
    val options: List<String>
)

data class Product(
    val id: String,
    val title: String,
    val storeName: String,
    val storeVillage: String,
    val storePhone: String = "081234567890",
    val category: ProductCategory,
    val price: Long,
    val originalPrice: Long? = null,
    val rating: Float = 4.9f,
    val soldCount: Int = 120,
    val stock: Int = 50,
    val imageResName: String = "",
    val imageUrl: String = "",
    val description: String,
    val variants: List<ProductVariant> = emptyList(),
    val isCodSupported: Boolean = true,
    val isLocalDeliveryAvailable: Boolean = true,
    val isFastDelivery: Boolean = false,
    val isFlashSale: Boolean = false,
    val badgeText: String = "UMKM Desa",
    val isMyStoreProduct: Boolean = false
) {
    val discountPercent: Int?
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price).toDouble() / originalPrice) * 100).toInt()
        } else null

    fun formattedPrice(): String = "Rp %,d".format(price).replace(',', '.')
    fun formattedOriginalPrice(): String? = originalPrice?.let { "Rp %,d".format(it).replace(',', '.') }
}
