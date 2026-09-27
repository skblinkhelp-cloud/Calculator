package com.sk.calculator_aisupported.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sk.calculator_aisupported.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Calculator",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "SK Software",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF635BFF)
            )
        }
    }
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    val density = displayMetrics.density
    val adWidth = (widthPixels / density).toInt()

    val adaptiveAdSize = remember(adWidth) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(adaptiveAdSize)
                    // Official Google AdMob Test Banner Ad Unit ID
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
fun CalculatorInterface(viewModel: CalculatorViewModel) {
    var showSplashScreen by remember { mutableStateOf(true) }

    if (showSplashScreen) {
        SplashScreen(onTimeout = { showSplashScreen = false })
    } else {
        val formulaState by viewModel.formula.collectAsState()
        val livePreviewState by viewModel.livePreview.collectAsState()
        val currentMode by viewModel.calculatorMode.collectAsState()
        val isRadMode by viewModel.isRadMode.collectAsState()
        val isDarkTheme by viewModel.isDarkTheme.collectAsState()
        val isHapticEnabled by viewModel.isHapticEnabled.collectAsState()
        val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()

        var showHistorySheet by remember { mutableStateOf(false) }
        var showSettingsSheet by remember { mutableStateOf(false) }
        var showModeSheet by remember { mutableStateOf(false) }

        var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
        var showTermsDialog by remember { mutableStateOf(false) }
        var showAboutDialog by remember { mutableStateOf(false) }

        val bgSurfaceColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
        val iconTintColor = if (isDarkTheme) Color.White else Color(0xFF1C1C1E)
        val displayTextColor = if (isDarkTheme) Color.White else Color(0xFF000000)
        val mutedTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (isDarkTheme) Color.Black else Color(0xFFF2F2F7)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Main Top & Middle Content (Header, Display & Keypad)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Header Bar: History & Settings on Left, Calculator Modes Window on Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Top Left: History & Settings Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = { showHistorySheet = true },
                                shape = CircleShape,
                                color = bgSurfaceColor,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History",
                                        tint = iconTintColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = { showSettingsSheet = true },
                                shape = CircleShape,
                                color = bgSurfaceColor,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = iconTintColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Top Right: Calculator Modes Professional Window Button
                        Surface(
                            onClick = { showModeSheet = true },
                            shape = CircleShape,
                            color = bgSurfaceColor,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Calculator Modes",
                                    tint = iconTintColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Display Area (Multi-line upward expansion into free space)
                    var totalDragX by remember { mutableFloatStateOf(0f) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragStart = { totalDragX = 0f },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDragX += dragAmount
                                    },
                                    onDragEnd = {
                                        if (totalDragX < -50f) { // Swipe Left -> Backspace
                                            viewModel.onBackspace()
                                        } else if (totalDragX > 50f) { // Swipe Right -> Evaluate
                                            viewModel.onEvaluate()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        val scrollState = rememberScrollState()
                        val formulaText = formulaState
                        val displayText = if (formulaText.isEmpty()) "0" else formulaText

                        LaunchedEffect(displayText) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Mode indicator badge (DEG vs RAD) if in scientific mode
                            if (currentMode == CalculatorViewModel.CalculatorMode.SCIENTIFIC) {
                                Text(
                                    text = if (isRadMode) "RAD" else "DEG",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = mutedTextColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            // Secondary Live Preview expression if actively calculating
                            if (formulaText.isNotEmpty() && livePreviewState.isNotEmpty() && livePreviewState != displayText) {
                                Text(
                                    text = "= $livePreviewState",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = mutedTextColor,
                                    textAlign = TextAlign.End,
                                    maxLines = 5,
                                    softWrap = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // Dynamic Font Sizing & Multi-line wrapping expanding upwards into free space
                            val fontSizeSp = when {
                                displayText.length > 30 -> 24.sp
                                displayText.length > 20 -> 30.sp
                                displayText.length > 14 -> 38.sp
                                displayText.length > 9 -> 50.sp
                                displayText.length > 5 -> 64.sp
                                else -> 76.sp
                            }

                            Text(
                                text = displayText,
                                fontSize = fontSizeSp,
                                fontWeight = FontWeight.Light,
                                color = displayTextColor,
                                textAlign = TextAlign.End,
                                lineHeight = fontSizeSp * 1.15f,
                                maxLines = 10,
                                softWrap = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculator Keypad Grid
                    CalculatorGrid(
                        viewModel = viewModel
                    )
                }

                // Full-Width Adaptive Banner Ad at the Very Bottom
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkTheme) Color.Black else Color(0xFFF2F2F7))
                )
            }

            if (showHistorySheet) {
                HistorySheet(
                    viewModel = viewModel,
                    onDismiss = { showHistorySheet = false }
                )
            }

            if (showModeSheet) {
                CalculatorModeSheet(
                    currentMode = currentMode,
                    isDarkTheme = isDarkTheme,
                    onSelectMode = { mode ->
                        viewModel.setCalculatorMode(mode)
                        showModeSheet = false
                    },
                    onDismiss = { showModeSheet = false }
                )
            }

            if (showSettingsSheet) {
                SettingsSheet(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    isHapticEnabled = isHapticEnabled,
                    isSoundEnabled = isSoundEnabled,
                    onOpenPrivacyPolicy = { showPrivacyPolicyDialog = true },
                    onOpenTerms = { showTermsDialog = true },
                    onOpenAbout = { showAboutDialog = true },
                    onDismiss = { showSettingsSheet = false }
                )
            }

            if (showPrivacyPolicyDialog) {
                PrivacyPolicyDialog(
                    isDarkTheme = isDarkTheme,
                    onDismiss = { showPrivacyPolicyDialog = false }
                )
            }

            if (showTermsDialog) {
                TermsOfServiceDialog(
                    isDarkTheme = isDarkTheme,
                    onDismiss = { showTermsDialog = false }
                )
            }

            if (showAboutDialog) {
                AboutAppDialog(
                    isDarkTheme = isDarkTheme,
                    onDismiss = { showAboutDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorModeSheet(
    currentMode: CalculatorViewModel.CalculatorMode,
    isDarkTheme: Boolean,
    onSelectMode: (CalculatorViewModel.CalculatorMode) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetContainerColor = if (isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetContainerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Calculator Workspaces 🧮",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Choose a workspace mode optimized for your calculation needs.",
                fontSize = 14.sp,
                color = subTextColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Basic Mode Card
            Surface(
                onClick = { onSelectMode(CalculatorViewModel.CalculatorMode.BASIC) },
                color = if (currentMode == CalculatorViewModel.CalculatorMode.BASIC) Color(0xFF635BFF).copy(alpha = 0.15f) else cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧮", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Basic Calculator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Standard arithmetic with large buttons (+, -, ×, ÷, %, +/-). Fast and clean everyday calculations.",
                            fontSize = 13.sp,
                            color = subTextColor
                        )
                    }
                    if (currentMode == CalculatorViewModel.CalculatorMode.BASIC) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = Color(0xFF635BFF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scientific Mode Card
            Surface(
                onClick = { onSelectMode(CalculatorViewModel.CalculatorMode.SCIENTIFIC) },
                color = if (currentMode == CalculatorViewModel.CalculatorMode.SCIENTIFIC) Color(0xFF635BFF).copy(alpha = 0.15f) else cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scientific Calculator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Full 6-column matrix with trigonometry (sin, cos, tan), powers, roots, logarithms, hyperbolics, and memory functions.",
                            fontSize = 13.sp,
                            color = subTextColor
                        )
                    }
                    if (currentMode == CalculatorViewModel.CalculatorMode.SCIENTIFIC) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = Color(0xFF635BFF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    viewModel: CalculatorViewModel,
    isDarkTheme: Boolean,
    isHapticEnabled: Boolean,
    isSoundEnabled: Boolean,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAbout: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetContainerColor = if (isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetContainerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Settings ⚙️",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme Switcher Row
            Surface(
                color = cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isDarkTheme) "Dark Mode 🌙" else "Light Mode (White) ☀️",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = if (isDarkTheme) "Switch to clean white Light theme" else "Switch to dark charcoal theme",
                            fontSize = 12.sp,
                            color = subTextColor
                        )
                    }
                    Switch(
                        checked = !isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF635BFF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Button Click Sound / Tune Toggle Row
            Surface(
                color = cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Button Click Sound 🎵",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Audio click tune effect when tapping keys",
                            fontSize = 12.sp,
                            color = subTextColor
                        )
                    }
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { viewModel.toggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF635BFF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Haptic Feedback Toggle Row
            Surface(
                color = cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Haptic Vibration 📳",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Tactile feedback when pressing buttons",
                            fontSize = 12.sp,
                            color = subTextColor
                        )
                    }
                    Switch(
                        checked = isHapticEnabled,
                        onCheckedChange = { viewModel.toggleHaptic() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF635BFF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("LEGAL & APP INFO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = subTextColor)

            Spacer(modifier = Modifier.height(8.dp))

            // Legal & About Rows
            Surface(
                color = cardBgColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Privacy Policy
                    TextButton(
                        onClick = {
                            onDismiss()
                            onOpenPrivacyPolicy()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF635BFF))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Privacy Policy 🔒", fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Medium)
                        }
                    }

                    HorizontalDivider(color = subTextColor.copy(alpha = 0.2f))

                    // Terms of Service
                    TextButton(
                        onClick = {
                            onDismiss()
                            onOpenTerms()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF635BFF))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Terms of Service 📄", fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Medium)
                        }
                    }

                    HorizontalDivider(color = subTextColor.copy(alpha = 0.2f))

                    // About App
                    TextButton(
                        onClick = {
                            onDismiss()
                            onOpenAbout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF635BFF))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("About App ℹ️", fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrivacyPolicyDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val dialogBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBgColor,
        title = {
            Text("Privacy Policy 🔒", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text("Effective Date: 2026\nDeveloper: SK Software\nContact: sk.blink.help@gmail.com\n", fontSize = 12.sp, color = subTextColor)
                Text("1. Zero Data Collection", fontWeight = FontWeight.Bold, color = textColor)
                Text("Calculator respects your privacy. We do NOT collect, store, or transmit any personal information, location, or usage data.\n", fontSize = 13.sp, color = subTextColor)

                Text("2. Local Storage", fontWeight = FontWeight.Bold, color = textColor)
                Text("Your calculation history and app settings (Theme preference, Haptic toggles, Sound preferences) are saved strictly locally on your device using Jetpack DataStore. No data ever leaves your device.\n", fontSize = 13.sp, color = subTextColor)

                Text("3. Offline Functionality", fontWeight = FontWeight.Bold, color = textColor)
                Text("All scientific and arithmetic evaluations are computed 100% offline on your device processor.\n", fontSize = 13.sp, color = subTextColor)

                Text("4. Google Play Compliance", fontWeight = FontWeight.Bold, color = textColor)
                Text("This policy complies with Google Play's Developer Privacy Guidelines.", fontSize = 13.sp, color = subTextColor)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF))
            ) {
                Text("Got It", color = Color.White)
            }
        }
    )
}

@Composable
fun TermsOfServiceDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val dialogBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBgColor,
        title = {
            Text("Terms of Service 📄", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text("Terms & Conditions\nDeveloper: SK Software\nContact: sk.blink.help@gmail.com\n", fontSize = 12.sp, color = subTextColor)
                Text("1. Use of Service", fontWeight = FontWeight.Bold, color = textColor)
                Text("Calculator is provided for general calculation purposes. While high precision math algorithms are used, users are encouraged to verify calculations for critical engineering or financial applications.\n", fontSize = 13.sp, color = subTextColor)

                Text("2. Intellectual Property", fontWeight = FontWeight.Bold, color = textColor)
                Text("All UI designs, logos, and custom layout matrices are protected under applicable software licenses.", fontSize = 13.sp, color = subTextColor)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF))
            ) {
                Text("Accept", color = Color.White)
            }
        }
    )
}

@Composable
fun AboutAppDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val dialogBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBgColor,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧮", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            }
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Version 1.0.0 (2026)", fontWeight = FontWeight.Medium, color = textColor)
                Text("Developer: SK Software", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF635BFF))
                Text("Support: sk.blink.help@gmail.com", fontSize = 12.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text("A high-precision scientific calculator app built with Jetpack Compose, Kotlin, and the exp4j math engine.", fontSize = 13.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(12.dp))
                Text("• 100% Offline & Private\n• Jetpack Compose & Material 3\n• Modern Light / Dark Theme\n• Dynamic Font Auto-Scaling", fontSize = 13.sp, color = subTextColor)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF))
            ) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    viewModel: CalculatorViewModel,
    onDismiss: () -> Unit
) {
    val historyItems by viewModel.historyTape.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val sheetContainerColor = if (isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val cardBgColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color(0xFF8E8E93) else Color(0xFF6C6C70)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetContainerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History 📜",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearHistoryTape() }) {
                        Text("Clear All", color = Color(0xFFFF453A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No history entries yet", color = subTextColor, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    items(historyItems, key = { it.id }) { record ->
                        Surface(
                            onClick = {
                                viewModel.onSelectHistoryItem(record)
                                onDismiss()
                            },
                            color = cardBgColor,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(record.formulaExpression, fontSize = 15.sp, color = subTextColor)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(record.solvedResult, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
                            }
                        }
                    }
                }
            }
        }
    }
}