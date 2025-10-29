package com.example.proyectofinalweb.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinalweb.screens.* // Import all screens
import com.example.proyectofinalweb.ui.home.HomeDestination
import com.example.proyectofinalweb.ui.note.NoteDetailsDestination
import com.example.proyectofinalweb.ui.note.NoteEditDestination
import com.example.proyectofinalweb.ui.note.NoteEntryDestination
import com.example.proyectofinalweb.ui.task.TaskDetailsDestination
import com.example.proyectofinalweb.ui.task.TaskEditDestination
import com.example.proyectofinalweb.ui.task.TaskEntryDestination
import com.example.proyectofinalweb.util.AppContentType

@Composable
fun AppNavHost(
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val contentType = when (widthSizeClass) {
        WindowWidthSizeClass.Compact,
        WindowWidthSizeClass.Medium -> AppContentType.LIST_ONLY
        WindowWidthSizeClass.Expanded -> AppContentType.LIST_AND_DETAIL
        else -> AppContentType.LIST_ONLY
    }

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        // Notes
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNoteEntry = { navController.navigate(NoteEntryDestination.route) },
                navigateToTaskEntry = { navController.navigate(TaskEntryDestination.route) },
                navigateToNoteUpdate = { navController.navigate("${NoteDetailsDestination.route}/$it") },
                navigateToTaskUpdate = { navController.navigate("${TaskDetailsDestination.route}/$it") },
                navigateToEditNote = { navController.navigate("${NoteEditDestination.route}/$it") },
                navigateToEditTask = { navController.navigate("${TaskEditDestination.route}/$it") },
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

        // Tasks
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
