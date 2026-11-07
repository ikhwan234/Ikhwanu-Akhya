package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CourierBooking
import com.example.data.model.CourierFleetType
import com.example.data.model.PaymentMethod
import com.example.ui.theme.DesaGreenPrimary
import com.example.ui.theme.GojekGreen
import com.example.ui.theme.GrabGreen
import com.example.ui.theme.RatingYellow
import com.example.ui.theme.ShopeeOrangeSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurirDesaScreen(
    courierBookings: List<CourierBooking>,
    onBookCourier: (serviceType: String, sender: String, senderAddr: String, recipient: String, recipientAddr: String, itemDesc: String, fleet: CourierFleetType, payment: PaymentMethod, fee: Long, notes: String) -> Unit,
    onOpenGojek: () -> Unit,
    onOpenGrab: () -> Unit,
    onContactCourierWhatsApp: (String, String) -> Unit,
    onCallCourier: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var serviceType by remember { mutableStateOf("Antar Paket / Barang") }
    var senderName by remember { mutableStateOf("Bu Sri (Warung Berkah)") }
    var senderAddress by remember { mutableStateOf("Dusun Pasir Muncang RT 01") }
    var recipientName by remember { mutableStateOf("Pak Lurah") }
    var recipientAddress by remember { mutableStateOf("Balai Desa Sukamaju") }
    var itemDescription by remember { mutableStateOf("Paket pakaian batik & camilan") }
    var selectedFleet by remember { mutableStateOf(CourierFleetType.KURIR_DESA) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.COD) }
    var notes by remember { mutableStateOf("") }
    var isBookingSuccess by remember { mutableStateOf(false) }

    val fee = when (selectedFleet) {
        CourierFleetType.KURIR_DESA -> 5000L
        CourierFleetType.GOJEK_GOSEND -> 14000L
        CourierFleetType.GRAB_EXPRESS -> 15000L
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Kurir Desa Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("kurir_hero_banner")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kurir_desa_hero_1794020762339),
                    contentDescription = "Kurir Desa Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topEnd = 12.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Layanan Kurir & Antar Jemput Desa",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Diantar pemuda lokal desa atau opsi konek Gojek",
                            color = Color(0xFFC8E6C9),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // External Connector Shortcut (Gojek / Grab)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Konek ke Aplikasi Eksternal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Luar Desa",
                                color = DesaGreenPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Untuk pengiriman ke luar kota / antar kecamatan yang tidak terjangkau kurir desa, gunakan tombol di bawah untuk langsung membuka Gojek atau Grab:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Gojek Button
                        Button(
                            onClick = onOpenGojek,
                            colors = ButtonDefaults.buttonColors(containerColor = GojekGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_launch_gojek")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buka Gojek",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Grab Button
                        Button(
                            onClick = onOpenGrab,
                            colors = ButtonDefaults.buttonColors(containerColor = GrabGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_launch_grab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buka Grab",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Form Panggil Kurir Lokal Desa
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = DesaGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pesan Antar Paket & Ojek Desa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pilihan Armada
                    Text("Pilih Armada Pengantar:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    CourierFleetType.values().forEach { fleet ->
                        val isSelected = fleet == selectedFleet
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { selectedFleet = fleet },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF9F9F9),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, DesaGreenPrimary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedFleet = fleet },
                                    colors = RadioButtonDefaults.colors(selectedColor = DesaGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(fleet.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(fleet.provider, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                                Text(
                                    text = "Rp %,d".format(fleet.baseFee).replace(',', '.'),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DesaGreenPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lokasi Jemput & Antar
                    OutlinedTextField(
                        value = senderAddress,
                        onValueChange = { senderAddress = it },
                        label = { Text("Titik Jemput (Asal)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = DesaGreenPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = recipientAddress,
                        onValueChange = { recipientAddress = it },
                        label = { Text("Titik Antar (Tujuan)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ShopeeOrangeSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = itemDescription,
                        onValueChange = { itemDescription = it },
                        label = { Text("Barang yang Diantar / Titip Belanja") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metode Bayar Kurir (COD / QRIS / DompetDesa)
                    Text("Metode Bayar Ongkir:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(PaymentMethod.COD, PaymentMethod.QRIS, PaymentMethod.DOMPET_DESA).forEach { pay ->
                            val isSel = pay == selectedPayment
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPayment = pay },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFFFFF3E0) else Color(0xFFF5F5F5),
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.5.dp, ShopeeOrangeSecondary) else null
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pay.title.split(" ").first(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) ShopeeOrangeSecondary else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            onBookCourier(
                                serviceType,
                                senderName,
                                senderAddress,
                                recipientName,
                                recipientAddress,
                                itemDescription,
                                selectedFleet,
                                selectedPayment,
                                fee,
                                notes
                            )
                            isBookingSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DesaGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_courier_booking_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Panggil Kurir (Rp %,d)".format(fee).replace(',', '.'),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (isBookingSuccess) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Kurir sedang menuju lokasi jemput!",
                                color = DesaGreenPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daftar Kurir Lokal Siaga (Posko Desa)
        item {
            Text(
                text = "Kurir Lokal Siaga di Desa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val activeCouriers = listOf(
            Triple("Kang Maman", "Pos Ojek RT 03", "081234567890"),
            Triple("Kang Asep Suherman", "Pos RW 02 Pasar Sukamaju", "081398765432"),
            Triple("Ujang Supriatna", "Karang Taruna Dusun 1", "081255667788")
        )

        items(activeCouriers) { (name, pos, phone) ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(DesaGreenPrimary.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = DesaGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                Text("Online", color = DesaGreenPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text(text = pos, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = RatingYellow, modifier = Modifier.size(12.dp))
                            Text(" 4.9 • 240+ Antar", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    // Contact Actions: WA & Dial
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                onContactCourierWhatsApp(
                                    phone,
                                    "Halo $name, saya ingin minta tolong antar barang/pesanan di Desa Sukamaju."
                                )
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE8F5E9), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat WA Kurir",
                                tint = DesaGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { onCallCourier(phone) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFF3E0), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Telepon Kurir",
                                tint = ShopeeOrangeSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
