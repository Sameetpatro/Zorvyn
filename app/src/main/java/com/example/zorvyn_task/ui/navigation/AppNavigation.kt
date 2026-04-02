package com.example.zorvyn_task.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zorvyn_task.data.local.GoalPreferences
import com.example.zorvyn_task.data.repository.GoalRepository
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.ui.addtransaction.AddTransactionScreen
import com.example.zorvyn_task.ui.goal.GoalViewModel
import com.example.zorvyn_task.ui.home.HomeScreen
import com.example.zorvyn_task.ui.home.HomeViewModel
import com.example.zorvyn_task.ui.insights.InsightsScreen
import com.example.zorvyn_task.ui.insights.InsightsViewModel

object Routes {
    const val HOME = "home"
    const val ADD_TRANSACTION = "add_transaction"
    const val INSIGHTS = "insights"
}

@Composable
fun AppNavigation(
    transactionRepository: TransactionRepository,
    goalRepository: GoalRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

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
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
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