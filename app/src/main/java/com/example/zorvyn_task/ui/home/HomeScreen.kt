package com.example.zorvyn_task.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.goal.GoalSection
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private val EXPENSE_CATEGORIES = listOf(
    "Food", "Transport", "Shopping", "Health",
    "Entertainment", "Housing", "Utilities", "Education", "Travel", "Other"
)
private val INCOME_CATEGORIES = listOf(
    "Salary", "Freelance", "Investment", "Gift", "Refund", "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    goalViewModel: GoalViewModel,
    onAddTransaction: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val goalState by goalViewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    LaunchedEffect(Unit) {
        goalViewModel.evaluateStreakForToday()
    }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(colors.accentGreen.copy(alpha = 0.18f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 80.dp)
                .background(
                    Brush.radialGradient(listOf(colors.accentGreen.copy(alpha = 0.12f), Color.Transparent)),
                    CircleShape
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Finance",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Light,
                                letterSpacing = (-0.5).sp,
                                color = colors.textPrimary
                            )
                        )
                    },
                    actions = {
                        FloatingActionButton(
                            onClick = onAddTransaction,
                            modifier = Modifier.padding(end = 12.dp).size(40.dp),
                            containerColor = Color.Transparent,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.7f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "+",
                                    color = if (colors.isDark) Color.Black else Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            if (state.isLoading) {
                LoadingSkeleton(padding)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item { BalanceCard(state) }
                    item { IncomeExpenseRow(state) }
                    item {
                        GoalSection(
                            state = goalState,
                            onSetLimit = { goalViewModel.setDailyLimit(it) }
                        )
                    }
                    item { SearchAndFilterRow(state, viewModel) }
                    item {
                        Text(
                            if (state.searchQuery.isBlank() && state.activeFilter == TransactionFilter.ALL)
                                "Recent Transactions"
                            else
                                "Results (${state.filteredTransactions.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = colors.textSecondary, letterSpacing = 1.sp
                            )
                        )
                    }
                    if (state.filteredTransactions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        if (state.transactions.isEmpty()) "💸" else "🔍",
                                        fontSize = 36.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        if (state.transactions.isEmpty()) "No transactions yet"
                                        else "No results found",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = colors.textTertiary
                                        )
                                    )
                                    Text(
                                        if (state.transactions.isEmpty()) "Tap + to add your first one"
                                        else "Try a different search or filter",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textTertiary
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.filteredTransactions, key = { it.id }) { transaction ->
                            SwipeableTransactionItem(
                                transaction = transaction,
                                onEdit = { viewModel.startEdit(transaction) },
                                onDelete = { viewModel.promptDelete(transaction) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }

    if (state.editingTransaction != null) {
        EditTransactionDialog(
            transaction = state.editingTransaction!!,
            onConfirm = { amount, type, category, note, date ->
                viewModel.saveEdit(state.editingTransaction!!.id, amount, type, category, note, date)
            },
            onDismiss = { viewModel.cancelEdit() }
        )
    }

    if (state.showDeleteConfirm != null) {
        DeleteConfirmDialog(
            transaction = state.showDeleteConfirm!!,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}

@Composable
private fun LoadingSkeleton(padding: PaddingValues) {
    val colors = LocalAppColors.current
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (it == 0) 100.dp else 64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.glassWhite15.copy(alpha = shimmerAlpha))
            )
        }
    }
}

@Composable
private fun SearchAndFilterRow(state: HomeUiState, viewModel: HomeViewModel) {
    val colors = LocalAppColors.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val shape = RoundedCornerShape(14.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.glassWhite10)
                .border(1.dp, colors.glassBorder, shape)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(18.dp))
            BasicTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accentGreen),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (state.searchQuery.isEmpty()) {
                        Text("Search by category or note…", style = MaterialTheme.typography.bodyMedium.copy(color = colors.textTertiary))
                    }
                    inner()
                }
            )
            if (state.searchQuery.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = colors.textTertiary,
                    modifier = Modifier.size(16.dp).clickable { viewModel.setSearchQuery("") }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TransactionFilter.values()) { filter ->
                val selected = state.activeFilter == filter
                val label = when (filter) {
                    TransactionFilter.ALL -> "All"
                    TransactionFilter.INCOME -> "Income"
                    TransactionFilter.EXPENSE -> "Expense"
                }
                val chipShape = RoundedCornerShape(50)
                Box(
                    modifier = Modifier
                        .clip(chipShape)
                        .background(
                            if (selected) colors.accentGreen.copy(alpha = 0.20f)
                            else colors.glassWhite10
                        )
                        .border(1.dp, if (selected) colors.accentGreen.copy(alpha = 0.6f) else colors.glassBorder, chipShape)
                        .clickable { viewModel.setFilter(filter) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (selected) colors.accentGreen else colors.textSecondary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeableTransactionItem(
    transaction: TransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalAppColors.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "swipeOffset"
    )
    val threshold = 80f
    val maxReveal = 140f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(IntrinsicSize.Min)
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentGreen.copy(alpha = 0.20f))
                    .clickable { offsetX = 0f; onEdit() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colors.accentGreen, modifier = Modifier.size(18.dp))
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.accentRed.copy(alpha = 0.20f))
                    .clickable { offsetX = 0f; onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = colors.accentRed, modifier = Modifier.size(18.dp))
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(animOffset.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newOffset = (offsetX + delta).coerceIn(-maxReveal, 0f)
                        offsetX = newOffset
                    },
                    onDragStopped = {
                        offsetX = if (offsetX < -threshold) -maxReveal else 0f
                    }
                )
        ) {
            TransactionItem(
                transaction = transaction,
                onClick = { if (animOffset != 0f) offsetX = 0f }
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: TransactionEntity, onClick: () -> Unit = {}) {
    val colors = LocalAppColors.current
    val dateStr = remember(transaction.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
    }
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) colors.accentGreen else colors.accentRed
    val prefix = if (isIncome) "+" else "-"
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(colors.glassWhite15, colors.glassWhite10)))
            .border(1.dp, colors.glassBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(amountColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(10.dp).background(amountColor, CircleShape))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    transaction.category,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colors.textPrimary, fontWeight = FontWeight.Medium
                    )
                )
                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        transaction.note,
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                    )
                }
                Text(dateStr, style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary))
            }
        }
        Text(
            "$prefix ₹ ${"%.2f".format(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = amountColor, fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun BalanceCard(state: HomeUiState) {
    val colors = LocalAppColors.current
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
        Text(
            "Total Balance",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "₹ ${"%.2f".format(state.balance)}",
            style = MaterialTheme.typography.displayLarge.copy(
                color = colors.textPrimary, fontWeight = FontWeight.Light
            )
        )
    }
}

@Composable
private fun IncomeExpenseRow(state: HomeUiState) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(colors.accentGreen, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Income", style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹ ${"%.2f".format(state.totalIncome)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colors.accentGreen, fontWeight = FontWeight.Medium, fontSize = 18.sp
                )
            )
        }
        GlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(colors.accentRed, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Expense", style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹ ${"%.2f".format(state.totalExpense)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colors.accentRed, fontWeight = FontWeight.Medium, fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    transaction: TransactionEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    val containerColor = if (colors.isDark) Color(0xFF0D1A0D) else Color(0xFFE8F5F0)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerColor,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, tint = colors.accentRed, modifier = Modifier.size(28.dp))
        },
        title = {
            Text("Delete Transaction?", style = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary))
        },
        text = {
            Text(
                "This will permanently delete your ${transaction.category} transaction of ₹${"%.2f".format(transaction.amount)}.",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = colors.accentRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colors.textSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTransactionDialog(
    transaction: TransactionEntity,
    onConfirm: (Double, TransactionType, String, String?, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    val containerColor = if (colors.isDark) Color(0xFF0D1A0D) else Color(0xFFF0FAF6)
    val fieldBg = if (colors.isDark) Color(0xFF1A2A1A) else Color(0xFFFFFFFF)

    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var type by remember { mutableStateOf(transaction.type) }
    var category by remember { mutableStateOf(transaction.category) }
    var note by remember { mutableStateOf(transaction.note ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transaction.date,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= System.currentTimeMillis()
        }
    )
    val displayFmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val selectedDateLabel = datePickerState.selectedDateMillis?.let { displayFmt.format(Date(it)) } ?: "Tap to select"

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK", color = colors.accentGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = colors.textSecondary) }
            },
            colors = DatePickerDefaults.colors(
                containerColor = containerColor,
                titleContentColor = colors.textPrimary,
                headlineContentColor = colors.textPrimary,
                weekdayContentColor = colors.textSecondary,
                navigationContentColor = colors.textPrimary,
                yearContentColor = colors.textPrimary,
                currentYearContentColor = colors.accentGreen,
                selectedYearContentColor = if (colors.isDark) Color.Black else Color.White,
                selectedYearContainerColor = colors.accentGreen,
                dayContentColor = colors.textPrimary,
                selectedDayContentColor = if (colors.isDark) Color.Black else Color.White,
                selectedDayContainerColor = colors.accentGreen,
                todayContentColor = colors.accentGreen,
                todayDateBorderColor = colors.accentGreen,
            )
        ) { DatePicker(state = datePickerState) }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = colors.accentGreen,
        unfocusedBorderColor = colors.glassBorder,
        focusedLabelColor = colors.accentGreen,
        unfocusedLabelColor = colors.textSecondary,
        cursorColor = colors.accentGreen,
        focusedContainerColor = fieldBg,
        unfocusedContainerColor = fieldBg,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerColor,
        title = {
            Text("Edit Transaction", style = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.glassWhite10)
                        .padding(4.dp)
                ) {
                    listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { t ->
                        val selected = type == t
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
                                    color = if (selected) accentColor else colors.textSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Category", style = MaterialTheme.typography.labelSmall.copy(color = colors.textSecondary))
                val categories = if (type == TransactionType.EXPENSE) EXPENSE_CATEGORIES else INCOME_CATEGORIES
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(fieldBg)
                        .border(1.dp, colors.glassBorder, RoundedCornerShape(10.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Date", style = MaterialTheme.typography.labelSmall.copy(color = colors.textSecondary, fontSize = 11.sp))
                            Spacer(Modifier.height(2.dp))
                            Text(selectedDateLabel, style = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium))
                        }
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = colors.accentGreen, modifier = Modifier.size(20.dp))
                    }
                }

                if (error != null) {
                    Text(error!!, style = MaterialTheme.typography.bodySmall.copy(color = colors.accentRed))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                val date = datePickerState.selectedDateMillis
                when {
                    amt == null || amt <= 0 -> error = "Enter a valid amount"
                    category.isBlank() -> error = "Select a category"
                    date == null -> error = "Select a date"
                    else -> onConfirm(amt, type, category, note.ifBlank { null }, date)
                }
            }) {
                Text("Save", color = colors.accentGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colors.textSecondary) }
        }
    )
}