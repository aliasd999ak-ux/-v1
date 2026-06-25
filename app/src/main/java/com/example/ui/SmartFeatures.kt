package com.example.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.data.FoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// ==========================================
// 1. SIMULATED HOLOGRAPHIC BARCODE SCANNER
// ==========================================

data class PresetBarcodeProduct(
    val barcode: String,
    val name: String,
    val category: String,
    val recommendedDays: Int,
    val location: String,
    val unit: String,
    val quantity: Double,
    val notes: String
)

val presetProducts = listOf(
    PresetBarcodeProduct("62810003", "حليب طويل الأجل المراعي", "ألبان", 5, "الثلاجة", "لتر", 1.0, "حليب طازج معبأ، غني بالكالسيوم والبروتين."),
    PresetBarcodeProduct("62810010", "جبنة كرافت شيدر علب", "ألبان", 12, "الثلاجة", "علبة", 3.0, "جبنة مطبوخة ممتازة للفطور والتوست السريع."),
    PresetBarcodeProduct("62820002", "صلصة طماطم ندى", "خضروات", 14, "الرف", "علبة", 4.0, "معجون طماطم نقي ممتاز لإعداد الكبسة والإيدامات."),
    PresetBarcodeProduct("62830005", "نوتيلا شوكولاتة البندق", "أخرى", 30, "الرف", "علبة", 1.0, "كريمة بندق كاكاو لذيذة ومحبوبة للأطفال."),
    PresetBarcodeProduct("62810005", "صدور دجاج التنمية", "لحوم", 3, "الفريزر", "كجم", 1.2, "دجاج طازج نظيف وخالي من العظام."),
    PresetBarcodeProduct("62840001", "تفاح رويال جالا سكري", "فواكه", 8, "الثلاجة", "كجم", 2.0, "تفاح أحمر ناضج وحلو المذاق من مزارع الجبل."),
    PresetBarcodeProduct("62840012", "خيار بلدي طازج", "خضروات", 5, "درج الخضروات", "كجم", 1.5, "خيار مقرمش وغني بالماء."),
    PresetBarcodeProduct("62850020", "خبز توست أبيض هرفي", "مخبوزات", 6, "الرف", "كيس", 1.0, "توست طري طازج يومي بالحبوب.")
)

@Composable
fun SimulatedBarcodeScannerDialog(
    onDismiss: () -> Unit,
    onProductScanned: (PresetBarcodeProduct) -> Unit
) {
    val context = LocalContext.current
    var manualBarcode by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Sound effect trigger (retro beep)
    val playBeep = {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 80)
            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Laser scan animation line
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserYOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A), // Deep retro dark space theme
            border = BorderStroke(2.dp, Color(0xFF00FFCC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "ماسح الباركود النيون 🎚️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FFCC)
                    )
                    Box(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "قارئ نيون تفاعلي مستوحى من تكنولوجيا التسعينيات. وجّه كاميرا جهازك أو اختر منتجًا لتجربة المسح السريع.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Viewfinder Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF020617))
                        .border(BorderStroke(2.dp, Color(0xFF00FFCC).copy(alpha = 0.6f)), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Draw continuous laser scanner line
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val currentY = height * laserYOffset

                        // Holographic Grid Background
                        val rows = 8
                        val cols = 8
                        for (i in 1..rows) {
                            drawLine(
                                color = Color(0xFF00FFCC).copy(alpha = 0.05f),
                                start = Offset(0f, height * (i.toFloat() / rows)),
                                end = Offset(width, height * (i.toFloat() / rows)),
                                strokeWidth = 1f
                            )
                        }
                        for (i in 1..cols) {
                            drawLine(
                                color = Color(0xFF00FFCC).copy(alpha = 0.05f),
                                start = Offset(width * (i.toFloat() / cols), 0f),
                                end = Offset(width * (i.toFloat() / cols), height),
                                strokeWidth = 1f
                            )
                        }

                        // Neon corners
                        val margin = 10f
                        val len = 40f
                        val thick = 4f
                        // Top Left
                        drawLine(Color(0xFF00FFCC), Offset(margin, margin), Offset(margin + len, margin), thick)
                        drawLine(Color(0xFF00FFCC), Offset(margin, margin), Offset(margin, margin + len), thick)
                        // Top Right
                        drawLine(Color(0xFF00FFCC), Offset(width - margin, margin), Offset(width - margin - len, margin), thick)
                        drawLine(Color(0xFF00FFCC), Offset(width - margin, margin), Offset(width - margin, margin + len), thick)
                        // Bottom Left
                        drawLine(Color(0xFF00FFCC), Offset(margin, height - margin), Offset(margin + len, height - margin), thick)
                        drawLine(Color(0xFF00FFCC), Offset(margin, height - margin), Offset(margin, height - margin - len), thick)
                        // Bottom Right
                        drawLine(Color(0xFF00FFCC), Offset(width - margin, height - margin), Offset(width - margin - len, height - margin), thick)
                        drawLine(Color(0xFF00FFCC), Offset(width - margin, height - margin), Offset(width - margin, height - margin - len), thick)

                        // Laser Beam line
                        drawLine(
                            color = Color(0xFFFF0055), // Laser Red
                            start = Offset(margin + 5f, currentY),
                            end = Offset(width - margin - 5f, currentY),
                            strokeWidth = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF00FFCC))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("جاري فك تشفير الباركود بالليزر...", color = Color(0xFF00FFCC), fontSize = 12.sp)
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0xFF00FFCC).copy(alpha = 0.8f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "وجّه الكود البرمجي لقرائته تلقائياً",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual bar code entry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualBarcode,
                        onValueChange = { manualBarcode = it },
                        label = { Text("أدخل رقم الباركود يدوياً", color = Color.LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00FFCC),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF00FFCC)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (manualBarcode.isBlank()) {
                                Toast.makeText(context, "الرجاء إدخال رقم الباركود", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isScanning = true
                            scope.launch {
                                kotlinx.coroutines.delay(1000)
                                val matched = presetProducts.find { it.barcode == manualBarcode }
                                isScanning = false
                                playBeep()
                                if (matched != null) {
                                    onProductScanned(matched)
                                    onDismiss()
                                } else {
                                    // Generate a custom fresh product name
                                    onProductScanned(
                                        PresetBarcodeProduct(
                                            barcode = manualBarcode,
                                            name = "منتج باركود مجهول #${manualBarcode.takeLast(4)}",
                                            category = "أخرى",
                                            recommendedDays = 7,
                                            location = "الرف",
                                            unit = "قطعة",
                                            quantity = 1.0,
                                            notes = "تم إضافته تلقائياً عبر المسح اليدوي المبتكر للباركود."
                                        )
                                    )
                                    onDismiss()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC), contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("مسح", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Argentine and local pre-packaged scan list
                Text(
                    text = "منتجات سريعة الجرد والمطابقة التلقائية 📦",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetProducts) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isScanning = true
                                    scope.launch {
                                        kotlinx.coroutines.delay(800)
                                        isScanning = false
                                        playBeep()
                                        onProductScanned(product)
                                        onDismiss()
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("كود: ${product.barcode}", color = Color(0xFF00FFCC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text("التخزين: ${product.location}", color = Color.LightGray, fontSize = 11.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF00FFCC), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("جرد سريع", color = Color(0xFF00FFCC), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. RETRO PRINTABLE BARCODE LABEL GENERATOR
// ==========================================

@Composable
fun BarcodeGeneratorDialog(
    item: FoodItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Generate simulated pseudo-barcode stripes pattern from item barcode or id
    val barCodeString = if (item.barcode.isNotBlank()) item.barcode else "62899000${item.id}"

    // Generate bar widths based on digits to simulate code-128
    val widths = remember(barCodeString) {
        val list = mutableListOf<Float>()
        barCodeString.forEach { char ->
            val digit = char.toString().toIntOrNull() ?: 3
            list.add((digit % 4 + 1).toFloat() * 2f)
            list.add(((digit + 2) % 3 + 1).toFloat() * 1.5f)
            list.add(1f) // spacing
        }
        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(3.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Design
                Text(
                    text = "ملصق ترميز المخازن الذكي 🏷️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "مستودع الأرجنتين ومخازن التسعينيات الذكية",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // The Sticker Area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title / Stamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مستند جرد رسمي",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .border(BorderStroke(1.dp, Color.Red), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            Text(
                                text = "رقم التعريف: #${item.id}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Product Name & Location details
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("الموقع", color = Color.Gray, fontSize = 10.sp)
                                Text(item.storageLocation, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("الكمية", color = Color.Gray, fontSize = 10.sp)
                                Text("${item.quantity} ${item.unit}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("الانتهاء", color = Color.Gray, fontSize = 10.sp)
                                Text(formatDate(item.expiryDate), fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pseudo Barcode Drawing via Canvas (Visual highlight of the sticker)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            var xOffset = 0f
                            var barIndex = 0

                            // Center alignment offset
                            val totalWidth = widths.sum()
                            val startX = (size.width - totalWidth) / 2f

                            xOffset = startX

                            while (xOffset < (startX + totalWidth) && barIndex < widths.size) {
                                val width = widths[barIndex]
                                val drawBar = barIndex % 3 != 2 // draw bar, then leave spacing

                                if (drawBar) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = Offset(xOffset, 0f),
                                        size = androidx.compose.ui.geometry.Size(width, size.height)
                                    )
                                }
                                xOffset += width
                                barIndex++
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Barcode numeric digits
                        Text(
                            text = barCodeString,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Share / Save simulation
                Button(
                    onClick = {
                        Toast.makeText(context, "تم إرسال ملصق الباركود للطابعة اللاسلكية بنجاح! 🖨️", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("طباعة الملصق اللاصق", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "تم حفظ ملصق الباركود في المعرض! 📸", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مشاركة وحفظ الملصق")
                }
            }
        }
    }
}

// ==========================================
// 3. INTEGRATED ADVANCED NOTIFICATION CENTER
// ==========================================

@Composable
fun NotificationCenterDialog(
    allItems: List<FoodItem>,
    viewModel: FoodViewModel,
    onDismiss: () -> Unit,
    onItemClick: (FoodItem) -> Unit
) {
    val alertDays by viewModel.alertThresholdDays.collectAsState()
    val context = LocalContext.current

    // Segregate items
    val now = System.currentTimeMillis()
    val expiredItems = allItems.filter { getDaysRemaining(it.expiryDate) < 0 }
    val expiringSoonItems = allItems.filter {
        val days = getDaysRemaining(it.expiryDate)
        days in 0.0..(alertDays.toDouble())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = "جرس الإنذار الذكي 🔔",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        // Simulate a system push alert
                        val count = expiredItems.size + expiringSoonItems.size
                        if (count > 0) {
                            Toast.makeText(
                                context,
                                "🔔 تنبيه المطبخ: لديك $count عنصر تتطلب اهتماماً عاجلاً لتجنب هدر الطعام!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "🔔 حالة مخزنك ممتازة وطازجة بالكامل!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Test Notification", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Alert Slider Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تنبيه الأغذية المخصصة قبل:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("${alertDays} أيام", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = alertDays.toFloat(),
                            onValueChange = { viewModel.alertThresholdDays.value = it.toInt() },
                            valueRange = 1f..7f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            "يمكنك تمديد وقت التنبيه لضمان استهلاك الأطعمة والفاكهة وتجنب فسادها في الثلاجة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "الحالات الحرجة والمنتهية ⚠️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Expired Items
                    if (expiredItems.isNotEmpty()) {
                        item {
                            Text("منتهية الصلاحية تماماً (${expiredItems.size})", color = Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        items(expiredItems) { item ->
                            NotificationAlertCard(
                                item = item,
                                daysLeft = getDaysRemaining(item.expiryDate),
                                isExpired = true,
                                onActionConsume = {
                                    viewModel.deleteItem(item)
                                    Toast.makeText(context, "تم التخلص من المنتج واستبعاده من الجرد.", Toast.LENGTH_SHORT).show()
                                },
                                onActionExtend = {
                                    // Add 7 days to expiry
                                    val updated = item.copy(expiryDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))
                                    viewModel.updateItem(updated)
                                    Toast.makeText(context, "تم تجديد صلاحية المنتج لمدة أسبوع!", Toast.LENGTH_SHORT).show()
                                },
                                onClick = { onItemClick(item); onDismiss() }
                            )
                        }
                    }

                    // 2. Expiring soon Items
                    if (expiringSoonItems.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("توشك على الانتهاء خلال $alertDays أيام (${expiringSoonItems.size})", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        items(expiringSoonItems) { item ->
                            NotificationAlertCard(
                                item = item,
                                daysLeft = getDaysRemaining(item.expiryDate),
                                isExpired = false,
                                onActionConsume = {
                                    viewModel.deleteItem(item)
                                    Toast.makeText(context, "صحة وعافية! تم استهلاك المكون واستبعاده.", Toast.LENGTH_SHORT).show()
                                },
                                onActionExtend = {
                                    val updated = item.copy(expiryDate = item.expiryDate + (5 * 24 * 60 * 60 * 1000L))
                                    viewModel.updateItem(updated)
                                    Toast.makeText(context, "تم إضافة 5 أيام لمدى الصلاحية التقديرية.", Toast.LENGTH_SHORT).show()
                                },
                                onClick = { onItemClick(item); onDismiss() }
                            )
                        }
                    }

                    if (expiredItems.isEmpty() && expiringSoonItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(54.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("مطبخك آمن وطازج بالكامل!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    Text("لا توجد فواكه أو خضار توشك على الفساد.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationAlertCard(
    item: FoodItem,
    daysLeft: Double,
    isExpired: Boolean,
    onActionConsume: () -> Unit,
    onActionExtend: () -> Unit,
    onClick: () -> Unit
) {
    val cardColor = if (isExpired) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
    val contentColor = if (isExpired) Color(0xFFC62828) else Color(0xFFE65100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpired) Icons.Default.Cancel else Icons.Default.Warning,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item.name, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .background(contentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isExpired) "منتهي منذ ${-daysLeft.toInt()} ي" else "متبقي ${daysLeft.toInt()} ي",
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "الموقع: ${item.storageLocation} | الكمية: ${item.quantity} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onActionExtend,
                    colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تمديد الصلاحية", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onActionConsume,
                    colors = ButtonDefaults.buttonColors(containerColor = contentColor, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("تم الاستهلاك", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 4. RETRO '90s AI FOOD WASTE COOKING CHEF
// ==========================================

@Composable
fun AiRecipeChefDialog(
    allItems: List<FoodItem>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isGenerating by remember { mutableStateOf(false) }
    var recipeResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Filter available ingredients
    val expiringSoon = allItems.filter { getDaysRemaining(it.expiryDate) in 0.0..5.0 }
    val restOfStock = allItems.filter { getDaysRemaining(it.expiryDate) > 5.0 }

    val triggerAiRecipe: () -> Unit = {
        isGenerating = true
        recipeResult = ""

        scope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // FALLBACK: Highly polished, beautiful, locally customized recipe matching rule-base!
                kotlinx.coroutines.delay(2000) // Simulated AI delay for great UX feel
                isGenerating = false

                val soonNames = expiringSoon.map { it.name }
                val hasPoultry = allItems.any { it.category == "لحوم" || it.name.contains("دجاج") || it.name.contains("لحم") }
                val hasDairy = allItems.any { it.category == "ألبان" || it.name.contains("حليب") || it.name.contains("جبن") }
                val hasVeg = allItems.any { it.category == "خضروات" || it.name.contains("طماطم") || it.name.contains("خيار") }

                recipeResult = when {
                    hasPoultry && hasVeg -> """
                        🍲 **الوصفة المقترحة: صينية الدجاج بالخضار الدافئة (طريقة الأجداد الأرجنتينية)**
                        
                        *تاريخ الابتكار: مستوحى من مطابخ العائلات في بوينس آيرس عام 1994 لتقليل الهدر.*
                        
                        📝 **المكونات المستخرجة من مطبخك الطازج:**
                        - ${soonNames.joinToString("، ").ifBlank() { "دجاج طازج وخضار متنوعة" }}
                        - طماطم، بصل، بهارات مشكلة، زيت زيتون.
                        
                        ⚡ **طريقة التحضير خطوة بخطوة:**
                        1. قم بتقطيع الدجاج إلى قطع متساوية، ثم تبلها بالبهارات وزيت الزيتون والليمون.
                        2. قطّع الخضار المتبقية (طماطم، بطاطس، فلفل) ورتبها في قاعدة الصينية.
                        3. ضع قطع الدجاج فوق الخضار، وأضف ربع كوب من الماء الدافئ الممزوج بصلصة الطماطم.
                        4. غطِّ الصينية بورق القصدير واخبزها في الفرن لمدة 45 دقيقة، ثم حمّر الوجه برفق.
                        
                        💡 **سر الشيف لتقليل الهدر:**
                        هذه الصينية تبتلع أي نوع خضار متبقي لديك في درج الثلاجة كالجزر، الكوسا، والبطاطس، وتبقيها طازجة ومغذية ولذيذة!
                    """.trimIndent()

                    hasDairy -> """
                        🥞 **الوصفة المقترحة: الفطائر السريعة بحليب المطبخ المحلى**
                        
                        *تاريخ الابتكار: مستوحى من وصفات كافيهات الأرجنتين الكلاسيكية لحفظ الحليب والألبان.*
                        
                        📝 **المكونات المستخرجة من مطبخك الطازج:**
                        - ${soonNames.joinToString("، ").ifBlank() { "حليب كامل الدسم مبستر" }}
                        - دقيق، بيضة واحدة، سكر، قليل من الفانيليا والزبدة.
                        
                        ⚡ **طريقة التحضير خطوة بخطوة:**
                        1. في وعاء عميق، اخفق البيضة مع ملعقتين من السكر والفانيليا جيداً.
                        2. أضف كوباً من الحليب الذي يوشك على الانتهاء واخلطه جيداً.
                        3. أضف الدقيق تدريجياً مع ملعقة صغيرة بيكنج بودر حتى تحصل على قوام شبه سائل متماسك.
                        4. سخن مقلاة غير لاصقة مدهونة بقليل من الزبدة، واسكب كميات صغيرة لتحمير الفطائر من الجهتين.
                        
                        💡 **سر الشيف لتقليل الهدر:**
                        الحليب الذي قارب تاريخ انتهائه هو الأفضل لصنع المخبوزات والبانكيك والحلويات الدافئة لأنه يعطي طراوة مضاعفة للعجين!
                    """.trimIndent()

                    else -> """
                        🥗 **الوصفة المقترحة: سلطة الفتوش الشامية بالخضار المقرمشة والدبس**
                        
                        *تاريخ الابتكار: وصفة شرقية متوارثة لإنقاذ بقايا الخضار والخبز الجاف في المنزل.*
                        
                        📝 **المكونات المستخرجة من مطبخك الطازج:**
                        - ${soonNames.joinToString("، ").ifBlank() { "طماطم، خيار، أوراق خضراء" }}
                        - بقايا خبز توست أو خبز عربي، زيت زيتون، سماق، ليمون، دبس رمان.
                        
                        ⚡ **طريقة التحضير خطوة بخطوة:**
                        1. قطّع الخبز المتبقي إلى مكعبات صغيرة واقله في الزيت أو حمصه في الفرن مع رشة زيت وسماق ليكون مقرمشاً.
                        2. افرم بقايا الخضار المتوفرة (طماطم، خيار، بقدونس، نعناع) فرماً متوسط الحجم.
                        3. في وعاء صغير، امزج عصير الليمون، زيت الزيتون، الثوم المهروس، دبس الرمان، والملح لعمل التتبيلة الشهية.
                        4. امزج الخضار مع التتبيلة قبل التقديم مباشرة، وزيّن السطح بمكعبات الخبز المحمص المقرمش والسماق.
                        
                        💡 **سر الشيف لتقليل الهدر:**
                        الفتوش هو الصديق الأوفى للمطبخ الذكي، حيث يستوعب جميع أوراق النعناع والبقدونس وبقايا الخضار والخبز التالف، ليحولها إلى تحفة مغذية!
                    """.trimIndent()
                }
            } else {
                // Call actual Gemini REST API
                withContext(Dispatchers.IO) {
                    try {
                        val client = OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()

                        val soonList = expiringSoon.joinToString(", ") { "${it.name} (${it.quantity} ${it.unit})" }
                        val freshList = restOfStock.joinToString(", ") { "${it.name} (${it.quantity} ${it.unit})" }

                        val prompt = """
                            أنت شيف عبقري متخصص في ابتكار وصفات طعام مذهلة من بقايا الطعام لتقليل الهدر المنزلي.
                            لدي المكونات التالية التي توشك صلاحيتها على الانتهاء وتتطلب الاستهلاك فوراً:
                            [$soonList]
                            ولدي المكونات الطازجة الإضافية التالية في المخزن:
                            [$freshList]
                            
                            ابتكر وصفة عربية أو أرجنتينية ريفية تقليدية فريدة لطهيها اليوم، تجمع هذه العناصر بذكاء.
                            يرجى تنسيق الرد بشكل جذاب باللغة العربية مع العناوين والرموز التعبيرية كالتالي:
                            - اسم الوصفة المبتكرة
                            - أصل الوصفة ولمحة تاريخية شيقة عنها
                            - المكونات المستخدمة بدقة
                            - خطوات التحضير المفصلة
                            - نصائح ذكية لحفظ الطعام وتقليل الفاقد مستقبلاً.
                        """.trimIndent()

                        val requestJson = JSONObject().apply {
                            put("contents", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("parts", JSONArray().apply {
                                        put(JSONObject().apply {
                                            put("text", prompt)
                                        })
                                    })
                                })
                            })
                        }

                        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
                        val request = Request.Builder()
                            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                            .post(requestBody)
                            .build()

                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()

                        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                            val jsonResponse = JSONObject(responseBody)
                            val candidates = jsonResponse.getJSONArray("candidates")
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.getJSONObject("content")
                            val parts = content.getJSONArray("parts")
                            val text = parts.getJSONObject(0).getString("text")

                            withContext(Dispatchers.Main) {
                                recipeResult = text
                                isGenerating = false
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                recipeResult = "عذراً، فشل الاتصال بخادم الذكاء الاصطناعي. سنقوم بتشغيل مولد الوصفات المحلي البديل لمطبخك الذكي.\n\n" + 
                                    "وصفة بديلة سريعة:\n" + getLocalFallbackRecipe(expiringSoon)
                                isGenerating = false
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            recipeResult = "حدث خطأ غير متوقع أثناء الاتصال بـ Gemini API: ${e.message}\n\nإليك الوصفة البديلة المحلية من ذكاء المطبخ:\n\n" + getLocalFallbackRecipe(expiringSoon)
                            isGenerating = false
                        }
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF020617), // Futuristic dark space background
            border = BorderStroke(2.dp, Color(0xFF00FFCC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "طاهي الذكاء الاصطناعي ومقاوم الهدر 👨‍🍳",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FFCC)
                    )
                    Box(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "تقليص الهدر بنسبة 100%! يقوم الذكاء الاصطناعي بفحص المواد التي شارفت صلاحيتها على الانتهاء وابتكار أكلات تقليدية ممتازة وفورية لها.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ingredients checklist
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF00FFCC).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "المكونات المستهدفة بالطهي السريع 🌶️:",
                            color = Color(0xFF00FFCC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (expiringSoon.isEmpty()) {
                            Text("جميع المكونات آمنة تماماً في مطبخك ولن تفسد قريباً. يمكنك الابتكار باستخدام المخزون الكامل!", color = Color.White, fontSize = 11.sp)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                expiringSoon.take(4).forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                                            .border(BorderStroke(1.dp, Color(0xFFFF5555)), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(item.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Output Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00FFCC))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "جاري تشغيل محرك الطبخ الابتكاري التوليدي...",
                                color = Color(0xFF00FFCC),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "نحلل العناصر ونبحث في المطبخ العربي والأرجنتيني لحفظ طعامك",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else if (recipeResult.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = Color(0xFF00FFCC).copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "اضغط على الزر أدناه لتوليد وصفتك الذكية",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "يقوم الذكاء الاصطناعي بربط مكونات ثلاجتك ببعض لتقديم عشاء شهي فوري متكامل.",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 16.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = recipeResult,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Generation trigger buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { triggerAiRecipe() },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC), contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ابتكار الوصفة بالذكاء الاصطناعي", fontWeight = FontWeight.Bold)
                    }

                    if (recipeResult.isNotEmpty()) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "تم حفظ الوصفة في مذكرتك الذكية لمقاومة الهدر! 🗒️", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حفظ الوصفة")
                        }
                    }
                }
            }
        }
    }
}

// Local helper recipe generator fallback on empty keys
private fun getLocalFallbackRecipe(expiringSoon: List<FoodItem>): String {
    val names = expiringSoon.map { it.name }
    return """
        🍲 **وصفة ريفية مبتكرة: يخنة المطبخ المشكلة لتقليل الهدر (El Guiso del Almacén)**
        
        *مستوحاة من الأرجنتين الريفية لخلط جميع المكونات الطازجة وإنتاج عشاء دافئ ومغذي.*
        
        📝 **المكونات المستخرجة من مطبخك:**
        - ${names.joinToString("، ").ifBlank() { "مكونات متنوعة من جرد المطبخ" }}
        - بصل، فص ثوم، رشة زيت، ملعقة بهار مشكل، طماطم مبشورة.
        
        ⚡ **خطوات التحضير السريعة:**
        1. سخن القليل من الزيت في مقلاة عميقة وشوح البصل والثوم المفروم حتى يذبل.
        2. أضف الخضار المتبقية مقطعة لمكعبات صغيرة تدريجياً لضمان النضج الكامل.
        3. اسكب الطماطم المبشورة ونصف كوب من الماء الدافئ المالح واتركها تتسبك على نار هادئة.
        4. أضف الدجاج أو الجبن المتاح لديك قبل الانتهاء بـ 10 دقائق لتتشرب النكهة.
        
        💡 **نصيحة تقليص الهدر:**
        يمكنك غلق اليخنة ووضعها في علبة زجاجية محكمة الإغلاق بالثلاجة لتناولها غداً كوجبة سريعة مع الأرز أو الخبز المحمص.
    """.trimIndent()
}

// Helper to format timestamp
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))
    return sdf.format(Date(timestamp))
}

// Helper to calculate remaining days
private fun getDaysRemaining(expiryTimestamp: Long): Double {
    val diffMs = expiryTimestamp - System.currentTimeMillis()
    return diffMs / (24.0 * 60.0 * 60.0 * 1000.0)
}
