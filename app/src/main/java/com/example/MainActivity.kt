package com.example

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Label
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.FoodItem
import com.example.ui.FoodViewModel
import com.example.ui.SortOption
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Simulated Product for High-Tech Barcode Scanner
data class SimulatedProduct(
    val name: String,
    val category: String,
    val shelfLife: Int,
    val storage: String,
    val quantity: Double,
    val unit: String,
    val price: Float,
    val barcode: String,
    val notes: String
)

@Composable
fun SimulatedNotificationBanner(
    title: String,
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDismiss() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.error, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationImportant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "تم",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BarcodeScannerSimulator(
    onDismiss: () -> Unit,
    onProductScanned: (name: String, category: String, shelfLifeDays: Int, storage: String, quantity: Double, unit: String, price: Float, notes: String) -> Unit
) {
    val context = LocalContext.current
    
    val simulatedProducts = listOf(
        SimulatedProduct("حليب المراعي طازج عبوة عائلية", "ألبان", 7, "الثلاجة", 2.0, "لتر", 12f, "6281014001254", "حليب مبستر غني بالكالسيوم طازج وممتاز للقهوة."),
        SimulatedProduct("جبنة كرافت كاسات الأصلية", "ألبان", 15, "الثلاجة", 1.0, "علبة", 18f, "6281014002341", "جبنة قابلة للدهن غنية بنكهة الشيدر الأصلية."),
        SimulatedProduct("عصير المراعي برتقال طبيعي", "ألبان", 10, "الثلاجة", 1.5, "لتر", 11f, "6281014004561", "عصير طبيعي منعش وخالي من المكونات الصناعية."),
        SimulatedProduct("خبز صامولي لوزين طازج", "مخبوزات", 4, "الرف", 1.0, "كيس", 4f, "6281014007890", "خبز صامولي خفيف ورائع للوجبات الخفيفة والمدارس."),
        SimulatedProduct("صدور دجاج رضوى مبردة", "لحوم", 5, "الفريزر", 1.0, "كجم", 35f, "6281014008123", "صدور دجاج نظيفة وخالية من الدهون تماماً."),
        SimulatedProduct("صلصة طماطم لونا معلبة", "خضروات", 30, "الرف", 4.0, "علبة", 14f, "6281014009111", "صلصة طماطم فاخرة ومحفوظة بعناية."),
        SimulatedProduct("تفاح أحمر سكري عبوة عائلية", "فواكه", 14, "درج الخضروات", 1.5, "كجم", 16f, "6281014005122", "تفاح سكري طازج ومقرمش."),
        SimulatedProduct("خيار قطاف محلي طازج", "خضروات", 6, "درج الخضروات", 1.0, "كجم", 7f, "6281014006122", "خيار طازج وممتاز للسلطات اليومية.")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "إلغاء",
                        tint = Color.White
                    )
                }
                Text(
                    text = "قارئ الباركود والـ QR الذكي 📱",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Box(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pulse scanner animation box
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(BorderStroke(2.dp, Color.Green), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E1E1E))
                ) {
                    // Pulsing green scanning line inside camera box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(Color.Green)
                            .align(Alignment.Center)
                    )
                    
                    Text(
                        text = "جاري مسح الباركود على القطعة...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Green.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "اختر منتجاً لمحاكاة قراءة الباركود من كاميرا الهاتف 📲:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(simulatedProducts) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onProductScanned(
                                    prod.name,
                                    prod.category,
                                    prod.shelfLife,
                                    prod.storage,
                                    prod.quantity,
                                    prod.unit,
                                    prod.price,
                                    prod.notes
                                )
                                Toast.makeText(context, "تم قراءة الكود بنجاح 🔊: ${prod.barcode}", Toast.LENGTH_SHORT).show()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(prod.category),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "الباركود: ${prod.barcode} • الصلاحية: ${prod.shelfLife} أيام",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.Green.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${prod.price.toInt()} ر.س",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = {
                    val r = simulatedProducts.random()
                    onProductScanned(r.name, r.category, r.shelfLife, r.storage, r.quantity, r.unit, r.price, r.notes)
                    Toast.makeText(context, "تم المسح الذكي عشوائياً!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("محاكاة مسح عشوائي ذكي تلقائي ⚡", fontWeight = FontWeight.Bold)
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FoodInventoryApp()
                }
            }
        }
    }
}

enum class ActiveScreen {
    Dashboard,
    AddEdit
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FoodInventoryApp() {
    val viewModel: FoodViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(ActiveScreen.Dashboard) }
    var itemToEdit by remember { mutableStateOf<FoodItem?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDetailsSheet by remember { mutableStateOf(false) }
    var selectedItemForDetails by remember { mutableStateOf<FoodItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // New smart features states
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }
    var showAiChef by remember { mutableStateOf(false) }
    var selectedItemForLabel by remember { mutableStateOf<FoodItem?>(null) }
    val allItemsForCenter by viewModel.allItems.collectAsState()

    // ECO & NOTIFICATION SYSTEM STATES
    val sharedPrefs = remember { context.getSharedPreferences("eco_savings_prefs", Context.MODE_PRIVATE) }
    var totalSaved by remember { mutableStateOf(sharedPrefs.getFloat("total_saved", 145f)) }
    var totalWasted by remember { mutableStateOf(sharedPrefs.getFloat("total_wasted", 15f)) }
    var notificationDaysLimit by remember { mutableStateOf(sharedPrefs.getInt("notification_days_limit", 3)) }
    
    var notificationBannerVisible by remember { mutableStateOf(false) }
    var notificationBannerTitle by remember { mutableStateOf("جرد الأطعمة الذكي 🚨") }
    var notificationBannerText by remember { mutableStateOf("") }

    // Automatic alert triggering for expiring items
    LaunchedEffect(allItemsForCenter, notificationDaysLimit) {
        val expiringSoonItem = allItemsForCenter.firstOrNull { item ->
            val diffMs = item.expiryDate - System.currentTimeMillis()
            val daysLeft = diffMs / (24.0 * 60.0 * 60.0 * 1000.0)
            daysLeft in 0.0..notificationDaysLimit.toDouble()
        }
        if (expiringSoonItem != null) {
            val daysLeft = ((expiringSoonItem.expiryDate - System.currentTimeMillis()) / (24.0 * 60.0 * 60.0 * 1000.0)).toInt().coerceAtLeast(0)
            notificationBannerTitle = "تنبيه صلاحية: ${expiringSoonItem.name} ⚠️"
            notificationBannerText = "هذا المنتج مخزن في (${expiringSoonItem.storageLocation}) وينتهي صلاحيته خلال $daysLeft أيام! يرجى استهلاكه قريباً لتجنب الهدر."
            notificationBannerVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (currentScreen == ActiveScreen.Dashboard) {
                FloatingActionButton(
                    onClick = {
                        itemToEdit = null
                        currentScreen = ActiveScreen.AddEdit
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                        .testTag("add_item_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة منتج جديد",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (targetState == ActiveScreen.AddEdit) {
                    slideInVertically(initialOffsetY = { it }) + fadeIn() with
                            slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                } else {
                    slideInVertically(initialOffsetY = { -it }) + fadeIn() with
                            slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                ActiveScreen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        paddingValues = innerPadding,
                        onItemClick = { item ->
                            selectedItemForDetails = item
                            showDetailsSheet = true
                        },
                        onEditItem = { item ->
                            itemToEdit = item
                            currentScreen = ActiveScreen.AddEdit
                        },
                        totalSaved = totalSaved,
                        totalWasted = totalWasted,
                        onUpdateSavedWasted = { s, w ->
                            totalSaved = s
                            totalWasted = w
                        },
                        notificationDaysLimit = notificationDaysLimit,
                        onUpdateDaysLimit = { limit ->
                            notificationDaysLimit = limit
                        },
                        onTriggerNotificationSim = { title, msg ->
                            notificationBannerTitle = title
                            notificationBannerText = msg
                            notificationBannerVisible = true
                        },
                        onBarcodeClick = { showBarcodeScanner = true },
                        onNotificationCenterClick = { showNotificationCenter = true },
                        onAiChefClick = { showAiChef = true }
                    )
                }
                ActiveScreen.AddEdit -> {
                    AddEditScreen(
                        viewModel = viewModel,
                        itemToEdit = itemToEdit,
                        onDismiss = {
                            currentScreen = ActiveScreen.Dashboard
                            itemToEdit = null
                        },
                        onSave = {
                            currentScreen = ActiveScreen.Dashboard
                            itemToEdit = null
                            Toast.makeText(context, "تم حفظ المنتج بنجاح!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

        if (showDetailsSheet && selectedItemForDetails != null) {
            val item = selectedItemForDetails!!
            val daysLeft = getDaysRemaining(item.expiryDate)
            val statusColor = when {
                daysLeft < 0 -> Color(0xFFD32F2F)
                daysLeft <= 3.0 -> Color(0xFFF57C00)
                else -> Color(0xFF388E3C)
            }
            val statusText = when {
                daysLeft < 0 -> "منتهي الصلاحية"
                daysLeft <= 3.0 -> "يوشك على الانتهاء"
                else -> "طازج وسليم"
            }

            ModalBottomSheet(
                onDismissRequest = {
                    showDetailsSheet = false
                    selectedItemForDetails = null
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "المكان: ${item.storageLocation}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!item.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = File(item.imageUri),
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = getCategoryIcon(item.category),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "لم يتم التقاط صورة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = statusColor.copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "الوضعية والصلاحية",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        daysLeft < 0 -> "منتهي الصلاحية"
                                        daysLeft == 0.0 -> "ينتهي اليوم!"
                                        daysLeft < 1.0 -> "ينتهي غداً!"
                                        else -> "ينتهي خلال ${daysLeft.toInt()} أيام"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "الكمية الحالية",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${item.quantity} ${item.unit}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "تم جردها بدقة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تاريخ الإضافة (الإنشاء):",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = formatDate(item.creationDate),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationImportant,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تاريخ انتهاء الصلاحية:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = formatDate(item.expiryDate),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Extract price & Clean notes
                    val priceRegex = "\\[Price:([\\d.]+)\\]".toRegex()
                    val match = priceRegex.find(item.notes)
                    val itemPrice = match?.groupValues?.get(1)?.toDoubleOrNull() ?: 12.0 // fallback estimated price
                    val cleanNotes = item.notes.replace("\\[Price:[\\d.]+\\]".toRegex(), "").trim()

                    if (cleanNotes.isNotEmpty()) {
                        Text(
                            text = "تفاصيل ومعلومات إضافية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = cleanNotes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp),
                                lineHeight = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Display Price Estimate Badge
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ProductionQuantityLimits,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "السعر المقدر للمنتج:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${itemPrice.toInt()} ر.س",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "نصيحة ذكية لحفظ هذا المنتج 💡",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = getStorageAdvice(item.category),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "تحديث حالة المنتج الحقيقية (صفر هدر) 🛡️:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val gained = itemPrice * item.quantity
                                val newSaved = totalSaved + gained.toFloat()
                                sharedPrefs.edit().putFloat("total_saved", newSaved).apply()
                                totalSaved = newSaved
                                viewModel.deleteItem(item)
                                showDetailsSheet = false
                                selectedItemForDetails = null
                                Toast.makeText(context, "كفو! تم استهلاك المنتج وحماية ${gained.toInt()} ريال من الهدر 🎉", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تم الاستهلاك (توفير!)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val wasted = itemPrice * item.quantity
                                val newWasted = totalWasted + wasted.toFloat()
                                sharedPrefs.edit().putFloat("total_wasted", newWasted).apply()
                                totalWasted = newWasted
                                viewModel.deleteItem(item)
                                showDetailsSheet = false
                                selectedItemForDetails = null
                                Toast.makeText(context, "للأسف انتهت صلاحية المنتج وتم هدر ${wasted.toInt()} ريال 🗑️", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC62828)
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تالف وتم هدره 🗑️", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showDetailsSheet = false
                                itemToEdit = item
                                currentScreen = ActiveScreen.AddEdit
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تعديل البيانات", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.deleteItem(item)
                                showDetailsSheet = false
                                selectedItemForDetails = null
                                Toast.makeText(context, "تم حذف المنتج من المطبخ", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("حذف صامت")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            selectedItemForLabel = item
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Label, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إنشاء ملصق الباركود وتذكرة المخزن 🏷️", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        SimulatedNotificationBanner(
            title = notificationBannerTitle,
            message = notificationBannerText,
            visible = notificationBannerVisible,
            onDismiss = { notificationBannerVisible = false }
        )

        // Overlay dialogs for advanced smart features
        if (showBarcodeScanner) {
            com.example.ui.SimulatedBarcodeScannerDialog(
                onDismiss = { showBarcodeScanner = false },
                onProductScanned = { scannedProd ->
                    viewModel.searchQuery.value = scannedProd.barcode
                    Toast.makeText(context, "تم العثور على المنتج ومطابقة الباركود: ${scannedProd.name}", Toast.LENGTH_LONG).show()
                }
            )
        }

        if (showNotificationCenter) {
            com.example.ui.NotificationCenterDialog(
                allItems = allItemsForCenter,
                viewModel = viewModel,
                onDismiss = { showNotificationCenter = false },
                onItemClick = { item ->
                    selectedItemForDetails = item
                    showDetailsSheet = true
                }
            )
        }

        if (showAiChef) {
            com.example.ui.AiRecipeChefDialog(
                allItems = allItemsForCenter,
                onDismiss = { showAiChef = false }
            )
        }

        if (selectedItemForLabel != null) {
            com.example.ui.BarcodeGeneratorDialog(
                item = selectedItemForLabel!!,
                onDismiss = { selectedItemForLabel = null }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: FoodViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onItemClick: (FoodItem) -> Unit,
    onEditItem: (FoodItem) -> Unit,
    totalSaved: Float,
    totalWasted: Float,
    onUpdateSavedWasted: (saved: Float, wasted: Float) -> Unit,
    notificationDaysLimit: Int,
    onUpdateDaysLimit: (Int) -> Unit,
    onTriggerNotificationSim: (title: String, message: String) -> Unit,
    onBarcodeClick: () -> Unit,
    onNotificationCenterClick: () -> Unit,
    onAiChefClick: () -> Unit
) {
    val items by viewModel.filteredItems.collectAsState()
    val stats by viewModel.statsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val categories = listOf("الكل", "فواكه", "خضروات", "ألبان", "لحوم", "مخبوزات", "أخرى")
    val statuses = listOf("الكل", "طازج", "يوشك على الانتهاء", "منتهي الصلاحية")
    val locations = listOf("الكل", "الثلاجة", "الفريزر", "الرف", "درج الخضروات")

    var showSortMenu by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("inventory") } // "inventory", "recipes", "eco", "alerts"
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Kitchen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "جرد الأطعمة الذكي",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "نظام متكامل لتصوير وتتبع صلاحية غذائك وصفر هدر مالي",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. AI Cooking Assistant Button
                IconButton(
                    onClick = onAiChefClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "مساعد الطبخ بالذكاء الاصطناعي",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 2. Barcode scanner Button
                IconButton(
                    onClick = onBarcodeClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "ماسح الباركود",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 3. Notification Center Button with Badge Count!
                Box {
                    IconButton(
                        onClick = onNotificationCenterClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "تنبيهات الصلاحية",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    val totalAlarms = stats.warningCount + stats.expiredCount
                    if (totalAlarms > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = totalAlarms.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Premium Navigation Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
        ) {
            val tabs = listOf(
                Pair("inventory", "📦 الجرد الفعلي"),
                Pair("recipes", "🍳 طاهي المطبخ"),
                Pair("eco", "🛡️ درع التوفير"),
                Pair("alerts", "🔔 منبه الصلاحية")
            )
            items(tabs) { tab ->
                val isSelected = activeTab == tab.first
                val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                val chipText = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipBg)
                        .clickable { activeTab = tab.first }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tab.second,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = chipText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Render Active Tab Content
        when (activeTab) {
            "inventory" -> {
                // STATS COMPACT ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatsWidget(
                        label = "إجمالي الأغذية",
                        count = stats.totalItems,
                        icon = Icons.Default.Layers,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatsWidget(
                        label = "طازج",
                        count = stats.freshCount,
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    StatsWidget(
                        label = "على الوشك",
                        count = stats.warningCount,
                        icon = Icons.Default.Warning,
                        containerColor = Color(0xFFFFF3E0),
                        contentColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                    StatsWidget(
                        label = "منتهي الصلاحية",
                        count = stats.expiredCount,
                        icon = Icons.Default.NotificationImportant,
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFC62828),
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search & Sort bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("ابحث عن فواكه، خضار، أو مكان...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onBarcodeClick) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "ماسح الباركود",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("search_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "ترتيب وحفظ",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text("رتب حسب: ${option.displayNameArabic}") },
                                    onClick = {
                                        viewModel.sortBy.value = option
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortBy == option) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        val chipColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipColor)
                                .clickable { viewModel.selectedCategory.value = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(category),
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(statuses) { status ->
                        val isSelected = selectedStatus == status
                        val badgeColor = when (status) {
                            "طازج" -> Color(0xFF2E7D32)
                            "يوشك على الانتهاء" -> Color(0xFFE65100)
                            "منتهي الصلاحية" -> Color(0xFFC62828)
                            else -> MaterialTheme.colorScheme.outline
                        }
                        val chipBg = if (isSelected) badgeColor.copy(alpha = 0.15f) else Color.Transparent
                        val borderStyle = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) badgeColor else MaterialTheme.colorScheme.outlineVariant
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(chipBg)
                                .border(borderStyle, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectedStatus.value = status }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) badgeColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Storage Locations Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(locations) { loc ->
                        val isSelected = selectedLocation == loc
                        val chipBg = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        val chipText = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(chipBg)
                                .clickable { viewModel.selectedLocation.value = loc }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = chipText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Items list or empty state
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.img_food_empty),
                                contentDescription = "قائمة فارغة",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedLocation != "الكل") "لا توجد نتائج تطابق فلاترك" else "مطبخك فارغ وجاهز للجرد!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedLocation != "الكل") "يرجى تعديل خيارات البحث أو التصفية" else "اضغط على الزر أدناه (+) أو استخدم قارئ الباركود في صفحة الإضافة لتسجيل المنتجات بسرعة مذهلة بالصور.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = paddingValues.calculateBottomPadding() + 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items) { item ->
                            FoodItemCard(
                                item = item,
                                onClick = { onItemClick(item) },
                                onEdit = { onEditItem(item) }
                            )
                        }
                    }
                }
            }

            "recipes" -> {
                // TABS 2: SMART CHEF SUGGESTER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "💡 طاهي الذكاء الاصطناعي يقترح عليك وصفات مستوحاة من محتويات مطبخك التي قاربت على الانتهاء لتوفير أموالك ومنع التلف!",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "الوصفات المقترحة لك اليوم 🍽️:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated interactive recipes based on food items in inventory
                    val recipesList = listOf(
                        Triple("شوربة الخضار المشكلة المغذية", "خضروات", "وصفة سريعة وصحية للغاية. قطّع الخضار المتاحة لديك مثل الجزر، الكوسة، والطماطم مكعبات صغيرة. اسلقها بمرقة دجاج دافئة مع ملعقة كمون وقليل من الليمون الحامض لمنع فساد الخضار الورقية والاستمتاع بوجبة دافئة."),
                        Triple("سلطة الفواكه المنعشة بالكريمة والعسل", "فواكه", "ممتازة لإنقاذ الفواكه المستهدفة من الذبول! قطّع التفاح، البرتقال والموز مكعبات صغيرة. أضف إليها كوباً من الحليب البارد وقليلاً من العسل مع أوراق النعناع المنعشة وقم بتقديمها مبردة."),
                        Triple("صينية صدور دجاج بالكريمة والفرن", "لحوم", "وصفة لذيذة ومميزة. تبّل صدور الدجاج المبردة بالثوم والبهارات المشكلة والملح. صب عليها كريمة الطبخ ثم اخبزها بالفرن لمدة 30 دقيقة وقدمها مع الأرز الساخن."),
                        Triple("توست فرنسي مقرمش ذهبي بالبيض", "مخبوزات", "مثالية لشرائح التوست أو الصامولي المتبقي. اغمس شرائح الخبز في خليط من الحليب، البيض، والڤانيليا. قم بتحميصها على مقلاة ساخنة مدهونة بالزبدة حتى تأخذ لوناً ذهبياً رائعاً.")
                    )

                    recipesList.forEach { recipe ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = recipe.first,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "قسم: ${recipe.second}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = recipe.third,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مؤشر حماية الأغذية: 98٪ (صديق للبيئة)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "eco" -> {
                // TAB 3: SAVINGS ECO TRACKER DASHBOARD
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🛡️ درع كفاءة المطبخ البيئي والمالي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val total = totalSaved + totalWasted
                            val efficiency = if (total > 0f) ((totalSaved / total) * 100).toInt() else 100

                            Box(
                                modifier = Modifier.size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = efficiency / 100f,
                                    strokeWidth = 10.dp,
                                    color = if (efficiency > 80) Color(0xFF2E7D32) else Color(0xFFE65100),
                                    modifier = Modifier.size(110.dp)
                                )
                                Text(
                                    text = "$efficiency٪",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when {
                                    efficiency >= 85 -> "كفو! أنت بطل حقيقي في محاربة الهدر وتوفير ميزانية منزلك بشكل رائع! 🌍💚"
                                    efficiency >= 60 -> "ممتاز! لكن يمكنك زيادة التوفير بالانتباه للمنتجات التي تقترب صلاحيتها من الانتهاء."
                                    else -> "تنبيه! لديك نسبة هدر مرتفعة في الأطعمة. استعمل 'منبه الصلاحية' لتحسين حماية ميزانيتك. ⚠️"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تفاصيل الميزانية التراكمية 💰:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, Color(0xFF81C784))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "مجموع المدخرات المحمية", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${totalSaved.toInt()} ر.س", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "طعام تم استهلاكه", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            border = BorderStroke(1.dp, Color(0xFFE57373))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "مجموع الأموال المهدورة", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC62828))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${totalWasted.toInt()} ر.س", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "تلف لانتهاء الصلاحية", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val sp = context.getSharedPreferences("eco_savings_prefs", Context.MODE_PRIVATE)
                            sp.edit().putFloat("total_saved", 0f).putFloat("total_wasted", 0f).apply()
                            onUpdateSavedWasted(0f, 0f)
                            Toast.makeText(context, "تم إعادة تعيين إحصائيات صفر هدر بنجاح 🔄", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إعادة تصفير العداد والبدء من جديد 🔄", fontWeight = FontWeight.Bold)
                    }
                }
            }

            "alerts" -> {
                // TAB 4: ADVANCED SMART NOTIFICATION PANEL & SIMULATOR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🔔 إعدادات التنبيه والإنذار المبكر الذكي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "يرسل لك التطبيق تنبيهات دورية تلقائية لتذكيرك بالأطعمة قبل تلفها بعدد محدد من الأيام. حدد الفترة المناسبة لك أدناه:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Radio selections for days limit
                            val limits = listOf(1, 2, 3, 5)
                            limits.forEach { limit ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val sp = context.getSharedPreferences("eco_savings_prefs", Context.MODE_PRIVATE)
                                            sp.edit().putInt("notification_days_limit", limit).apply()
                                            onUpdateDaysLimit(limit)
                                            Toast.makeText(context, "تم حفظ عتبة الإخطار: $limit أيام بنجاح ✅", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = notificationDaysLimit == limit,
                                        onClick = {
                                            val sp = context.getSharedPreferences("eco_savings_prefs", Context.MODE_PRIVATE)
                                            sp.edit().putInt("notification_days_limit", limit).apply()
                                            onUpdateDaysLimit(limit)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "تنبيه مبكر قبل $limit أيام من تاريخ الانتهاء",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (notificationDaysLimit == limit) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📱 اختبار نظام الإشعارات الفوري",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "اضغط على الزر أدناه لمحاكاة إرسال إشعار فوري حقيقي من نظام الهاتف للأغذية منتهية الصلاحية بمطبخك حالياً لتجربته حياً:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (items.isNotEmpty()) {
                                        val firstItem = items.first()
                                        onTriggerNotificationSim(
                                            "جرد الأطعمة الذكي 🚨",
                                            "تنبيه عاجل: منتج [${firstItem.name}] يوشك على انتهاء صلاحيته خلال $notificationDaysLimit أيام فقط! تفقد الثلاجة واستهلكه الآن."
                                        )
                                    } else {
                                        onTriggerNotificationSim(
                                            "جرد الأطعمة الذكي 🚨",
                                            "تنبيه عاجل: حليب المراعي طازج يوشك على انتهاء صلاحيته خلال $notificationDaysLimit أيام فقط! تفقد الثلاجة واستهلكه الآن."
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("اختبر إرسال إشعار فوري 🔔", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsWidget(
    label: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FoodItemCard(
    item: FoodItem,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    val daysLeft = getDaysRemaining(item.expiryDate)
    val statusColor = when {
        daysLeft < 0 -> Color(0xFFD32F2F)
        daysLeft <= 3.0 -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }
    val statusText = when {
        daysLeft < 0 -> "منتهي"
        daysLeft <= 3.0 -> "ينتهي قريباً"
        else -> "طازج وسليم"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("food_item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!item.imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = File(item.imageUri),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(item.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.storageLocation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ProductionQuantityLimits,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when {
                        daysLeft < 0 -> "منتهي الصلاحية منذ ${-daysLeft.toInt()} يوم!"
                        daysLeft == 0.0 -> "ينتهي اليوم!"
                        daysLeft < 1.0 -> "ينتهي غداً!"
                        else -> "ينتهي خلال ${daysLeft.toInt()} يوم"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { onEdit() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "تعديل",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditScreen(
    viewModel: FoodViewModel,
    itemToEdit: FoodItem?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var category by remember { mutableStateOf(itemToEdit?.category ?: "فواكه") }
    var storageLocation by remember { mutableStateOf(itemToEdit?.storageLocation ?: "الثلاجة") }
    var quantityStr by remember { mutableStateOf(itemToEdit?.quantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(itemToEdit?.unit ?: "قطعة") }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    val initialDays = if (itemToEdit != null) {
        val diff = itemToEdit.expiryDate - itemToEdit.creationDate
        (diff / (24 * 60 * 60 * 1000.0)).coerceIn(1.0, 30.0).toFloat()
    } else {
        7f
    }
    var durationInDays by remember { mutableStateOf(initialDays) }

    var creationDate by remember { mutableStateOf(itemToEdit?.creationDate ?: System.currentTimeMillis()) }
    var expiryDate by remember { mutableStateOf(itemToEdit?.expiryDate ?: (System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))) }

    LaunchedEffect(durationInDays, creationDate) {
        expiryDate = creationDate + (durationInDays.toLong() * 24 * 60 * 60 * 1000L)
    }

    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var capturedPhotoPath by remember { mutableStateOf<String?>(itemToEdit?.imageUri) }

    val tempPhotoFile = remember {
        File(context.cacheDir, "camera_capture_temp.jpg")
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            try {
                val persistentFile = File(context.filesDir, "food_photo_${System.currentTimeMillis()}.jpg")
                tempPhotoFile.inputStream().use { input ->
                    persistentFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                capturedPhotoPath = persistentFile.absolutePath
                Toast.makeText(context, "تم التقاط الصورة بنجاح!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "فشل حفظ الصورة الملتقطة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                val persistentFile = File(context.filesDir, "food_photo_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(selectedUri)?.use { input ->
                    FileOutputStream(persistentFile).use { output ->
                        input.copyTo(output)
                    }
                }
                capturedPhotoPath = persistentFile.absolutePath
                Toast.makeText(context, "تم اختيار الصورة بنجاح!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "فشل تحميل الصورة من المعرض", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "رجوع",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (itemToEdit == null) "إضافة جرد جديد" else "تعديل بيانات المنتج",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "صورة المنتج والتقاط الجرد 📸",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!capturedPhotoPath.isNullOrEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = File(capturedPhotoPath!!),
                            contentDescription = "صورة المنتج المعاينة",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = { capturedPhotoPath = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "إزالة الصورة",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "التقط صورة طازجة لتخزين أسرع",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempPhotoFile
                            )
                            currentPhotoUri = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "حدث خطأ أثناء فتح الكاميرا", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("التقاط الكاميرا", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اختيار من المعرض", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "المعلومات والبيانات الأساسية 📝",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم المنتج (مثال: تفاح أخضر، خيار بلدي)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            var categoryExpanded by remember { mutableStateOf(false) }
            val categories = listOf("فواكه", "خضروات", "ألبان", "لحوم", "مخبوزات", "أخرى")
            Box {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("تصنيف المنتج") },
                    trailingIcon = {
                        IconButton(onClick = { categoryExpanded = true }) {
                            Icon(imageVector = Icons.Default.Layers, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(cat),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            var locationExpanded by remember { mutableStateOf(false) }
            val locations = listOf("الثلاجة", "الفريزر", "الرف", "درج الخضروات")
            Box {
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("مكان التخزين / الموقع 📍") },
                    trailingIcon = {
                        IconButton(onClick = { locationExpanded = true }) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { locationExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                DropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                storageLocation = loc
                                locationExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("الكمية") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                var unitExpanded by remember { mutableStateOf(false) }
                val units = listOf("قطعة", "كجم", "جرام", "لتر", "علبة", "كيس")
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الوحدة") },
                        trailingIcon = {
                            IconButton(onClick = { unitExpanded = true }) {
                                Icon(imageVector = Icons.Default.Fastfood, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { unitExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    DropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    unit = u
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "الصلاحية وتاريخ الانتهاء والمدة ⏳",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "حدد مدة الصلاحية التقديرية باليوم، وسيقوم التطبيق بجدولة تاريخ الانتهاء تلقائياً لراحتك.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مدة الصلاحية (الرف):",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${durationInDays.toInt()} أيام",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = durationInDays,
                        onValueChange = { durationInDays = it },
                        valueRange = 1f..30f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "تاريخ الإنشاء والبدء:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDate(creationDate),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "تاريخ الانتهاء المقدر:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDate(expiryDate),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("معلومات تفصيلية أخرى (ملاحظات، نصائح، إلخ)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("notes_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "الرجاء إدخال اسم المنتج أولاً", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val qty = quantityStr.toDoubleOrNull() ?: 1.0

                    val finalItem = FoodItem(
                        id = itemToEdit?.id ?: 0,
                        name = name,
                        category = category,
                        creationDate = creationDate,
                        expiryDate = expiryDate,
                        storageLocation = storageLocation,
                        imageUri = capturedPhotoPath,
                        quantity = qty,
                        unit = unit,
                        notes = notes
                    )

                    if (itemToEdit == null) {
                        viewModel.insertItem(finalItem)
                    } else {
                        viewModel.updateItem(finalItem)
                    }

                    onSave()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (itemToEdit == null) "حفظ وإضافة للمطبخ" else "حفظ التغييرات",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun getDaysRemaining(expiryTimestamp: Long): Double {
    val diffMs = expiryTimestamp - System.currentTimeMillis()
    return diffMs / (24.0 * 60.0 * 60.0 * 1000.0)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))
    return sdf.format(Date(timestamp))
}

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "فواكه" -> Icons.Default.Fastfood
        "خضروات" -> Icons.Default.Kitchen
        "ألبان" -> Icons.Default.Layers
        "لحوم" -> Icons.Default.Fastfood
        "مخبوزات" -> Icons.Default.Kitchen
        else -> Icons.Default.Fastfood
    }
}

fun getStorageAdvice(category: String): String {
    return when(category) {
        "فواكه" -> "احفظ التفاح والبرتقال في الثلاجة. الموز والأفوكادو يفضل حفظهما في درجة حرارة الغرفة بعيداً عن الفواكه الأخرى لتجنب سرعة النضج."
        "خضروات" -> "الخضروات الورقية تفضل الأجواء الرطبة والباردة (درج الثلاجة). البطاطس والبصل يجب حفظهما في مكان جاف ومظلم خارج الثلاجة."
        "ألبان" -> "احفظ الحليب والأجبان دائماً في الرف الأوسط من الثلاجة حيث الحرارة مستقرة، وتجنب حفظها في باب الثلاجة ليبقى بارداً."
        "لحوم" -> "اللحوم والدواجن الطازجة يجب طبخها خلال يومين أو حفظها في الفريزر فوراً مغلفة بإحكام لحمايتها من حروق التجميد."
        "مخبوزات" -> "احفظ الخبز في كيس مغلق في الرف المخصص. للتخزين الطويل، يمكنك تجميده وإعادة تسخينه ليبقى طازجاً."
        else -> "تأكد من إغلاق العبوات بإحكام بعد فتحها وحفظها في درجات الحرارة الموصى بها على الغلاف لتبقى طازجة لأطول فترة."
    }
}
