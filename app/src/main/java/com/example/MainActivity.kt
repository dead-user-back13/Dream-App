package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.DreamEntity
import com.example.data.model.DreamAnalysis
import com.example.data.model.DreamSymbol
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DreamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: DreamViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

// Minimal Localization Helper Object
object T {
    fun get(key: String, lang: String): String {
        return when (lang) {
            "fa" -> when (key) {
                "title" -> "تعبیر خواب رویان"
                "prompt_placeholder" -> "خواب خود را با جزئیات بنویسید... (مثال: دیشب خواب دیدم که در آسمانی پر ستاره پرواز می‌کنم و یک پر قو بر روی آب دیدم.)"
                "interpret_btn" -> "تفسیر خواب با هوش مصنوعی"
                "interpreting" -> "در حال تعبیر خواب شما..."
                "nav_interpret" -> "تعبیر"
                "nav_history" -> "تاریخچه"
                "nav_bookmarks" -> "نشان‌شده‌ها"
                "history_empty" -> "هنوز خوابی تعبیر نکرده‌اید."
                "bookmarks_empty" -> "خواب‌های نشان‌شده در اینجا ظاهر می‌شوند."
                "clear_history" -> "پاک کردن تاریخچه"
                "api_settings" -> "تنظیمات کلید API"
                "api_placeholder" -> "کلید اختصاصی Gemini API خود را وارد کنید..."
                "api_hint" -> "اگر این کادر را خالی بگذارید، برنامه تلاش می‌کند از کلید پیش‌فرض پلتفرم استفاده کند."
                "symbols_title" -> "نمادهای کلیدی خواب شما"
                "mood_title" -> "حس و حال کلی:"
                "save_success" -> "خواب با موفقیت نشان شد"
                "save_removed" -> "از نشان‌شده‌ها حذف شد"
                "delete_success" -> "خواب حذف شد"
                "clear_success" -> "تمامی تاریخچه پاک شد"
                "error_no_key" -> "کلید API یافت نشد. لطفاً در بخش تنظیمات یک کلید وارد کنید تا برنامه کار کند."
                "error_generic" -> "خطایی در ارتباط با سرور رخ داد. لطفا چند لحظه دیگر امتحان کنید."
                "welcome_sub" -> "دریچه‌ای به شناخت اعماق ناخودآگاه"
                "translate_toggle" -> "Translate to English"
                "api_configured" -> "کلید API به صورت محلی ذخیره شد."
                "api_save_btn" -> "ذخیره تغییرات"
                "tap_to_expand" -> "لمس کنید تا نماد تعبیر شود"
                "close_btn" -> "بستن"
                "app_about" -> "این نرم‌افزار با بهره‌گیری از مدل‌های متمایز هوش مصنوعی و تحلیل‌های روان‌شناختی، نمادهای پنهان در رویاهای شما را آشکار می‌سازد."
                "history_confirm" -> "آیا تاریخچه پاک شود؟"
                "delete_btn" -> "حذف"
                "confirm_yes" -> "بله"
                "confirm_cancel" -> "لغو"
                "selected_lang" -> "زبان: فارسی"
                "input_dream_below" -> "رویای خود را توصیف کنید"
                "lucid" -> "رویای آگاهانه"
                "mystical" -> "عرفانی"
                "anxious" -> "پراضطراب"
                "prophetic" -> "پیشگویانه"
                "harmonious" -> "روشن و آرام"
                "melancholy" -> "غم‌انگیز"
                else -> key
            }
            else -> when (key) {
                "title" -> "Royan Dreams"
                "prompt_placeholder" -> "Describe your dream in full detail... (e.g., Last night I dreamed of flying through a starry sky and finding a feather on calm water.)"
                "interpret_btn" -> "Interpret with AI"
                "interpreting" -> "Uncovering subconscious symbols..."
                "nav_interpret" -> "Analyze"
                "nav_history" -> "History"
                "nav_bookmarks" -> "Saved"
                "history_empty" -> "No search history records found."
                "bookmarks_empty" -> "Your bookmarked dreams will appear here."
                "clear_history" -> "Clear Database"
                "api_settings" -> "Gemini API Configuration"
                "api_placeholder" -> "Enter custom Gemini API key..."
                "api_hint" -> "If left empty, the application will attempt to use the platform's default API key."
                "symbols_title" -> "Key Dream Symbols"
                "mood_title" -> "Ethereal Mood:"
                "save_success" -> "Saved to bookmarks"
                "save_removed" -> "Removed from bookmarks"
                "delete_success" -> "Record deleted successfully"
                "clear_success" -> "Database history cleared"
                "error_no_key" -> "API Key missing. Please input a valid Gemini key in settings."
                "error_generic" -> "Connection error occurred. Please check and try again."
                "welcome_sub" -> "Bridge to your profound subconscious self"
                "translate_toggle" -> "ترجمه به فارسی"
                "api_configured" -> "Custom API key stored locally."
                "api_save_btn" -> "Apply Settings"
                "tap_to_expand" -> "Tap to reveal symbol archetype"
                "close_btn" -> "Close"
                "app_about" -> "This application leverages advanced artificial intelligence to analyze dream semantics, extracting deep symbolic archetypes and spiritual insights."
                "history_confirm" -> "Are you sure you want to clear all history?"
                "delete_btn" -> "Delete"
                "confirm_yes" -> "Confirm"
                "confirm_cancel" -> "Cancel"
                "selected_lang" -> "Lang: English"
                "input_dream_below" -> "Describe Your Dream"
                "lucid" -> "Lucid"
                "mystical" -> "Mystical"
                "anxious" -> "Anxious"
                "prophetic" -> "Prophetic"
                "harmonious" -> "Harmonious"
                "melancholy" -> "Melancholic"
                else -> key
            }
        }
    }
}

// Particle Helper Class for Twinkling Stars
@Keep
data class StarParticle(
    val xFraction: Float,
    val yFraction: Float,
    val basePulseSpeed: Float,
    val initialDelay: Float,
    val baseSize: Float
)

// Canvas-based Twinkling Stardust Anim for interactive UI
@Composable
fun DreamyBackground(isDark: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "celestial_twinkle")
    
    // Smooth background gradient transition
    val gradientStartColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1C1B1F) else Color(0xFFF7F2FA),
        animationSpec = tween(durationMillis = 800),
        label = "bg_color_start"
    )
    val gradientMiddleColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF2B2930) else Color(0xFFECE6F0),
        animationSpec = tween(durationMillis = 800),
        label = "bg_color_middle"
    )
    val gradientEndColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF151417) else Color(0xFFEADDFF),
        animationSpec = tween(durationMillis = 800),
        label = "bg_color_end"
    )

    // Twinkling animation value
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_pulse"
    )

    val stars = remember {
        List(20) {
            StarParticle(
                xFraction = (0..100).random() / 100f,
                yFraction = (0..100).random() / 100f,
                basePulseSpeed = (1500..3000).random().toFloat(),
                initialDelay = (0..1000).random().toFloat(),
                baseSize = (4..12).random().toFloat()
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.verticalGradient(
                    colors = listOf(gradientStartColor, gradientMiddleColor, gradientEndColor)
                )
                drawRect(brush = brush)

                // Twinkle stars in dark mode, floating circular aura clouds in light mode
                if (isDark) {
                    stars.forEach { star ->
                        val alpha = (pulseAlpha * star.basePulseSpeed % 1.0f + 0.3f).coerceIn(0.1f, 0.9f)
                        drawCircle(
                            color = Color(0xFFFFECC4).copy(alpha = alpha),
                            radius = star.baseSize,
                            center = Offset(size.width * star.xFraction, size.height * star.yFraction)
                        )
                    }
                } else {
                    // Floating pastel translucent clouds
                    drawCircle(
                        color = Color(0xFFFFE0D9).copy(alpha = 0.45f),
                        radius = size.width * 0.4f,
                        center = Offset(size.width * 0.1f, size.height * 0.2f)
                    )
                    drawCircle(
                        color = Color(0xFFD6E4FF).copy(alpha = 0.5f),
                        radius = size.width * 0.5f,
                        center = Offset(size.width * 0.85f, size.height * 0.65f)
                    )
                }
            }
    )
}

@Composable
fun MainScreen(viewModel: DreamViewModel) {
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val currentAnalysis by viewModel.currentAnalysis.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    val context = LocalContext.current

    var showSettingsDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Flip entire layout direction easily using dynamic LocalLayoutDirection composition local provider
    val layoutDirection = if (appLanguage == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Animating starry/cloud outer backdrop
            DreamyBackground(isDark = isDarkTheme)

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    TopScreenBar(
                        appLanguage = appLanguage,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { viewModel.setDarkTheme(!isDarkTheme) },
                        onLangToggle = {
                            val nextLang = if (appLanguage == "fa") "en" else "fa"
                            viewModel.setAppLanguage(nextLang)
                        },
                        onSettingsClick = { showSettingsDialog = true }
                    )
                },
                bottomBar = {
                    FloatingBarNavigation(
                        activeTab = activeTab,
                        onTabSelected = { viewModel.setActiveTab(it) },
                        appLanguage = appLanguage,
                        isDark = isDarkTheme
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "tab_switches"
                    ) { tab ->
                        when (tab) {
                            "interpret" -> InterpretTabContent(
                                viewModel = viewModel,
                                appLanguage = appLanguage,
                                isDark = isDarkTheme
                            )
                            "history" -> HistoryTabContent(
                                viewModel = viewModel,
                                appLanguage = appLanguage,
                                isDark = isDarkTheme,
                                isBookmarkView = false
                            )
                            "bookmarks" -> HistoryTabContent(
                                viewModel = viewModel,
                                appLanguage = appLanguage,
                                isDark = isDarkTheme,
                                isBookmarkView = true
                            )
                        }
                    }
                }
            }

            // Custom modern developer settings dialog
            if (showSettingsDialog) {
                SettingsPanelDialog(
                    customApiKey = customApiKey,
                    onSaveKey = {
                        viewModel.setCustomApiKey(it)
                        Toast.makeText(context, T.get("api_configured", appLanguage), Toast.LENGTH_SHORT).show()
                        showSettingsDialog = false
                    },
                    onClearHistory = {
                        viewModel.clearAllHistory()
                        Toast.makeText(context, T.get("clear_success", appLanguage), Toast.LENGTH_SHORT).show()
                        showSettingsDialog = false
                    },
                    onDismiss = { showSettingsDialog = false },
                    appLanguage = appLanguage,
                    isDark = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun TopScreenBar(
    appLanguage: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLangToggle: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Poetic Title with clean custom Canvas Moon rendering or glowing typography
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) Color(0xFFFFD54F).copy(alpha = 0.15f)
                        else Color(0xFF6E5DF5).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌙",
                    fontSize = 20.sp,
                    modifier = Modifier.animateContentSize()
                )
            }
            Column {
                Text(
                    text = T.get("title", appLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = T.get("welcome_sub", appLanguage),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            }
        }

        // Action Toggles
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Language selector button
            IconButton(
                onClick = onLangToggle,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .testTag("lang_toggle")
            ) {
                Text(
                    text = if (appLanguage == "fa") "EN" else "FA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Theme toggle (Day/Night switcher)
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .testTag("theme_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.Star else Icons.Default.Settings,
                    contentDescription = "Theme Toggle",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingBarNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    appLanguage: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = if (isDark) Color.Black else Color.Gray.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.95f)
            else Color.White.copy(alpha = 0.95f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) Color(0xFF49454F).copy(alpha = 0.6f)
            else Color(0xFFE6E1E5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("interpret", Icons.Default.Search, T.get("nav_interpret", appLanguage)),
                Triple("history", Icons.Default.List, T.get("nav_history", appLanguage)),
                Triple("bookmarks", Icons.Default.Star, T.get("nav_bookmarks", appLanguage))
            )

            tabs.forEach { (tabId, icon, label) ->
                val isSelected = activeTab == tabId
                val backgroundAlpha by animateFloatAsState(if (isSelected) 0.12f else 0f, label = "tab_bg")
                val scale by animateFloatAsState(if (isSelected) 1.05f else 0.95f, label = "tab_scale")

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(tabId) }
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = backgroundAlpha
                            )
                        )
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                        .testTag("tab_$tabId"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        AnimatedVisibility(visible = isSelected) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InterpretTabContent(
    viewModel: DreamViewModel,
    appLanguage: String,
    isDark: Boolean
) {
    val userDreamInput by viewModel.userDreamInput.collectAsState()
    val isInterpretationLoading by viewModel.isInterpretationLoading.collectAsState()
    val interpretationError by viewModel.interpretationError.collectAsState()
    val currentAnalysis by viewModel.currentAnalysis.collectAsState()
    val isCurrentDreamBookmarked by viewModel.isCurrentDreamBookmarked.collectAsState()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Smooth loading pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_button")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(bottom = 120.dp), // Extra space to not overlap the floating nav bar
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // If an active interpretation is loaded, display it beautifully! Otherwise show Input Form
        if (currentAnalysis != null) {
            InterpretationResultCard(
                analysis = currentAnalysis!!,
                isBookmarked = isCurrentDreamBookmarked,
                appLanguage = appLanguage,
                isDark = isDark,
                onBookmarkToggle = { viewModel.toggleBookmarkCurrentDream() },
                onInterpretAnother = { viewModel.clearCurrentAnalysis() }
            )
        } else {
            // Elegant Welcome Header
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "✨",
                fontSize = 44.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = T.get("input_dream_below", appLanguage),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = T.get("welcome_sub", appLanguage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Dream Prompt Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = if (isDark) Color.Black else Color.LightGray.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.85f)
                    else Color.White.copy(alpha = 0.9f)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF49454F).copy(alpha = 0.6f) else Color(0xFFE6E1E5)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = userDreamInput,
                        onValueChange = { viewModel.onDreamInputChanged(it) },
                        placeholder = {
                            Text(
                                text = T.get("prompt_placeholder", appLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp)
                            .testTag("dream_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (userDreamInput.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onDreamInputChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear input",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error display
            if (!interpretationError.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⚠️", fontSize = 20.sp)
                        Text(
                            text = if (interpretationError == "API_KEY_MISSING") {
                                T.get("error_no_key", appLanguage)
                            } else {
                                T.get("error_generic", appLanguage)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Beautiful glowing interpretation button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.interpretUserDream()
                },
                enabled = userDreamInput.isNotBlank() && !isInterpretationLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(if (isInterpretationLoading) 1.0f else pulseScale)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isInterpretationLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = T.get("interpreting", appLanguage),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "💫",
                            fontSize = 20.sp
                        )
                        Text(
                            text = T.get("interpret_btn", appLanguage),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = if (userDreamInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InterpretationResultCard(
    analysis: DreamAnalysis,
    isBookmarked: Boolean,
    appLanguage: String,
    isDark: Boolean,
    onBookmarkToggle: () -> Unit,
    onInterpretAnother: () -> Unit
) {
    // Dynamic theme color parsing with fallback
    val parsedColor = remember(analysis.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(analysis.colorHex))
        } catch (e: Exception) {
            Color(0xFF6E5DF5)
        }
    }

    var selectedLangView by remember { mutableStateOf(appLanguage) }

    // Card frame transition
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = parsedColor.copy(alpha = 0.3f),
                spotColor = parsedColor
            )
            .testTag("result_card"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.9f)
            else Color.White.copy(alpha = 0.95f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isDark) Color(0xFF49454F).copy(alpha = 0.7f) else Color(0xFFCAC4D0).copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header Action controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category/Mood Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(parsedColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                    )
                    Text(
                        text = T.get(analysis.mood.lowercase(), selectedLangView),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) parsedColor else parsedColor.copy(alpha = 0.85f),
                        fontStyle = FontStyle.Italic
                    )
                }

                // Header Action tools
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language switcher for THIS card block
                    IconButton(
                        onClick = { selectedLangView = if (selectedLangView == "fa") "en" else "fa" },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Flip Language Translation",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Save / Bookmark
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            .testTag("bookmark_toggle")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.FavoriteBorder,
                            contentDescription = "Save Dream",
                            tint = if (isBookmarked) Color(0xFFFFD54F) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Poetic Dream Title
            Text(
                text = if (selectedLangView == "fa") analysis.titleFa else analysis.titleEn,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 0.2.sp,
                    lineHeight = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interpretation Text Container with Left (EN) or Right (FA) border accent
            val isFa = selectedLangView == "fa"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isFa) 16.dp else 4.dp,
                            topEnd = if (isFa) 4.dp else 16.dp,
                            bottomEnd = if (isFa) 4.dp else 16.dp,
                            bottomStart = if (isFa) 16.dp else 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                    .drawBehind {
                        val strokeWidth = 3.dp.toPx()
                        if (isFa) {
                            drawLine(
                                color = parsedColor,
                                start = Offset(size.width - strokeWidth / 2, 0f),
                                end = Offset(size.width - strokeWidth / 2, size.height),
                                strokeWidth = strokeWidth
                            )
                        } else {
                            drawLine(
                                color = parsedColor,
                                start = Offset(strokeWidth / 2, 0f),
                                end = Offset(strokeWidth / 2, size.height),
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = if (selectedLangView == "fa") analysis.interpretationFa else analysis.interpretationEn,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 26.sp,
                        letterSpacing = 0.1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Symbols list title
            Text(
                text = T.get("symbols_title", selectedLangView),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = T.get("tap_to_expand", selectedLangView),
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Symbols layout: custom expandable card list
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                analysis.symbols.forEach { symbol ->
                    SymbolCardRow(symbol = symbol, currentViewLang = selectedLangView, moodColor = parsedColor, isDark = isDark)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Instant translation toggle button & New Dream buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { selectedLangView = if (selectedLangView == "fa") "en" else "fa" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = T.get("translate_toggle", selectedLangView),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onInterpretAnother,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = parsedColor.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New dream",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Text(
                            text = T.get("close_btn", selectedLangView),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SymbolCardRow(
    symbol: DreamSymbol,
    currentViewLang: String,
    moodColor: Color,
    isDark: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.5f)
            else Color(0xFFF4F2F7).copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isExpanded) moodColor.copy(alpha = 0.7f)
            else if (isDark) Color(0xFF49454F).copy(alpha = 0.5f) else Color(0xFFE6E1E5)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Symbol display (Fa and En side-by-side to exhibit rich translation skill)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(moodColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            fontSize = 12.sp,
                            color = moodColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (currentViewLang == "fa") symbol.symbolFa else symbol.symbolEn,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Secondary language tag snippet to enrich design
                    Text(
                        text = "(${if (currentViewLang == "fa") symbol.symbolEn else symbol.symbolFa})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.Star else Icons.Default.PlayArrow,
                    contentDescription = "Expand/Collapse",
                    tint = moodColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (currentViewLang == "fa") symbol.meaningFa else symbol.meaningEn,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryTabContent(
    viewModel: DreamViewModel,
    appLanguage: String,
    isDark: Boolean,
    isBookmarkView: Boolean
) {
    val dreamsFlow = if (isBookmarkView) viewModel.bookmarkedDreams else viewModel.historyDreams
    val dreamsList by dreamsFlow.collectAsState()

    var confirmDeleteIndex by remember { mutableStateOf<DreamEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 120.dp), // Safe window clearance for bottom navigation
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // History Database list header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBookmarkView) T.get("nav_bookmarks", appLanguage) else T.get("nav_history", appLanguage),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!isBookmarkView && dreamsList.isNotEmpty()) {
                TextButton(
                    onClick = { confirmDeleteIndex = dreamsList.firstOrNull() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear all database cache",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = T.get("clear_history", appLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dreamsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBookmarkView) "⭐" else "📜",
                        fontSize = 44.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBookmarkView) {
                            T.get("bookmarks_empty", appLanguage)
                        } else {
                            T.get("history_empty", appLanguage)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(dreamsList, key = { it.id }) { dream ->
                    HistoryDreamRowCard(
                        entity = dream,
                        lang = appLanguage,
                        isDark = isDark,
                        onSelect = { viewModel.selectHistoryDream(dream) },
                        onDelete = { viewModel.deleteDream(dream) }
                    )
                }
            }
        }
    }

    // Modal popup confirm dialog to wipe history safely
    if (confirmDeleteIndex != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteIndex = null },
            title = {
                Text(
                    text = T.get("history_confirm", appLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        confirmDeleteIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(T.get("confirm_yes", appLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteIndex = null }) {
                    Text(T.get("confirm_cancel", appLanguage))
                }
            },
            containerColor = if (isDark) Color(0xFF1C1B1F) else Color.White
        )
    }
}

@Keep
@Composable
fun HistoryDreamRowCard(
    entity: DreamEntity,
    lang: String,
    isDark: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val themeColor = remember(entity.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(entity.colorHex))
        } catch (e: Exception) {
            Color(0xFFD0BCFF)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = themeColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.8f)
            else Color.White.copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) Color(0xFF49454F).copy(alpha = 0.4f) else Color(0xFFE6E1E5)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ethereal circular indicator dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(themeColor)
                    )

                    // Title
                    Text(
                        text = if (lang == "fa") entity.titleFa else entity.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Prompt excerpt
                Text(
                    text = entity.userPrompt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action delete row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (entity.isBookmarked) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Saved",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsPanelDialog(
    customApiKey: String,
    onSaveKey: (String) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit,
    appLanguage: String,
    isDark: Boolean
) {
    var keyInput by remember { mutableStateOf(customApiKey) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF2B2930).copy(alpha = 0.95f)
                else Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF49454F).copy(alpha = 0.6f) else Color(0xFFE6E1E5)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header icon
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = T.get("api_settings", appLanguage),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = T.get("app_about", appLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // API Input Box
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    placeholder = {
                        Text(
                            text = T.get("api_placeholder", appLanguage),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_field"),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    trailingIcon = {
                        if (keyInput.isNotEmpty()) {
                            IconButton(onClick = { keyInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "clear field",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = T.get("api_hint", appLanguage),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Control Actions grid
                Button(
                    onClick = { onSaveKey(keyInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("save_api_key_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = T.get("api_save_btn", appLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showConfirmClearDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "wipe history",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = T.get("clear_history", appLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = T.get("close_btn", appLanguage),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = {
                Text(
                    text = T.get("history_confirm", appLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(T.get("confirm_yes", appLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) {
                    Text(T.get("confirm_cancel", appLanguage))
                }
            },
            containerColor = if (isDark) Color(0xFF1C1E32) else Color.White
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
