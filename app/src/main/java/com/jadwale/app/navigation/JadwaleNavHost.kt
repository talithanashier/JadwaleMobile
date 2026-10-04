package com.jadwale.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jadwale.core.model.UserRole
import com.jadwale.feature.dashboard.*
import com.jadwale.feature.schedule_generator.*
import com.jadwale.feature.schedule_view.*
import com.jadwale.feature.schedule_edit.*
import com.jadwale.feature.teachers.*
import com.jadwale.feature.classes.*
import com.jadwale.feature.subjects.*
import com.jadwale.feature.routine_activities.*
import com.jadwale.feature.assignments.*

@Composable
fun JadwaleNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    initialRole: UserRole = UserRole.ADMIN_SEKOLAH
) {
    var activeRole by remember { mutableStateOf(initialRole) }
    var activeUserName by remember {
        mutableStateOf<String?>(
            when (initialRole) {
                UserRole.SUPER_ADMIN -> "Administrator Dinas Pendidikan"
                UserRole.ADMIN_SEKOLAH -> "Operator Kurikulum SDN Pancasila 01"
                UserRole.GURU -> "Bpk. Bambang Sutrisno, M.Pd"
                UserRole.UMUM -> "Wali Murid / Tamu Umum"
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = LOGIN_ROUTE,
        modifier = modifier
    ) {
        // 0. Login Screen (Layar Masuk 3-Peran + Tamu Publik)
        loginScreen(
            onLoginSuccess = { role, accountName ->
                activeRole = role
                activeUserName = when (role) {
                    UserRole.SUPER_ADMIN -> "Administrator Dinas Pendidikan"
                    UserRole.ADMIN_SEKOLAH -> "Operator Kurikulum SDN Pancasila 01"
                    UserRole.GURU -> if (accountName.contains("bambang", ignoreCase = true) || accountName.contains("guru", ignoreCase = true) || accountName == "default") "Bpk. Bambang Sutrisno, M.Pd" else accountName
                    UserRole.UMUM -> "Wali Murid / Tamu Umum"
                }
                navController.navigateToDashboard(
                    role = role,
                    account = accountName,
                    navOptions = navOptions {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                )
            },
            onNavigateToRegister = {
                navController.navigateToRegister()
            }
        )

        // 0b. Register Screen (Pendaftaran Akun Baru)
        registerScreen(
            onNavigateToLogin = {
                navController.popBackStack()
            },
            onRegisterSuccess = { role, accountName ->
                activeRole = role
                activeUserName = accountName
                navController.navigateToDashboard(
                    role = role,
                    account = accountName,
                    navOptions = navOptions {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                )
            }
        )

        // 1. Dashboard (Beranda adaptif sesuai peran)
        dashboardScreen(
            onNavigateToScheduleView = {
                if (activeRole != UserRole.SUPER_ADMIN) {
                    navController.navigateToScheduleView()
                } else {
                    navController.navigateToDashboard(role = UserRole.SUPER_ADMIN)
                }
            },
            onNavigateToGenerator = { navController.navigateToScheduleGenerator() },
            onNavigateToTeachers = { navController.navigateToTeacherList() },
            onNavigateToClasses = { navController.navigateToClassList() },
            onNavigateToSubjects = { navController.navigateToSubjectList() },
            onNavigateToRoutines = { navController.navigateToRoutineList() },
            onNavigateToAssignments = { navController.navigateToAssignmentList() },
            onNavigateToMenu = { navController.navigateToMenu() },
            onSwitchRole = { newRole ->
                activeRole = newRole
                activeUserName = when (newRole) {
                    UserRole.SUPER_ADMIN -> "Administrator Dinas Pendidikan"
                    UserRole.ADMIN_SEKOLAH -> "Operator Kurikulum SDN Pancasila 01"
                    UserRole.GURU -> "Bpk. Bambang Sutrisno, M.Pd"
                    UserRole.UMUM -> "Wali Murid / Tamu Umum"
                }
                navController.navigateToDashboard(
                    role = newRole,
                    account = activeUserName ?: "default",
                    navOptions = navOptions {
                        popUpTo(DASHBOARD_BASE_ROUTE) { inclusive = true }
                    }
                )
            },
            onLogout = {
                activeRole = UserRole.ADMIN_SEKOLAH
                activeUserName = null
                navController.navigate(LOGIN_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.navigate(LOGIN_ROUTE)
            }
        )

        // 2. Schedule View & Detail (Jadwal)
        scheduleViewScreen(
            userRole = { activeRole },
            userName = { activeUserName },
            onNavigateToDashboard = { navController.navigateToDashboard(role = activeRole) },
            onNavigateToMenu = { navController.navigateToMenu() },
            onNavigateToEdit = { classId -> navController.navigateToScheduleEdit(classId) },
            onNavigateToGenerator = { navController.navigateToScheduleGenerator() }
        )

        // 3. Menu Modul Lengkap
        menuScreen(
            currentRole = { activeRole },
            userName = { activeUserName },
            onNavigateToDashboard = { navController.navigateToDashboard(role = activeRole) },
            onNavigateToScheduleView = {
                if (activeRole != UserRole.SUPER_ADMIN) {
                    navController.navigateToScheduleView()
                } else {
                    navController.navigateToDashboard(role = UserRole.SUPER_ADMIN)
                }
            },
            onNavigateToTeachers = { navController.navigateToTeacherList() },
            onNavigateToClasses = { navController.navigateToClassList() },
            onNavigateToSubjects = { navController.navigateToSubjectList() },
            onNavigateToRoutines = { navController.navigateToRoutineList() },
            onNavigateToAssignments = { navController.navigateToAssignmentList() },
            onNavigateToGenerator = { navController.navigateToScheduleGenerator() },
            onLogout = {
                activeRole = UserRole.ADMIN_SEKOLAH
                activeUserName = null
                navController.navigate(LOGIN_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSwitchRole = { newRole ->
                activeRole = newRole
                activeUserName = when (newRole) {
                    UserRole.SUPER_ADMIN -> "Administrator Dinas Pendidikan"
                    UserRole.ADMIN_SEKOLAH -> "Operator Kurikulum SDN Pancasila 01"
                    UserRole.GURU -> "Bpk. Bambang Sutrisno, M.Pd"
                    UserRole.UMUM -> "Wali Murid / Tamu Umum"
                }
                navController.navigateToDashboard(
                    role = newRole,
                    account = activeUserName ?: "default",
                    navOptions = navOptions {
                        popUpTo(DASHBOARD_BASE_ROUTE) { inclusive = true }
                    }
                )
            }
        )

        // 4. AI Generator
        scheduleGeneratorScreen(
            onNavigateToScheduleView = { navController.navigateToScheduleView() },
            onNavigateBack = { navController.popBackStack() }
        )

        // 5. Edit Manual
        scheduleEditScreen(
            onNavigateBack = { navController.popBackStack() }
        )

        // 6. Master Data Guru
        teacherScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddEdit = { id -> navController.navigateToTeacherAddEdit(id) }
        )

        // 7. Master Data Kelas
        classScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddEdit = { id -> navController.navigateToClassAddEdit(id) }
        )

        // 8. Master Data Mapel
        subjectScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddEdit = { id -> navController.navigateToSubjectAddEdit(id) }
        )

        // 9. Kegiatan Rutin
        routineScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddEdit = { id -> navController.navigateToRoutineAddEdit(id) }
        )

        // 10. Pembagian Jam Mengajar
        assignmentScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddEdit = { id -> navController.navigateToAssignmentAddEdit(id) }
        )
    }
}
