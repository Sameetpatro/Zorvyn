package com.example.zorvyn_task.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.ui.addtransaction.AddTransactionScreen
import com.example.zorvyn_task.ui.home.HomeScreen
import com.example.zorvyn_task.ui.home.HomeViewModel

object Routes {
    const val HOME = "home"
    const val ADD_TRANSACTION = "add_transaction"
}

@Composable
fun AppNavigation(repository: TransactionRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onAddTransaction = { navController.navigate(Routes.ADD_TRANSACTION) }
            )
        }

        composable(Routes.ADD_TRANSACTION) {
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}