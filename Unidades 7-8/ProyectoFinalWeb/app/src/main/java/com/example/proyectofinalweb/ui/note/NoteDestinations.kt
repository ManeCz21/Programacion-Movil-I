package com.example.proyectofinalweb.ui.note

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.proyectofinalweb.ui.navigation.NavigationDestination

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
