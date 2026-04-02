package com.example.zorvyn_task.ui.navigation

import androidx.compose.runtime.Composable
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
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.home.HomeScreen
import com.example.zorvyn_task.ui.home.HomeViewModel
import com.example.zorvyn_task.ui.insights.InsightsScreen
import com.example.zorvyn_task.ui.insights.InsightsViewModel

object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val ADD_TRANSACTION = "add_transaction"
    const val INSIGHTS = "insights"
}

@Composable
fun AppNavigation(
    transactionRepository: TransactionRepository,
    goalRepository: GoalRepository,
    userRepository: UserRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.AUTH) {

        composable(Routes.AUTH) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(userRepository)
            )
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(transactionRepository)
            )
            val goalViewModel: GoalViewModel = viewModel(
                factory = GoalViewModel.Factory(goalRepository, transactionRepository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                goalViewModel = goalViewModel,
                onAddTransaction = { navController.navigate(Routes.ADD_TRANSACTION) },
                onOpenInsights = { navController.navigate(Routes.INSIGHTS) }
            )
        }

        composable(Routes.ADD_TRANSACTION) {
            val addViewModel: AddTransactionViewModel = viewModel(
                factory = AddTransactionViewModel.Factory(transactionRepository)
            )
            AddTransactionScreen(
                viewModel = addViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INSIGHTS) {
            val insightsViewModel: InsightsViewModel = viewModel(
                factory = InsightsViewModel.Factory(transactionRepository)
            )
            InsightsScreen(
                viewModel = insightsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}