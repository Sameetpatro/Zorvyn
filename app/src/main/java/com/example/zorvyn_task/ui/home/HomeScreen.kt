package com.example.zorvyn_task.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddTransaction: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    GlassBackground(modifier = Modifier.fillMaxSize()) {

        // Decorative blurred orbs for depth
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3060A5FA),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x308B5CF6),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
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
                                color = TextPrimary
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF60A5FA),
                                    Color(0xFF818CF8)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onAddTransaction) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Transaction",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    BalanceCard(state)
                    Spacer(modifier = Modifier.height(8.dp))
                    IncomeExpenseRow(state)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Transactions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextSecondary,
                            letterSpacing = (0.5).sp,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No transactions yet",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextTertiary
                                )
                            )
                        }
                    }
                } else {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(transaction)
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun BalanceCard(state: HomeUiState) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
        Text(
            "Total Balance",
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextSecondary,
                letterSpacing = (1).sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "₹ ${"%.2f".format(state.balance)}",
            style = MaterialTheme.typography.displayLarge.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Light
            )
        )
    }
}

@Composable
private fun IncomeExpenseRow(state: HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Income card
        GlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Income",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹ ${"%.2f".format(state.totalIncome)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AccentGreen,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
        }

        // Expense card
        GlassCard(modifier = Modifier.weight(1f), cornerRadius = 20.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Expense",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹ ${"%.2f".format(state.totalExpense)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AccentRed,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
private fun TransactionItem(transaction: TransactionEntity) {
    val dateStr = remember(transaction.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
    }
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) AccentGreen else AccentRed
    val prefix = if (isIncome) "+" else "-"

    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(GlassWhite15, GlassWhite10)
                )
            )
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Category dot indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isIncome) Color(0x2034D399) else Color(0x20FC8181)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(amountColor, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    transaction.category,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        transaction.note,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary)
                    )
                }
                Text(
                    dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
                )
            }
        }

        Text(
            "$prefix ₹ ${"%.2f".format(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = amountColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}