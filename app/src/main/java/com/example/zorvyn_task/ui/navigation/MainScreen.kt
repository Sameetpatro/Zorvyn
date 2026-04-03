package com.example.zorvyn_task.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zorvyn_task.ui.components.HapticHelper
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.home.HomeScreen
import com.example.zorvyn_task.ui.home.HomeViewModel
import com.example.zorvyn_task.ui.insights.InsightsScreen
import com.example.zorvyn_task.ui.insights.InsightsViewModel
import com.example.zorvyn_task.ui.profile.ProfileScreen
import com.example.zorvyn_task.ui.profile.ProfileViewModel

private val NAV_BAR_HEIGHT = 88.dp

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    goalViewModel: GoalViewModel,
    insightsViewModel: InsightsViewModel,
    profileViewModel: ProfileViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onAddTransaction: () -> Unit,
    haptic: HapticHelper
) {
    var currentTab by remember { mutableStateOf(BottomTab.HOME) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Tab content ────────────────────────────────────────────────────────
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = {
                (fadeIn(tween(280)) + scaleIn(
                    initialScale = 0.93f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )).togetherWith(
                    fadeOut(tween(200)) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(200)
                    )
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = NAV_BAR_HEIGHT),
            label = "tabContent"
        ) { tab ->
            when (tab) {
                BottomTab.HOME -> HomeScreen(
                    viewModel        = homeViewModel,
                    goalViewModel    = goalViewModel,
                    onAddTransaction = onAddTransaction
                )
                BottomTab.INSIGHTS -> InsightsScreen(
                    viewModel = insightsViewModel
                )
                BottomTab.PROFILE -> ProfileScreen(
                    viewModel        = profileViewModel,
                    isDarkMode       = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
        }

        // ── Bottom nav ─────────────────────────────────────────────────────────
        BottomNavBar(
            currentTab    = currentTab,
            onTabSelected = { currentTab = it },
            onAddClick    = onAddTransaction,
            haptic        = haptic,
            modifier      = Modifier.align(Alignment.BottomCenter)
        )
    }
}