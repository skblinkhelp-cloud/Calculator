package com.sk.calculator_aisupported.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificPillButton(
    text: String = "",
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    containerColor: Color = Color(0xFF252528),
    contentColor: Color = Color.White,
    pressedContainerColor: Color = Color(0xFF424245),
    border: BorderStroke? = null,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedBg by animateColorAsState(
        targetValue = if (isPressed) pressedContainerColor else containerColor,
        label = "btnBg"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = CircleShape,
        color = animatedBg,
        contentColor = contentColor,
        border = border,
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = text,
                    fontSize = fontSize.sp,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun IosCalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    pressedContainerColor: Color,
    pressedContentColor: Color = contentColor,
    border: BorderStroke? = null,
    isWide: Boolean = false,
    fontSize: Int = 36,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedBg by animateColorAsState(
        targetValue = if (isPressed) pressedContainerColor else containerColor,
        label = "btnBg"
    )
    val animatedFg by animateColorAsState(
        targetValue = if (isPressed) pressedContentColor else contentColor,
        label = "btnFg"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.then(
            if (!isWide) Modifier.aspectRatio(1f) else Modifier.fillMaxHeight()
        ),
        shape = CircleShape,
        color = animatedBg,
        contentColor = animatedFg,
        border = border,
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = if (isWide) Alignment.CenterStart else Alignment.Center,
            modifier = if (isWide) Modifier.padding(start = 32.dp) else Modifier
        ) {
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CalculatorGrid(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val currentView = LocalView.current
    val mode by viewModel.calculatorMode.collectAsState()
    val isRadMode by viewModel.isRadMode.collectAsState()
    val is2ndActive by viewModel.is2ndActive.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Highly Polished Scientific Theme Palette
    val sciBg = if (isDarkTheme) Color(0xFF1E1E21) else Color(0xFFFFFFFF)
    val sciPressed = if (isDarkTheme) Color(0xFF38383B) else Color(0xFFE0E7FF)
    val sciText = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF4F46E5)
    val sciBorder = if (!isDarkTheme) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null

    val topOpBg = if (isDarkTheme) Color(0xFF505054) else Color(0xFFE2E8F0)
    val topOpPressed = if (isDarkTheme) Color(0xFF6E6E73) else Color(0xFFCBD5E1)
    val topOpText = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    val numBg = if (isDarkTheme) Color(0xFF252528) else Color(0xFFFFFFFF)
    val numPressed = if (isDarkTheme) Color(0xFF424245) else Color(0xFFF1F5F9)
    val numText = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val numBorder = if (!isDarkTheme) BorderStroke(1.dp, Color(0xFFCBD5E1)) else null

    val accentBg = Color(0xFF635BFF)
    val accentPressed = Color(0xFF817BFF)

    val gap = 6.dp

    if (mode == CalculatorViewModel.CalculatorMode.BASIC) {
        // Standard Basic Calculator Layout
        val lightGrayBg = if (isDarkTheme) Color(0xFFA5A5A5) else Color(0xFFE2E8F0)
        val lightGrayPressed = if (isDarkTheme) Color(0xFFD9D9D9) else Color(0xFFCBD5E1)
        val lightGrayText = Color(0xFF0F172A)

        val darkGrayBg = if (isDarkTheme) Color(0xFF333333) else Color(0xFFFFFFFF)
        val darkGrayPressed = if (isDarkTheme) Color(0xFF636366) else Color(0xFFF1F5F9)
        val darkGrayText = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF0F172A)

        val basicGap = 12.dp

        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(basicGap)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(basicGap)) {
                IosCalculatorButton("C", Modifier.weight(1f), lightGrayBg, lightGrayText, lightGrayPressed, fontSize = 32) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onClear()
                }
                IosCalculatorButton("+/-", Modifier.weight(1f), lightGrayBg, lightGrayText, lightGrayPressed, fontSize = 28) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onToggleSign()
                }
                IosCalculatorButton("%", Modifier.weight(1f), lightGrayBg, lightGrayText, lightGrayPressed, fontSize = 30) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onPercentage()
                }
                IosCalculatorButton("÷", Modifier.weight(1f), accentBg, Color.White, accentPressed, fontSize = 40) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("÷")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(basicGap)) {
                IosCalculatorButton("7", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("7")
                }
                IosCalculatorButton("8", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("8")
                }
                IosCalculatorButton("9", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("9")
                }
                IosCalculatorButton("×", Modifier.weight(1f), accentBg, Color.White, accentPressed, fontSize = 36) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("×")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(basicGap)) {
                IosCalculatorButton("4", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("4")
                }
                IosCalculatorButton("5", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("5")
                }
                IosCalculatorButton("6", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("6")
                }
                IosCalculatorButton("-", Modifier.weight(1f), accentBg, Color.White, accentPressed, fontSize = 42) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("-")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(basicGap)) {
                IosCalculatorButton("1", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("1")
                }
                IosCalculatorButton("2", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("2")
                }
                IosCalculatorButton("3", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("3")
                }
                IosCalculatorButton("+", Modifier.weight(1f), accentBg, Color.White, accentPressed, fontSize = 36) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("+")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(basicGap)
            ) {
                IosCalculatorButton("0", Modifier.weight(2f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder, isWide = true, fontSize = 36) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("0")
                }
                IosCalculatorButton(".", Modifier.weight(1f), darkGrayBg, darkGrayText, darkGrayPressed, border = numBorder, fontSize = 36) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(".")
                }
                IosCalculatorButton("=", Modifier.weight(1f), accentBg, Color.White, accentPressed, fontSize = 36) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onEvaluate()
                }
            }
        }
    } else {
        // Scientific 6-Column Grid matching Scientific Calculator layout
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            // Row 1: (, ), mc, m+, m-, mr
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("(", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("(")
                }
                ScientificPillButton(")", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(")")
                }
                ScientificPillButton("mc", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onMemoryClear()
                }
                ScientificPillButton("m+", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onMemoryAdd()
                }
                ScientificPillButton("m-", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onMemorySubtract()
                }
                ScientificPillButton("mr", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onMemoryRecall()
                }
            }

            // Row 2: 2nd, x² / x³, x³ / yˣ, xʸ, eˣ, 10ˣ
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton(
                    "2ⁿᵈ",
                    Modifier.weight(1f),
                    null,
                    if (is2ndActive) accentBg else sciBg,
                    if (is2ndActive) Color.White else sciText,
                    sciPressed,
                    border = if (is2ndActive) null else sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.toggle2nd()
                }
                ScientificPillButton(if (is2ndActive) "x³" else "x²", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); if (is2ndActive) viewModel.onCube() else viewModel.onSquare()
                }
                ScientificPillButton(if (is2ndActive) "yˣ" else "x³", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("^")
                }
                ScientificPillButton("xʸ", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("^")
                }
                ScientificPillButton("eˣ", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("e^")
                }
                ScientificPillButton("10ˣ", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("10^")
                }
            }

            // Row 3: 1/x, ²√x / ³√x, ³√x / ʸ√x, ʸ√x, ln / log₂, log₁₀
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("1/x", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onReciprocal()
                }
                ScientificPillButton(if (is2ndActive) "³√x" else "²√x", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "³√(" else "√(")
                }
                ScientificPillButton(if (is2ndActive) "ʸ√x" else "³√x", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) " ʸ√ " else "³√(")
                }
                ScientificPillButton("ʸ√x", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(" ʸ√ ")
                }
                ScientificPillButton(if (is2ndActive) "log₂" else "ln", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "log2(" else "ln(")
                }
                ScientificPillButton("log₁₀", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("log(")
                }
            }

            // Row 4: x!, sin/asin, cos/acos, tan/atan, e, EE
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("x!", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onFactorial()
                }
                ScientificPillButton(
                    if (is2ndActive) "sin⁻¹" else "sin",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "asin(" else "sin(")
                }
                ScientificPillButton(
                    if (is2ndActive) "cos⁻¹" else "cos",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "acos(" else "cos(")
                }
                ScientificPillButton(
                    if (is2ndActive) "tan⁻¹" else "tan",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "atan(" else "tan(")
                }
                ScientificPillButton("e", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("e")
                }
                ScientificPillButton("EE", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("EE")
                }
            }

            // Row 5: Rand, sinh/asinh, cosh/acosh, tanh/atanh, π, Rad/Deg
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("Rand", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onRandom()
                }
                ScientificPillButton(
                    if (is2ndActive) "sinh⁻¹" else "sinh",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "asinh(" else "sinh(")
                }
                ScientificPillButton(
                    if (is2ndActive) "cosh⁻¹" else "cosh",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "acosh(" else "cosh(")
                }
                ScientificPillButton(
                    if (is2ndActive) "tanh⁻¹" else "tanh",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(if (is2ndActive) "atanh(" else "tanh(")
                }
                ScientificPillButton("π", Modifier.weight(1f), null, sciBg, sciText, sciPressed, border = sciBorder, fontWeight = FontWeight.Medium) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("π")
                }
                ScientificPillButton(
                    if (isRadMode) "Rad" else "Deg",
                    Modifier.weight(1f),
                    null,
                    sciBg,
                    sciText,
                    sciPressed,
                    border = sciBorder,
                    fontWeight = FontWeight.Medium
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.toggleRadDeg()
                }
            }

            // Row 6: ⌫ (weight 2), AC (weight 2), % (weight 1), ÷ (weight 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton(
                    text = "",
                    modifier = Modifier.weight(2f),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = topOpText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    containerColor = topOpBg,
                    pressedContainerColor = topOpPressed
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onBackspace()
                }
                ScientificPillButton(
                    "AC",
                    Modifier.weight(2f),
                    null,
                    topOpBg,
                    topOpText,
                    topOpPressed,
                    fontSize = 18,
                    fontWeight = FontWeight.SemiBold
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onClear()
                }
                ScientificPillButton(
                    "%",
                    Modifier.weight(1f),
                    null,
                    topOpBg,
                    topOpText,
                    topOpPressed,
                    fontSize = 18,
                    fontWeight = FontWeight.SemiBold
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onPercentage()
                }
                ScientificPillButton(
                    "÷",
                    Modifier.weight(1f),
                    null,
                    accentBg,
                    Color.White,
                    accentPressed,
                    fontSize = 24,
                    fontWeight = FontWeight.Bold
                ) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("÷")
                }
            }

            // Row 7: 7 (weight 2), 8 (weight 2), 9 (weight 1), × (weight 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("7", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("7")
                }
                ScientificPillButton("8", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("8")
                }
                ScientificPillButton("9", Modifier.weight(1f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("9")
                }
                ScientificPillButton("×", Modifier.weight(1f), null, accentBg, Color.White, accentPressed, fontSize = 22, fontWeight = FontWeight.Bold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("×")
                }
            }

            // Row 8: 4 (weight 2), 5 (weight 2), 6 (weight 1), - (weight 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("4", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("4")
                }
                ScientificPillButton("5", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("5")
                }
                ScientificPillButton("6", Modifier.weight(1f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("6")
                }
                ScientificPillButton("-", Modifier.weight(1f), null, accentBg, Color.White, accentPressed, fontSize = 24, fontWeight = FontWeight.Bold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("-")
                }
            }

            // Row 9: 1 (weight 2), 2 (weight 2), 3 (weight 1), + (weight 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("1", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("1")
                }
                ScientificPillButton("2", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("2")
                }
                ScientificPillButton("3", Modifier.weight(1f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("3")
                }
                ScientificPillButton("+", Modifier.weight(1f), null, accentBg, Color.White, accentPressed, fontSize = 22, fontWeight = FontWeight.Bold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("+")
                }
            }

            // Row 10: +/- (weight 2), 0 (weight 2), . (weight 1), = (weight 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                ScientificPillButton("+/-", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onToggleSign()
                }
                ScientificPillButton("0", Modifier.weight(2f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed("0")
                }
                ScientificPillButton(".", Modifier.weight(1f), null, numBg, numText, numPressed, border = numBorder, fontSize = 18, fontWeight = FontWeight.SemiBold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onDigitPressed(".")
                }
                ScientificPillButton("=", Modifier.weight(1f), null, accentBg, Color.White, accentPressed, fontSize = 22, fontWeight = FontWeight.Bold) {
                    viewModel.triggerHapticFeedback(currentView); viewModel.onEvaluate()
                }
            }
        }
    }
}