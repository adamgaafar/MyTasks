package com.agaafar.jetdo2.Util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.agaafar.jetdo2.ui.theme.todo_list.TodoListScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.TODO_LIST
    ) {
        composable(route = Routes.TODO_LIST) {
            TodoListScreen()
        }
    }
}