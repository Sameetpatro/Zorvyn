package com.example.zorvyn_task.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.AppColors
import com.example.zorvyn_task.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val state  by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessage()
        }
    }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colors.textPrimary, fontWeight = FontWeight.Light
                )
            )

            // ── Avatar card ───────────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.6f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            state.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            fontSize   = 26.sp,
                            color      = if (colors.isDark) Color.Black else Color.White,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            state.userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = colors.textPrimary, fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            state.userId.take(20) + "…",
                            style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = colors.glassBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatChip("Transactions", state.transactionCount.toString(), colors)
                    StatChip("Income",  "₹${"%.0f".format(state.totalIncome)}",  colors)
                    StatChip("Expense", "₹${"%.0f".format(state.totalExpense)}", colors)
                }
            }

            // ── Dark mode toggle ──────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDarkMode) Icons.Default.NightlightRound else Icons.Default.LightMode,
                            contentDescription = null,
                            tint     = colors.accentGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                if (isDarkMode) "Dark Mode" else "Light Mode",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = colors.textPrimary, fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                if (isDarkMode) "Switch to mint light theme" else "Switch to dark theme",
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                            )
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = if (colors.isDark) Color.Black else Color.White,
                            checkedTrackColor   = colors.accentGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = colors.accentGreen.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            // ── Add past transaction ──────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showAddHistory() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.History, contentDescription = null,
                            tint = colors.accentGreen, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Add Past Transaction",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colors.textPrimary, fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            "Log historical income or expenses",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                }
            }

            // ── Reset data ────────────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showResetConfirm() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null,
                            tint = colors.accentRed, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Reset All Data",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colors.accentRed, fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            "Permanently delete all transactions",
                            style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.accentRed)
                }
            }

            // ── Success toast ─────────────────────────────────────────────────
            AnimatedVisibility(visible = state.successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accentGreen.copy(alpha = 0.2f))
                        .border(1.dp, colors.accentGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        state.successMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.accentGreen)
                    )
                }
            }
        }
    }

    if (state.resetConfirmVisible) {
        ResetConfirmDialog(
            onConfirm = { viewModel.resetAllData() },
            onDismiss = { viewModel.hideResetConfirm() }
        )
    }

    if (state.addHistoryVisible) {
        AddPastTransactionDialog(
            onConfirm = { amount, type, category, note, dateMs ->
                viewModel.addPastTransaction(amount, type, category, note, dateMs)
                viewModel.hideAddHistory()
            },
            onDismiss = { viewModel.hideAddHistory() }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(label: String, value: String, colors: AppColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium.copy(
            color = colors.textPrimary, fontWeight = FontWeight.SemiBold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary))
    }
}

@Composable
private fun ResetConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors         = LocalAppColors.current
    val containerColor = if (colors.isDark) Color(0xFF0D1A0D) else Color(0xFFE8F5F0)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = containerColor,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null,
                tint = colors.accentRed, modifier = Modifier.size(32.dp))
        },
        title = {
            Text("Reset All Data?", style = MaterialTheme.typography.titleMedium.copy(
                color = colors.textPrimary))
        },
        text = {
            Text(
                "This will permanently delete ALL your transactions. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete Everything", color = colors.accentRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colors.textSecondary) }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────

private val EXPENSE_CATEGORIES = listOf(
    "🍔 Food","🚗 Transport","🛍️ Shopping","🏥 Health",
    "🎬 Entertainment","🏠 Housing","📱 Utilities","📚 Education","✈️ Travel","💡 Other"
)
private val INCOME_CATEGORIES = listOf(
    "💼 Salary","💰 Freelance","📈 Investment","🎁 Gift","💳 Refund","💡 Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPastTransactionDialog(
    onConfirm: (Double, TransactionType, String, String?, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val colors         = LocalAppColors.current
    val containerColor = if (colors.isDark) Color(0xFF0D1A0D) else Color(0xFFF0FAF6)
    val fieldBg        = if (colors.isDark) Color(0xFF1A2A1A) else Color(0xFFFFFFFF)

    var amount   by remember { mutableStateOf("") }
    var type     by remember { mutableStateOf(TransactionType.EXPENSE) }
    var category by remember { mutableStateOf("") }
    var note     by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf<String?>(null) }

    // ── Date picker ───────────────────────────────────────────────────────────
    var showDatePicker  by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis <= System.currentTimeMillis()
        }
    )
    val displayFmt         = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val selectedDateMillis = datePickerState.selectedDateMillis
    val selectedDateLabel  = selectedDateMillis
        ?.let { displayFmt.format(Date(it)) }
        ?: "Tap to select"

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false; error = null }) {
                    Text("OK", color = colors.accentGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor             = containerColor,
                titleContentColor          = colors.textPrimary,
                headlineContentColor       = colors.textPrimary,
                weekdayContentColor        = colors.textSecondary,
                subheadContentColor        = colors.textSecondary,
                navigationContentColor     = colors.textPrimary,
                yearContentColor           = colors.textPrimary,
                currentYearContentColor    = colors.accentGreen,
                selectedYearContentColor   = if (colors.isDark) Color.Black else Color.White,
                selectedYearContainerColor = colors.accentGreen,
                dayContentColor            = colors.textPrimary,
                selectedDayContentColor    = if (colors.isDark) Color.Black else Color.White,
                selectedDayContainerColor  = colors.accentGreen,
                todayContentColor          = colors.accentGreen,
                todayDateBorderColor       = colors.accentGreen,
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Shared field colours ──────────────────────────────────────────────────
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor          = colors.textPrimary,
        unfocusedTextColor        = colors.textPrimary,
        focusedBorderColor        = colors.accentGreen,
        unfocusedBorderColor      = colors.glassBorder,
        focusedLabelColor         = colors.accentGreen,
        unfocusedLabelColor       = colors.textSecondary,
        cursorColor               = colors.accentGreen,
        focusedContainerColor     = fieldBg,
        unfocusedContainerColor   = fieldBg,
        focusedPlaceholderColor   = colors.textTertiary,
        unfocusedPlaceholderColor = colors.textTertiary,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = containerColor,
        title = {
            Text(
                "Add Past Transaction",
                style = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Type toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.glassWhite10)
                        .padding(4.dp)
                ) {
                    listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { t ->
                        val selected    = type == t
                        val accentColor = if (t == TransactionType.EXPENSE) colors.accentRed else colors.accentGreen
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) accentColor.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { type = t; category = "" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (t == TransactionType.EXPENSE) "Expense" else "Income",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color      = if (selected) accentColor else colors.textSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value         = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label         = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    colors        = fieldColors,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp)
                )

                // Category chips
                Text("Category",
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.textSecondary))
                val categories = if (type == TransactionType.EXPENSE) EXPENSE_CATEGORIES else INCOME_CATEGORIES
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val sel = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (sel) colors.accentGreen.copy(alpha = 0.20f) else colors.glassWhite10)
                                .border(1.dp, if (sel) colors.accentGreen else colors.glassBorder, RoundedCornerShape(50))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(cat, style = MaterialTheme.typography.bodySmall.copy(
                                color = if (sel) colors.accentGreen else colors.textSecondary))
                        }
                    }
                }

                // Note
                OutlinedTextField(
                    value         = note,
                    onValueChange = { note = it },
                    label         = { Text("Note (optional)") },
                    singleLine    = true,
                    colors        = fieldColors,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(10.dp)
                )

                // Date selector button (replaces manual text input)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(fieldBg)
                        .border(
                            width  = 1.dp,
                            color  = if (error?.contains("date", ignoreCase = true) == true)
                                colors.accentRed else colors.glassBorder,
                            shape  = RoundedCornerShape(10.dp)
                        )
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "Date",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color    = colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                selectedDateLabel,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color      = if (selectedDateMillis != null) colors.textPrimary
                                    else colors.textTertiary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Pick date",
                            tint     = colors.accentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Error
                if (error != null) {
                    Text(error!!, style = MaterialTheme.typography.bodySmall.copy(color = colors.accentRed))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt  = amount.toDoubleOrNull()
                val date = selectedDateMillis
                when {
                    amt == null || amt <= 0 -> error = "Enter a valid amount"
                    category.isBlank()      -> error = "Select a category"
                    date == null            -> error = "Select a date"
                    else -> onConfirm(amt, type, category, note.ifBlank { null }, date)
                }
            }) {
                Text("Add", color = colors.accentGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}