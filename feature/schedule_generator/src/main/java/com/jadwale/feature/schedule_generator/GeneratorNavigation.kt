package com.jadwale.feature.schedule_generator

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SCHEDULE_GENERATOR_ROUTE = "schedule_generator_route"

fun NavController.navigateToScheduleGenerator(navOptions: NavOptions? = null) {
    this.navigate(SCHEDULE_GENERATOR_ROUTE, navOptions)
}

fun NavGraphBuilder.scheduleGeneratorScreen(
    onNavigateToScheduleView: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    composable(route = SCHEDULE_GENERATOR_ROUTE) {
        val viewModel: GeneratorViewModel = hiltViewModel()
        GeneratorScreen(
            viewModel = viewModel,
            onNavigateToScheduleView = onNavigateToScheduleView,
            onNavigateBack = onNavigateBack
        )
    }
}
