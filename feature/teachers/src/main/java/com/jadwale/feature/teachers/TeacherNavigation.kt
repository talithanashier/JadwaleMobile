package com.jadwale.feature.teachers

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val TEACHER_LIST_ROUTE = "teacher_list_route"
const val TEACHER_ADD_EDIT_BASE_ROUTE = "teacher_add_edit_route"
const val TEACHER_ID_ARG = "teacherId"
const val TEACHER_ADD_EDIT_ROUTE = "$TEACHER_ADD_EDIT_BASE_ROUTE?teacherId={$TEACHER_ID_ARG}"

fun NavController.navigateToTeacherList(navOptions: NavOptions? = null) {
    this.navigate(TEACHER_LIST_ROUTE, navOptions)
}

fun NavController.navigateToTeacherAddEdit(teacherId: String? = null) {
    val route = if (teacherId != null) "$TEACHER_ADD_EDIT_BASE_ROUTE?teacherId=$teacherId" else TEACHER_ADD_EDIT_BASE_ROUTE
    this.navigate(route)
}

fun NavGraphBuilder.teacherScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (teacherId: String?) -> Unit
) {
    composable(route = TEACHER_LIST_ROUTE) {
        val viewModel: TeacherViewModel = hiltViewModel()
        TeacherListScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToAddEdit = onNavigateToAddEdit
        )
    }

    composable(
        route = TEACHER_ADD_EDIT_ROUTE,
        arguments = listOf(
            navArgument(TEACHER_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val teacherId = backStackEntry.arguments?.getString(TEACHER_ID_ARG)
        val viewModel: TeacherViewModel = hiltViewModel()
        AddEditTeacherScreen(
            viewModel = viewModel,
            teacherId = teacherId,
            onNavigateBack = onNavigateBack
        )
    }
}
