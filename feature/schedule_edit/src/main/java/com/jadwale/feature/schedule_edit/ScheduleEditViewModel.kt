package com.jadwale.feature.schedule_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.*
import com.jadwale.core.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScheduleEditUiState {
    object Loading : ScheduleEditUiState
    data class Success(
        val classes: List<ClassRoom>,
        val teachers: List<Teacher>,
        val subjects: List<Subject>,
        val selectedClassId: String,
        val fullSchedule: Schedule,
        val classSlots: List<ScheduleSlot>,
        val editingSlot: ScheduleSlot? = null,
        val conflictError: String? = null,
        val isSaving: Boolean = false,
        val saveSuccessMessage: String? = null
    ) : ScheduleEditUiState
    data class Error(val message: String) : ScheduleEditUiState
}

@HiltViewModel
class ScheduleEditViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleEditUiState>(ScheduleEditUiState.Loading)
    val uiState: StateFlow<ScheduleEditUiState> = _uiState.asStateFlow()

    fun initialize(initialClassId: String = "", schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { ScheduleEditUiState.Loading }

            val classesResult = classRepository.getClasses(schoolId)
            val teachersResult = teacherRepository.getTeachers(schoolId)
            val subjectsResult = subjectRepository.getSubjects(schoolId)
            val scheduleResult = scheduleRepository.getSchedule(schoolId)

            if (classesResult.isSuccess && teachersResult.isSuccess && subjectsResult.isSuccess && scheduleResult.isSuccess) {
                val classes = classesResult.getOrNull() ?: emptyList()
                val teachers = teachersResult.getOrNull() ?: emptyList()
                val subjects = subjectsResult.getOrNull() ?: emptyList()
                val schedule = scheduleResult.getOrNull() ?: Schedule(id = "", schoolId = schoolId)

                val activeClassId = if (initialClassId.isNotBlank() && classes.any { it.id == initialClassId }) {
                    initialClassId
                } else {
                    classes.firstOrNull()?.id ?: ""
                }

                val filtered = schedule.slots.filter { it.classId == activeClassId }

                _uiState.update {
                    ScheduleEditUiState.Success(
                        classes = classes,
                        teachers = teachers,
                        subjects = subjects,
                        selectedClassId = activeClassId,
                        fullSchedule = schedule,
                        classSlots = filtered
                    )
                }
            } else {
                _uiState.update {
                    ScheduleEditUiState.Error("Gagal memuat data master untuk form edit jadwal.")
                }
            }
        }
    }

    fun selectClass(classId: String) {
        val current = _uiState.value as? ScheduleEditUiState.Success ?: return
        val filtered = current.fullSchedule.slots.filter { it.classId == classId }
        _uiState.update {
            current.copy(
                selectedClassId = classId,
                classSlots = filtered,
                editingSlot = null,
                conflictError = null
            )
        }
    }

    fun openSlotEditor(slot: ScheduleSlot) {
        val current = _uiState.value as? ScheduleEditUiState.Success ?: return
        // Jangan edit kegiatan rutin tetap seperti Upacara / Istirahat
        if (slot.isRoutine) return
        _uiState.update { current.copy(editingSlot = slot, conflictError = null) }
    }

    fun closeSlotEditor() {
        val current = _uiState.value as? ScheduleEditUiState.Success ?: return
        _uiState.update { current.copy(editingSlot = null, conflictError = null) }
    }

    fun saveSlotEdit(
        newSubject: Subject,
        newTeacher: Teacher,
        newRoom: String
    ) {
        val current = _uiState.value as? ScheduleEditUiState.Success ?: return
        val slotToEdit = current.editingSlot ?: return

        // 1. Validasi Bentrok Guru: Apakah guru tersebut sedang mengajar di kelas lain pada hari & jam yang sama?
        val isTeacherConflict = current.fullSchedule.slots.any { otherSlot ->
            otherSlot.id != slotToEdit.id &&
            otherSlot.day == slotToEdit.day &&
            otherSlot.timeSlot.id == slotToEdit.timeSlot.id &&
            otherSlot.teacher?.id == newTeacher.id
        }

        if (isTeacherConflict) {
            _uiState.update {
                current.copy(
                    conflictError = "Bentrok! Guru ${newTeacher.name} sudah memiliki jadwal mengajar di kelas lain pada hari ${slotToEdit.day.displayName} ${slotToEdit.timeSlot.startTime}-${slotToEdit.timeSlot.endTime}."
                )
            }
            return
        }

        // 2. Simpan jika tidak bentrok
        viewModelScope.launch {
            _uiState.update { current.copy(isSaving = true, conflictError = null) }

            val updatedSlot = slotToEdit.copy(
                subject = newSubject,
                teacher = newTeacher,
                room = newRoom
            )

            val updateResult = scheduleRepository.updateSlot(updatedSlot.id, updatedSlot)

            if (updateResult.isSuccess) {
                val newFullSlots = current.fullSchedule.slots.map {
                    if (it.id == updatedSlot.id) updatedSlot else it
                }
                val newSchedule = current.fullSchedule.copy(slots = newFullSlots)
                val newClassSlots = newSchedule.slots.filter { it.classId == current.selectedClassId }

                _uiState.update {
                    current.copy(
                        fullSchedule = newSchedule,
                        classSlots = newClassSlots,
                        editingSlot = null,
                        conflictError = null,
                        isSaving = false,
                        saveSuccessMessage = "Jadwal ${newSubject.name} berhasil diperbarui!"
                    )
                }
            } else {
                _uiState.update {
                    current.copy(
                        isSaving = false,
                        conflictError = "Gagal menyimpan perubahan ke server."
                    )
                }
            }
        }
    }

    fun dismissSuccessMessage() {
        val current = _uiState.value as? ScheduleEditUiState.Success ?: return
        _uiState.update { current.copy(saveSuccessMessage = null) }
    }
}
