package com.jadwale.feature.routine_activities

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ROUTINE_LIST_ROUTE = "routine_list_route"
const val ROUTINE_ADD_EDIT_BASE_ROUTE = "routine_add_edit_route"
const val ROUTINE_ID_ARG = "routineId"
const val ROUTINE_ADD_EDIT_ROUTE = "$ROUTINE_ADD_EDIT_BASE_ROUTE?routineId={$ROUTINE_ID_ARG}"

fun NavController.navigateToRoutineList(navOptions: NavOptions? = null) {
    this.navigate(ROUTINE_LIST_ROUTE, navOptions)
}

fun NavController.navigateToRoutineAddEdit(routineId: String? = null) {
    val route = if (routineId != null) "$ROUTINE_ADD_EDIT_BASE_ROUTE?routineId=$routineId" else ROUTINE_ADD_EDIT_BASE_ROUTE
    this.navigate(route)
}

fun NavGraphBuilder.routineScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (routineId: String?) -> Unit
) {
    composable(route = ROUTINE_LIST_ROUTE) {
        val viewModel: RoutineViewModel = hiltViewModel()
        RoutineListScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToAddEdit = onNavigateToAddEdit
        )
    }

    composable(
        route = ROUTINE_ADD_EDIT_ROUTE,
        arguments = listOf(
            navArgument(ROUTINE_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val routineId = backStackEntry.arguments?.getString(ROUTINE_ID_ARG)
        val viewModel: RoutineViewModel = hiltViewModel()
        AddEditRoutineScreen(
            viewModel = viewModel,
            routineId = routineId,
            onNavigateBack = onNavigateBack
        )
    }
}
