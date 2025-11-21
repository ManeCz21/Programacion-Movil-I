package com.example.proyectofinalweb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinalweb.screens.*
import com.example.proyectofinalweb.util.AppContentType

// 2. Definición de todos los destinos de la app
object HomeDestination : NavigationDestination {
    override val route = "home"
}

object NoteEntryDestination : NavigationDestination {
    override val route = "note_entry"
}

object NoteDetailsDestination : NavigationDestination {
    override val route = "note_details"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
    val arguments = listOf(navArgument(NOTE_ID_ARG) { type = NavType.IntType })
}

object NoteEditDestination : NavigationDestination {
    override val route = "note_edit"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
    val arguments = listOf(navArgument(NOTE_ID_ARG) { type = NavType.IntType })
}

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

// 3. El NavHost que usa los destinos para configurar la navegación
@Composable
fun AppNavHost(
    navController: NavHostController,
    contentType: AppContentType
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNoteEntry = { navController.navigate(NoteEntryDestination.route) },
                navigateToTaskEntry = { navController.navigate(TaskEntryDestination.route) },
                navigateToNoteUpdate = { navController.navigate("${NoteDetailsDestination.route}/$it") },
                navigateToTaskUpdate = { navController.navigate("${TaskDetailsDestination.route}/$it") },
                contentType = contentType
            )
        }
        composable(route = NoteEntryDestination.route) {
            NoteEntryScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = NoteDetailsDestination.routeWithArgs,
            arguments = NoteDetailsDestination.arguments
        ) {
            NoteDetailsScreen(
                navigateToEditNote = { navController.navigate("${NoteEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = NoteEditDestination.routeWithArgs,
            arguments = NoteEditDestination.arguments
        ) {
            NoteEditScreen(navigateBack = { navController.popBackStack() })
        }
        composable(route = TaskEntryDestination.route) {
            TaskEntryScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = TaskDetailsDestination.routeWithArgs,
            arguments = TaskDetailsDestination.arguments
        ) {
            TaskDetailsScreen(
                navigateToEditTask = { navController.navigate("${TaskEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = TaskEditDestination.routeWithArgs,
            arguments = TaskEditDestination.arguments
        ) {
            TaskEditScreen(navigateBack = { navController.popBackStack() })
        }
    }
}
