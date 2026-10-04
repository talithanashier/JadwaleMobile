package com.jadwale.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.*
import com.jadwale.core.repository.ClassRepository
import com.jadwale.core.repository.ScheduleRepository
import com.jadwale.core.repository.SubjectRepository
import com.jadwale.core.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ScheduleStatus(val label: String) {
    BELUM_DIBUAT("Belum Dibuat"),
    SUDAH_JADI("Siap Digunakan"),
    PERLU_REGENERATE("Perlu Regenerate")
}

data class SchoolStat(
    val id: String,
    val name: String,
    val totalTeachers: Int,
    val totalClasses: Int,
    val scheduleStatus: String
)

data class DashboardData(
    val schoolName: String = "SDN Pancasila 01",
    val academicYear: String = "2026/2027",
    val teacherCount: Int = 0,
    val classCount: Int = 0,
    val subjectCount: Int = 0,
    val scheduleStatus: ScheduleStatus = ScheduleStatus.BELUM_DIBUAT,
    val todaySlots: List<ScheduleSlot> = emptyList(),
    val weeklySlots: List<ScheduleSlot> = emptyList(),
    val allSchools: List<SchoolStat> = emptyList(),
    val totalUsers: Int = 142,
    val totalSchedules: Int = 18
)

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardData, val currentRole: UserRole = UserRole.ADMIN_SEKOLAH) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val subjectRepository: SubjectRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard(
        role: UserRole = UserRole.ADMIN_SEKOLAH,
        teacherId: String = "t1", // Default id guru jika role Guru
        schoolId: String = "default_school"
    ) {
        viewModelScope.launch {
            _uiState.update { DashboardUiState.Loading }

            val teachersResult = teacherRepository.getTeachers(schoolId)
            val classesResult = classRepository.getClasses(schoolId)
            val subjectsResult = subjectRepository.getSubjects(schoolId)
            val scheduleResult = scheduleRepository.getSchedule(schoolId)

            val teachers = teachersResult.getOrNull() ?: emptyList()
            val classes = classesResult.getOrNull() ?: emptyList()
            val subjects = subjectsResult.getOrNull() ?: emptyList()
            val schedule = scheduleResult.getOrNull()

            val status = when {
                schedule == null || schedule.slots.isEmpty() -> ScheduleStatus.BELUM_DIBUAT
                else -> ScheduleStatus.SUDAH_JADI
            }

            // Filter untuk Guru
            val allTeacherSlots = schedule?.slots?.filter { it.teacher?.id == teacherId } ?: emptyList()
            val today = DayOfWeek.SENIN // Default simulasi hari Senin
            val todayTeacherSlots = allTeacherSlots.filter { it.day == today }

            val mockSchools = listOf(
                SchoolStat("sch1", "SDN Pancasila 01", 12, 12, "Siap"),
                SchoolStat("sch2", "SDN Harapan Bangsa", 14, 12, "Siap"),
                SchoolStat("sch3", "SDN Merdeka 02", 10, 8, "Belum"),
                SchoolStat("sch4", "SD Budi Utomo", 16, 12, "Siap")
            )

            val data = DashboardData(
                schoolName = "SDN Pancasila 01",
                teacherCount = teachers.size,
                classCount = classes.size,
                subjectCount = subjects.size,
                scheduleStatus = status,
                todaySlots = todayTeacherSlots,
                weeklySlots = allTeacherSlots,
                allSchools = mockSchools,
                totalUsers = 142,
                totalSchedules = 18
            )

            _uiState.update { DashboardUiState.Success(data = data, currentRole = role) }
        }
    }
}
