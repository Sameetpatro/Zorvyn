package com.example.zorvyn_task.ui.addtransaction

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.LocalAppColors

private val EXPENSE_CATEGORIES = listOf(
    "🍔 Food","🚗 Transport","🛍️ Shopping","🏥 Health",
    "🎬 Entertainment","🏠 Housing","📱 Utilities","📚 Education","✈️ Travel","💡 Other"
)
private val INCOME_CATEGORIES = listOf(
    "💼 Salary","💰 Freelance","📈 Investment","🎁 Gift","💳 Refund","💡 Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    // Solid field background so typed text is always readable
    val fieldContainerColor = if (colors.isDark) Color(0xFF1A2040) else Color(0xFFFFFFFF)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor      = colors.textPrimary,
        unfocusedTextColor    = colors.textPrimary,
        focusedBorderColor    = colors.accentBlue,
        unfocusedBorderColor  = colors.glassBorder,
        focusedLabelColor     = colors.accentBlue,
        unfocusedLabelColor   = colors.textSecondary,
        cursorColor           = colors.accentBlue,
        focusedContainerColor   = fieldContainerColor,
        unfocusedContainerColor = fieldContainerColor,
        focusedPlaceholderColor   = colors.textTertiary,
        unfocusedPlaceholderColor = colors.textTertiary,
    )

    LaunchedEffect(state.saved) { if (state.saved) onNavigateBack() }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Add Transaction",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = colors.textPrimary, fontWeight = FontWeight.Light
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.textPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                TypeToggle(selected = state.type, onSelect = viewModel::setType)

                // ── Amount ────────────────────────────────────────────────────
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "AMOUNT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colors.textSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "₹",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = if (state.type == TransactionType.INCOME)
                                    colors.accentGreen else colors.accentRed
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AmountField(value = state.amount, onValueChange = viewModel::setAmount)
                    }
                }

                // ── Category ──────────────────────────────────────────────────
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "CATEGORY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colors.textSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val categories =
                        if (state.type == TransactionType.EXPENSE) EXPENSE_CATEGORIES
                        else INCOME_CATEGORIES
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            CategoryChip(
                                label    = cat,
                                selected = state.category == cat,
                                onSelect = { viewModel.setCategory(cat) }
                            )
                        }
                    }
                }

                // ── Note ──────────────────────────────────────────────────────
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "NOTE (optional)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colors.textSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value          = state.note,
                        onValueChange  = viewModel::setNote,
                        placeholder    = { Text("Add a note…") },
                        modifier       = Modifier.fillMaxWidth(),
                        singleLine     = false,
                        maxLines       = 3,
                        colors         = fieldColors,
                        shape          = RoundedCornerShape(12.dp)
                    )
                }

                // ── Error ─────────────────────────────────────────────────────
                AnimatedVisibility(visible = state.error != null) {
                    Text(
                        state.error ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.accentRed),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ── Save button ───────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                if (state.type == TransactionType.INCOME)
                                    listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.7f))
                                else
                                    listOf(colors.accentRed, colors.accentRed.copy(alpha = 0.7f))
                            )
                        )
                        .clickable(enabled = !state.isSaving) { viewModel.save() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color    = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save Transaction",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White, fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun AmountField(value: String, onValueChange: (String) -> Unit) {
    val colors = LocalAppColors.current
    BasicTextField(
        value         = value,
        onValueChange = { new -> onValueChange(new.filter { c -> c.isDigit() || c == '.' }) },
        textStyle     = MaterialTheme.typography.headlineLarge.copy(
            color      = colors.textPrimary,   // always theme-correct colour
            fontWeight = FontWeight.Light
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        cursorBrush     = SolidColor(colors.accentBlue),
        singleLine      = true,
        decorationBox   = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    "0.00",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color      = colors.textTertiary,
                        fontWeight = FontWeight.Light
                    )
                )
            }
            innerTextField()
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TypeToggle(selected: TransactionType, onSelect: (TransactionType) -> Unit) {
    val colors = LocalAppColors.current
    val shape  = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.glassWhite10)
            .border(1.dp, colors.glassBorder, shape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { type ->
            val isSelected = selected == type
            val label      = if (type == TransactionType.EXPENSE) "Expense" else "Income"
            val color      = if (type == TransactionType.EXPENSE) colors.accentRed else colors.accentGreen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
                    .border(
                        width  = if (isSelected) 1.dp else 0.dp,
                        color  = if (isSelected) color.copy(alpha = 0.6f) else Color.Transparent,
                        shape  = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color      = if (isSelected) color else colors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onSelect: () -> Unit) {
    val colors = LocalAppColors.current
    val shape  = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) colors.glassWhite15 else colors.glassWhite10)
            .border(1.dp, if (selected) colors.glassBorderStrong else colors.glassBorder, shape)
            .clickable(onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color      = if (selected) colors.textPrimary else colors.textSecondary,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}