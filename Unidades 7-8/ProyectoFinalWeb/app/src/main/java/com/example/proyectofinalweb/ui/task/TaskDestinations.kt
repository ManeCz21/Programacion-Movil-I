package com.example.proyectofinalweb.ui.task

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.proyectofinalweb.ui.navigation.NavigationDestination

object TaskEntryDestination : NavigationDestination {
    override val route = "task_entry"
}

object TaskDetailsDestination : NavigationDestination {
    override val route = "task_details"
    const val TASK_ID_ARG = "taskId"
    val routeWithArgs = "$route/{$TASK_ID_ARG}"
    val arguments = listOf(navArgument(TASK_ID_ARG) { type = NavType.IntType })
}

object TaskEditDestination : NavigationDestination {
    override val route = "task_edit"
    const val TASK_ID_ARG = "taskId"
    val routeWithArgs = "$route/{$TASK_ID_ARG}"
    val arguments = listOf(navArgument(TASK_ID_ARG) { type = NavType.IntType })
}
