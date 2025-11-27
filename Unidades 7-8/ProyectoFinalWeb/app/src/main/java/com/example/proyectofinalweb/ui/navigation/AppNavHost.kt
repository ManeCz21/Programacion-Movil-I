package com.example.proyectofinalweb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinalweb.screens.*
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
    contentType: AppContentType,
    modifier: Modifier = Modifier,
    taskId: Int? = null,
    onTaskOpened: () -> Unit
) {
    LaunchedEffect(taskId) {
        if (taskId != null) {
            navController.navigate("${TaskDetailsDestination.route}/$taskId")
            onTaskOpened()
        }
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
