package com.jadwale.feature.schedule_view

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SCHEDULE_VIEW_ROUTE = "schedule_view_route"

fun NavController.navigateToScheduleView(navOptions: NavOptions? = null) {
    this.navigate(SCHEDULE_VIEW_ROUTE, navOptions)
}

fun NavGraphBuilder.scheduleViewScreen(
    userRole: () -> com.jadwale.core.model.UserRole = { com.jadwale.core.model.UserRole.ADMIN_SEKOLAH },
    userName: () -> String? = { null },
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onNavigateToEdit: (classId: String) -> Unit,
    onNavigateToGenerator: () -> Unit
) {
    composable(route = SCHEDULE_VIEW_ROUTE) {
        val viewModel: ScheduleViewViewModel = hiltViewModel()
        ScheduleViewScreen(
            viewModel = viewModel,
            userRole = userRole(),
            userName = userName(),
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToMenu = onNavigateToMenu,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToGenerator = onNavigateToGenerator
        )
    }
}
