package com.jadwale.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jadwale.core.model.UserRole

const val LOGIN_ROUTE = "login_route"
const val REGISTER_ROUTE = "register_route"
const val DASHBOARD_BASE_ROUTE = "dashboard_route"
const val DASHBOARD_ROUTE = "$DASHBOARD_BASE_ROUTE/{role}?account={account}"
const val MENU_ROUTE = "menu_route"

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    this.navigate(LOGIN_ROUTE, navOptions)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(REGISTER_ROUTE, navOptions)
}

fun NavController.navigateToDashboard(
    role: UserRole = UserRole.ADMIN_SEKOLAH,
    account: String = "default",
    navOptions: NavOptions? = null
) {
    val cleanAccount = if (account.isBlank()) "default" else account
    this.navigate("$DASHBOARD_BASE_ROUTE/${role.name}?account=$cleanAccount", navOptions)
}

fun NavController.navigateToMenu(navOptions: NavOptions? = null) {
    this.navigate(MENU_ROUTE, navOptions)
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: (role: UserRole, accountName: String) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    composable(route = LOGIN_ROUTE) {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onNavigateToRegister = onNavigateToRegister
        )
    }
}

fun NavGraphBuilder.registerScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (role: UserRole, accountName: String) -> Unit
) {
    composable(route = REGISTER_ROUTE) {
        RegisterScreen(
            onNavigateToLogin = onNavigateToLogin,
            onRegisterSuccess = onRegisterSuccess
        )
    }
}

fun NavGraphBuilder.dashboardScreen(
    onNavigateToScheduleView: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToRoutines: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    composable(
        route = DASHBOARD_ROUTE,
        arguments = listOf(
            navArgument("role") {
                type = NavType.StringType
                defaultValue = UserRole.ADMIN_SEKOLAH.name
            },
            navArgument("account") {
                type = NavType.StringType
                defaultValue = "default"
            }
        )
    ) { backStackEntry ->
        val roleParam = backStackEntry.arguments?.getString("role")
        val currentRole = try {
            UserRole.valueOf(roleParam ?: UserRole.ADMIN_SEKOLAH.name)
        } catch (e: Exception) {
            UserRole.ADMIN_SEKOLAH
        }
        val currentAccount = backStackEntry.arguments?.getString("account") ?: "default"

        val viewModel: DashboardViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(currentRole) {
            viewModel.loadDashboard(role = currentRole)
        }

        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1D68E4))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Memuat Dashboard Jadwale...",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
            is DashboardUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Gagal Memuat Dashboard",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadDashboard(role = currentRole) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
            is DashboardUiState.Success -> {
                when (state.currentRole) {
                    UserRole.ADMIN_SEKOLAH -> {
                        AdminDashboardScreen(
                            data = state.data,
                            onNavigateToScheduleView = onNavigateToScheduleView,
                            onNavigateToGenerator = onNavigateToGenerator,
                            onNavigateToTeachers = onNavigateToTeachers,
                            onNavigateToClasses = onNavigateToClasses,
                            onNavigateToSubjects = onNavigateToSubjects,
                            onNavigateToRoutines = onNavigateToRoutines,
                            onNavigateToAssignments = onNavigateToAssignments,
                            onNavigateToMenu = onNavigateToMenu,
                            onSwitchRole = onSwitchRole,
                            onLogout = onLogout
                        )
                    }
                    UserRole.GURU -> {
                        val teacherName = if (currentAccount.contains("bambang", ignoreCase = true) || currentAccount.contains("guru", ignoreCase = true) || currentAccount == "default") {
                            "Bpk. Bambang Sutrisno, M.Pd"
                        } else {
                            currentAccount
                        }
                        TeacherDashboardScreen(
                            data = state.data,
                            teacherName = teacherName,
                            onNavigateToScheduleView = onNavigateToScheduleView,
                            onNavigateToMenu = onNavigateToMenu,
                            onSwitchRole = onSwitchRole,
                            onLogout = onLogout
                        )
                    }
                    UserRole.SUPER_ADMIN -> {
                        SuperAdminDashboardScreen(
                            data = state.data,
                            onNavigateToScheduleView = onNavigateToScheduleView,
                            onNavigateToMenu = onNavigateToMenu,
                            onSwitchRole = onSwitchRole,
                            onLogout = onLogout
                        )
                    }
                    UserRole.UMUM -> {
                        ParentScheduleScreen(
                            onLoginClick = onNavigateToLogin,
                            onSwitchRole = onSwitchRole,
                            onNavigateToScheduleView = onNavigateToScheduleView,
                            onNavigateToMenu = onNavigateToMenu
                        )
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.menuScreen(
    currentRole: () -> UserRole = { UserRole.ADMIN_SEKOLAH },
    userName: () -> String? = { null },
    schoolName: String = "SDN Pancasila 01",
    onNavigateToDashboard: () -> Unit,
    onNavigateToScheduleView: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onLogout: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {}
) {
    composable(route = MENU_ROUTE) {
        MenuScreen(
            currentRole = currentRole(),
            userName = userName(),
            schoolName = schoolName,
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToScheduleView = onNavigateToScheduleView,
            onNavigateToTeachers = onNavigateToTeachers,
            onNavigateToClasses = onNavigateToClasses,
            onNavigateToSubjects = onNavigateToSubjects,
            onNavigateToRoutines = onNavigateToRoutines,
            onNavigateToAssignments = onNavigateToAssignments,
            onNavigateToGenerator = onNavigateToGenerator,
            onLogout = onLogout,
            onSwitchRole = onSwitchRole
        )
    }
}
