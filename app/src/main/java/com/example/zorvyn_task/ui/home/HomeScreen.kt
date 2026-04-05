package com.example.zorvyn_task.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.zorvyn_task.ui.goal.GoalSection
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    goalViewModel: GoalViewModel,
    onAddTransaction: () -> Unit
) {
    val state     by viewModel.uiState.collectAsState()
    val goalState by goalViewModel.uiState.collectAsState()
    val colors    = LocalAppColors.current

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
                                fontWeight   = FontWeight.Light,
                                letterSpacing = (-0.5).sp,
                                color        = colors.textPrimary
                            )
                        )
                    },
                    actions = {
                        FloatingActionButton(
                            onClick        = onAddTransaction,
                            modifier       = Modifier.padding(end = 12.dp).size(40.dp),
                            containerColor = Color.Transparent,
                            elevation      = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                colors.accentGreen,
                                                colors.accentGreen.copy(alpha = 0.7f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "+",
                                    color      = if (colors.isDark) Color.Black else Color.White,
                                    fontSize   = 22.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
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
                        state    = goalState,
                        onSetLimit = { goalViewModel.setDailyLimit(it) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LocalAppColors.current.textSecondary, letterSpacing = 1.sp
                        )
                    )
                }
                if (state.transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💸", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No transactions yet",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = LocalAppColors.current.textTertiary
                                    )
                                )
                                Text(
                                    "Tap + to add your first one",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = LocalAppColors.current.textTertiary
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(transaction)
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
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
                Text("Income",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary))
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
                Text("Expense",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary))
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
private fun TransactionItem(transaction: TransactionEntity) {
    val colors  = LocalAppColors.current
    val dateStr = remember(transaction.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
    }
    val isIncome    = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) colors.accentGreen else colors.accentRed
    val prefix      = if (isIncome) "+" else "-"
    val shape       = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(colors.glassWhite15, colors.glassWhite10)))
            .border(1.dp, colors.glassBorder, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
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
                    Text(transaction.note,
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary))
                }
                Text(dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary))
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