package com.jadwale.feature.assignments

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ASSIGNMENT_LIST_ROUTE = "assignment_list_route"
const val ASSIGNMENT_ADD_EDIT_BASE_ROUTE = "assignment_add_edit_route"
const val ASSIGNMENT_ID_ARG = "assignmentId"
const val ASSIGNMENT_ADD_EDIT_ROUTE = "$ASSIGNMENT_ADD_EDIT_BASE_ROUTE?assignmentId={$ASSIGNMENT_ID_ARG}"

fun NavController.navigateToAssignmentList(navOptions: NavOptions? = null) {
    this.navigate(ASSIGNMENT_LIST_ROUTE, navOptions)
}

fun NavController.navigateToAssignmentAddEdit(assignmentId: String? = null) {
    val route = if (assignmentId != null) "$ASSIGNMENT_ADD_EDIT_BASE_ROUTE?assignmentId=$assignmentId" else ASSIGNMENT_ADD_EDIT_BASE_ROUTE
    this.navigate(route)
}

fun NavGraphBuilder.assignmentScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (assignmentId: String?) -> Unit
) {
    composable(route = ASSIGNMENT_LIST_ROUTE) {
        val viewModel: AssignmentViewModel = hiltViewModel()
        AssignmentListScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToAddEdit = onNavigateToAddEdit
        )
    }

    composable(
        route = ASSIGNMENT_ADD_EDIT_ROUTE,
        arguments = listOf(
            navArgument(ASSIGNMENT_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val assignmentId = backStackEntry.arguments?.getString(ASSIGNMENT_ID_ARG)
        val viewModel: AssignmentViewModel = hiltViewModel()
        AddEditAssignmentScreen(
            viewModel = viewModel,
            assignmentId = assignmentId,
            onNavigateBack = onNavigateBack
        )
    }
}
