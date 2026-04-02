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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.*

private val EXPENSE_CATEGORIES = listOf(
    "🍔 Food", "🚗 Transport", "🛍️ Shopping", "🏥 Health",
    "🎬 Entertainment", "🏠 Housing", "📱 Utilities", "📚 Education", "✈️ Travel", "💡 Other"
)

private val INCOME_CATEGORIES = listOf(
    "💼 Salary", "💰 Freelance", "📈 Investment", "🎁 Gift", "💳 Refund", "💡 Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Add Transaction",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Light
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
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

                // Amount input
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "AMOUNT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "₹",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = if (state.type == TransactionType.INCOME) AccentGreen else AccentRed
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AmountField(value = state.amount, onValueChange = viewModel::setAmount)
                    }
                }

                // Category
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "CATEGORY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val categories = if (state.type == TransactionType.EXPENSE) EXPENSE_CATEGORIES else INCOME_CATEGORIES
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            CategoryChip(
                                label = cat,
                                selected = state.category == cat,
                                onSelect = { viewModel.setCategory(cat) }
                            )
                        }
                    }
                }

                // Note
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                    Text(
                        "NOTE (optional)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary, letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = viewModel::setNote,
                        placeholder = { Text("Add a note…", color = TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Color(0x40FFFFFF),
                            cursorColor = AccentBlue,
                            focusedContainerColor = Color(0x10FFFFFF),
                            unfocusedContainerColor = Color(0x08FFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Error
                AnimatedVisibility(visible = state.error != null) {
                    Text(
                        state.error ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = AccentRed),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Save button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                if (state.type == TransactionType.INCOME)
                                    listOf(Color(0xFF34D399), Color(0xFF6EE7B7))
                                else
                                    listOf(Color(0xFFFC8181), Color(0xFFFCA5A5))
                            )
                        )
                        .clickable(enabled = !state.isSaving) { viewModel.save() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save Transaction",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
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
    BasicTextField(
        value = value,
        onValueChange = { new -> onValueChange(new.filter { c -> c.isDigit() || c == '.' }) },
        textStyle = MaterialTheme.typography.headlineLarge.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Light
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    "0.00",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = TextTertiary,
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
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x20FFFFFF))
            .border(1.dp, Color(0x30FFFFFF), shape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { type ->
            val isSelected = selected == type
            val label = if (type == TransactionType.EXPENSE) "Expense" else "Income"
            val color = if (type == TransactionType.EXPENSE) AccentRed else AccentGreen

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) color.copy(alpha = 0.6f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isSelected) color else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onSelect: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) Color(0x40FFFFFF) else Color(0x15FFFFFF))
            .border(1.dp, if (selected) Color(0x80FFFFFF) else Color(0x25FFFFFF), shape)
            .clickable(onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (selected) TextPrimary else TextSecondary,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}