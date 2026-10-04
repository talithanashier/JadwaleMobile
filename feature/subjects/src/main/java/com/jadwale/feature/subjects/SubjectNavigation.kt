package com.jadwale.feature.subjects

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val SUBJECT_LIST_ROUTE = "subject_list_route"
const val SUBJECT_ADD_EDIT_BASE_ROUTE = "subject_add_edit_route"
const val SUBJECT_ID_ARG = "subjectId"
const val SUBJECT_ADD_EDIT_ROUTE = "$SUBJECT_ADD_EDIT_BASE_ROUTE?subjectId={$SUBJECT_ID_ARG}"

fun NavController.navigateToSubjectList(navOptions: NavOptions? = null) {
    this.navigate(SUBJECT_LIST_ROUTE, navOptions)
}

fun NavController.navigateToSubjectAddEdit(subjectId: String? = null) {
    val route = if (subjectId != null) "$SUBJECT_ADD_EDIT_BASE_ROUTE?subjectId=$subjectId" else SUBJECT_ADD_EDIT_BASE_ROUTE
    this.navigate(route)
}

fun NavGraphBuilder.subjectScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (subjectId: String?) -> Unit
) {
    composable(route = SUBJECT_LIST_ROUTE) {
        val viewModel: SubjectViewModel = hiltViewModel()
        SubjectListScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToAddEdit = onNavigateToAddEdit
        )
    }

    composable(
        route = SUBJECT_ADD_EDIT_ROUTE,
        arguments = listOf(
            navArgument(SUBJECT_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val subjectId = backStackEntry.arguments?.getString(SUBJECT_ID_ARG)
        val viewModel: SubjectViewModel = hiltViewModel()
        AddEditSubjectScreen(
            viewModel = viewModel,
            subjectId = subjectId,
            onNavigateBack = onNavigateBack
        )
    }
}
