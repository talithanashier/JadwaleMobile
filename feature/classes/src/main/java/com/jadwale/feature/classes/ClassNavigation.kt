package com.jadwale.feature.classes

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val CLASS_LIST_ROUTE = "class_list_route"
const val CLASS_ADD_EDIT_BASE_ROUTE = "class_add_edit_route"
const val CLASS_ID_ARG = "classId"
const val CLASS_ADD_EDIT_ROUTE = "$CLASS_ADD_EDIT_BASE_ROUTE?classId={$CLASS_ID_ARG}"

fun NavController.navigateToClassList(navOptions: NavOptions? = null) {
    this.navigate(CLASS_LIST_ROUTE, navOptions)
}

fun NavController.navigateToClassAddEdit(classId: String? = null) {
    val route = if (classId != null) "$CLASS_ADD_EDIT_BASE_ROUTE?classId=$classId" else CLASS_ADD_EDIT_BASE_ROUTE
    this.navigate(route)
}

fun NavGraphBuilder.classScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (classId: String?) -> Unit
) {
    composable(route = CLASS_LIST_ROUTE) {
        val viewModel: ClassViewModel = hiltViewModel()
        ClassListScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToAddEdit = onNavigateToAddEdit
        )
    }

    composable(
        route = CLASS_ADD_EDIT_ROUTE,
        arguments = listOf(
            navArgument(CLASS_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val classId = backStackEntry.arguments?.getString(CLASS_ID_ARG)
        val viewModel: ClassViewModel = hiltViewModel()
        AddEditClassScreen(
            viewModel = viewModel,
            classId = classId,
            onNavigateBack = onNavigateBack
        )
    }
}
