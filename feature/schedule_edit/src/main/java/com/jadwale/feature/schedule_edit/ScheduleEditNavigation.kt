package com.jadwale.feature.schedule_edit

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val SCHEDULE_EDIT_BASE_ROUTE = "schedule_edit_route"
const val CLASS_ID_ARG = "classId"
const val SCHEDULE_EDIT_ROUTE = "$SCHEDULE_EDIT_BASE_ROUTE/{$CLASS_ID_ARG}"

fun NavController.navigateToScheduleEdit(classId: String = "c1", navOptions: NavOptions? = null) {
    this.navigate("$SCHEDULE_EDIT_BASE_ROUTE/$classId", navOptions)
}

fun NavGraphBuilder.scheduleEditScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = SCHEDULE_EDIT_ROUTE,
        arguments = listOf(
            navArgument(CLASS_ID_ARG) {
                type = NavType.StringType
                defaultValue = "c1"
            }
        )
    ) { backStackEntry ->
        val classId = backStackEntry.arguments?.getString(CLASS_ID_ARG) ?: "c1"
        val viewModel: ScheduleEditViewModel = hiltViewModel()
        ScheduleEditScreen(
            viewModel = viewModel,
            initialClassId = classId,
            onNavigateBack = onNavigateBack
        )
    }
}
