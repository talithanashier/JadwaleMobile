package com.jadwale.feature.schedule_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.ClassRoom
import com.jadwale.core.model.Schedule
import com.jadwale.core.model.ScheduleSlot
import com.jadwale.core.model.Teacher
import com.jadwale.core.model.UserRole
import com.jadwale.core.repository.ClassRepository
import com.jadwale.core.repository.ScheduleRepository
import com.jadwale.core.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScheduleViewUiState {
    object Loading : ScheduleViewUiState
    data class Success(
        val schedule: Schedule,
        val classes: List<ClassRoom>,
        val teachers: List<Teacher> = emptyList(),
        val selectedClassIndex: Int = 0,
        val selectedTeacherIndex: Int = 0,
        val selectedSegment: Int = 0, // 0: Per Kelas, 1: Per Guru, 2: 12 Kelas
        val filteredSlots: List<ScheduleSlot> = emptyList(),
        val selectedSlotDetail: ScheduleSlot? = null,
        val isRefreshing: Boolean = false
    ) : ScheduleViewUiState
    data class Error(val message: String) : ScheduleViewUiState
}

@HiltViewModel
class ScheduleViewViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleViewUiState>(ScheduleViewUiState.Loading)
    val uiState: StateFlow<ScheduleViewUiState> = _uiState.asStateFlow()

    private var currentRole: UserRole = UserRole.ADMIN_SEKOLAH
    private var currentUserName: String? = null

    init {
        loadSchedule()
    }

    fun setRole(role: UserRole, userName: String? = null) {
        currentRole = role
        currentUserName = userName
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return

        val targetSegment = if (role == UserRole.GURU) 1 else 0
        val targetTeacherIdx = if (role == UserRole.GURU) {
            val idx = currentState.teachers.indexOfFirst {
                it.name.contains("bambang", ignoreCase = true) ||
                (userName != null && (it.name.contains(userName, ignoreCase = true) || userName.contains(it.name, ignoreCase = true)))
            }
            if (idx >= 0) idx else 0
        } else currentState.selectedTeacherIndex

        val filtered = if (targetSegment == 1) {
            val activeTeacher = currentState.teachers.getOrNull(targetTeacherIdx)
            if (activeTeacher != null) currentState.schedule.slots.filter { it.teacher?.id == activeTeacher.id }
            else currentState.schedule.slots
        } else {
            val activeClass = currentState.classes.getOrNull(currentState.selectedClassIndex)
            if (activeClass != null) currentState.schedule.slots.filter { it.classId == activeClass.id }
            else currentState.schedule.slots
        }

        _uiState.update {
            currentState.copy(
                selectedSegment = targetSegment,
                selectedTeacherIndex = targetTeacherIdx,
                filteredSlots = filtered
            )
        }
    }

    fun loadSchedule(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { ScheduleViewUiState.Loading }

            val classesResult = classRepository.getClasses(schoolId)
            val teachersResult = teacherRepository.getTeachers(schoolId)
            val scheduleResult = scheduleRepository.getSchedule(schoolId)

            if (classesResult.isSuccess && scheduleResult.isSuccess) {
                val classes = classesResult.getOrNull() ?: emptyList()
                val teachers = teachersResult.getOrNull() ?: emptyList()
                val schedule = scheduleResult.getOrNull() ?: Schedule(id = "", schoolId = schoolId)

                val targetSegment = if (currentRole == UserRole.GURU) 1 else 0
                val targetTeacherIdx = if (currentRole == UserRole.GURU) {
                    val idx = teachers.indexOfFirst {
                        it.name.contains("bambang", ignoreCase = true) ||
                        (currentUserName != null && (it.name.contains(currentUserName!!, ignoreCase = true) || currentUserName!!.contains(it.name, ignoreCase = true)))
                    }
                    if (idx >= 0) idx else 0
                } else 0

                val initialFiltered = if (targetSegment == 1) {
                    val activeTeacher = teachers.getOrNull(targetTeacherIdx)
                    if (activeTeacher != null) schedule.slots.filter { it.teacher?.id == activeTeacher.id }
                    else schedule.slots
                } else {
                    val initialClassId = classes.firstOrNull()?.id ?: ""
                    schedule.slots.filter { it.classId == initialClassId }
                }

                _uiState.update {
                    ScheduleViewUiState.Success(
                        schedule = schedule,
                        classes = classes,
                        teachers = teachers,
                        selectedClassIndex = 0,
                        selectedTeacherIndex = targetTeacherIdx,
                        selectedSegment = targetSegment,
                        filteredSlots = initialFiltered
                    )
                }
            } else {
                val errorMsg = scheduleResult.exceptionOrNull()?.message
                    ?: classesResult.exceptionOrNull()?.message
                    ?: "Gagal memuat jadwal pelajaran sekolah."
                _uiState.update { ScheduleViewUiState.Error(errorMsg) }
            }
        }
    }

    fun selectSegment(segment: Int) {
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return
        val filtered = when (segment) {
            0 -> {
                val activeClass = currentState.classes.getOrNull(currentState.selectedClassIndex)
                if (activeClass != null) currentState.schedule.slots.filter { it.classId == activeClass.id }
                else currentState.schedule.slots
            }
            1 -> {
                val activeTeacher = currentState.teachers.getOrNull(currentState.selectedTeacherIndex)
                if (activeTeacher != null) currentState.schedule.slots.filter { it.teacher?.id == activeTeacher.id }
                else currentState.schedule.slots
            }
            else -> currentState.schedule.slots
        }

        _uiState.update {
            currentState.copy(
                selectedSegment = segment,
                filteredSlots = filtered
            )
        }
    }

    fun selectClassTab(index: Int, keepSegment: Boolean = false) {
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return
        if (index !in currentState.classes.indices) return

        val targetClass = currentState.classes[index]
        val filtered = currentState.schedule.slots.filter { it.classId == targetClass.id }

        _uiState.update {
            currentState.copy(
                selectedClassIndex = index,
                selectedSegment = if (keepSegment) currentState.selectedSegment else 0,
                filteredSlots = filtered
            )
        }
    }

    fun showAll12Classes() {
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return
        _uiState.update {
            currentState.copy(
                selectedSegment = 2,
                filteredSlots = currentState.schedule.slots
            )
        }
    }

    fun selectTeacherTab(index: Int) {
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return
        if (index !in currentState.teachers.indices) return

        val targetTeacher = currentState.teachers[index]
        val filtered = currentState.schedule.slots.filter { it.teacher?.id == targetTeacher.id }

        _uiState.update {
            currentState.copy(
                selectedTeacherIndex = index,
                selectedSegment = 1,
                filteredSlots = filtered
            )
        }
    }

    fun selectSlotForDetail(slot: ScheduleSlot?) {
        val currentState = _uiState.value as? ScheduleViewUiState.Success ?: return
        _uiState.update { currentState.copy(selectedSlotDetail = slot) }
    }

    fun refreshSchedule(schoolId: String = "default_school") {
        val currentState = _uiState.value as? ScheduleViewUiState.Success
        viewModelScope.launch {
            if (currentState != null) {
                _uiState.update { currentState.copy(isRefreshing = true) }
            }

            val classesResult = classRepository.getClasses(schoolId)
            val teachersResult = teacherRepository.getTeachers(schoolId)
            val scheduleResult = scheduleRepository.getSchedule(schoolId)

            if (classesResult.isSuccess && scheduleResult.isSuccess) {
                val classes = classesResult.getOrNull() ?: emptyList()
                val teachers = teachersResult.getOrNull() ?: emptyList()
                val schedule = scheduleResult.getOrNull() ?: Schedule(id = "", schoolId = schoolId)
                val currentIndex = currentState?.selectedClassIndex ?: 0
                val safeIndex = if (currentIndex in classes.indices) currentIndex else 0
                val activeClassId = classes.getOrNull(safeIndex)?.id ?: ""
                val filtered = schedule.slots.filter { it.classId == activeClassId }

                _uiState.update {
                    ScheduleViewUiState.Success(
                        schedule = schedule,
                        classes = classes,
                        teachers = teachers,
                        selectedClassIndex = safeIndex,
                        selectedTeacherIndex = currentState?.selectedTeacherIndex ?: 0,
                        selectedSegment = currentState?.selectedSegment ?: 0,
                        filteredSlots = filtered,
                        isRefreshing = false
                    )
                }
            } else {
                _uiState.update {
                    ScheduleViewUiState.Error("Gagal memperbarui jadwal.")
                }
            }
        }
    }
}
