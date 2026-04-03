package com.example.zorvyn_task.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zorvyn_task.data.repository.GoalRepository
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.data.repository.UserRepository
import com.example.zorvyn_task.ui.addtransaction.AddTransactionScreen
import com.example.zorvyn_task.ui.addtransaction.AddTransactionViewModel
import com.example.zorvyn_task.ui.auth.AuthScreen
import com.example.zorvyn_task.ui.auth.AuthViewModel
import com.example.zorvyn_task.ui.components.rememberHaptic
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.home.HomeScreen
import com.example.zorvyn_task.ui.home.HomeViewModel
import com.example.zorvyn_task.ui.insights.InsightsScreen
import com.example.zorvyn_task.ui.insights.InsightsViewModel
import com.example.zorvyn_task.ui.profile.ProfileScreen
import com.example.zorvyn_task.ui.profile.ProfileViewModel

object Routes {
    const val AUTH            = "auth"
    const val MAIN            = "main"
    const val ADD_TRANSACTION = "add_transaction"
}

// Bubble-style enter/exit specs
private fun bubbleEnter() = fadeIn(tween(300)) +
        scaleIn(initialScale = 0.88f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ))

private fun bubbleExit() = fadeOut(tween(220)) +
        scaleOut(targetScale = 0.92f, animationSpec = tween(220))

@Composable
fun AppNavigation(
    transactionRepository: TransactionRepository,
    goalRepository: GoalRepository,
    userRepository: UserRepository,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val haptic = rememberHaptic()

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH,
        enterTransition = { bubbleEnter() },
        exitTransition = { bubbleExit() },
        popEnterTransition = { bubbleEnter() },
        popExitTransition = { bubbleExit() }
    ) {
        composable(Routes.AUTH) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(userRepository)
            )
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    haptic.click()
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(transactionRepository)
            )
            val goalViewModel: GoalViewModel = viewModel(
                factory = GoalViewModel.Factory(goalRepository, transactionRepository)
            )
            val insightsViewModel: InsightsViewModel = viewModel(
                factory = InsightsViewModel.Factory(transactionRepository)
            )
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(userRepository, transactionRepository)
            )

            MainScreen(
                homeViewModel    = homeViewModel,
                goalViewModel    = goalViewModel,
                insightsViewModel = insightsViewModel,
                profileViewModel = profileViewModel,
                isDarkMode       = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onAddTransaction = {
                    haptic.click()
                    navController.navigate(Routes.ADD_TRANSACTION)
                },
                haptic = haptic
            )
        }

        composable(Routes.ADD_TRANSACTION) {
            val addViewModel: AddTransactionViewModel = viewModel(
                factory = AddTransactionViewModel.Factory(transactionRepository)
            )
            AddTransactionScreen(
                viewModel = addViewModel,
                onNavigateBack = {
                    haptic.tick()
                    navController.popBackStack()
                }
            )
        }
    }
}