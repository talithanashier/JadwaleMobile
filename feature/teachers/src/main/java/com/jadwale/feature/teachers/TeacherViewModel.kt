package com.jadwale.feature.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.Teacher
import com.jadwale.core.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TeacherUiState {
    object Loading : TeacherUiState
    data class Success(
        val teachers: List<Teacher>,
        val searchQuery: String = "",
        val isSaving: Boolean = false,
        val successMessage: String? = null
    ) : TeacherUiState {
        val filteredTeachers: List<Teacher>
            get() = if (searchQuery.isBlank()) teachers else teachers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.nip.contains(searchQuery) ||
                it.subjects.any { subj -> subj.contains(searchQuery, ignoreCase = true) }
            }
    }
    data class Error(val message: String) : TeacherUiState
}

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherUiState>(TeacherUiState.Loading)
    val uiState: StateFlow<TeacherUiState> = _uiState.asStateFlow()

    init {
        loadTeachers()
    }

    fun loadTeachers(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { TeacherUiState.Loading }
            val result = teacherRepository.getTeachers(schoolId)
            if (result.isSuccess) {
                _uiState.update { TeacherUiState.Success(teachers = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { TeacherUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat data guru.") }
            }
        }
    }

    fun setSearchQuery(query: String) {
        val current = _uiState.value as? TeacherUiState.Success ?: return
        _uiState.update { current.copy(searchQuery = query) }
    }

    fun saveTeacher(
        id: String?,
        name: String,
        nip: String,
        email: String,
        phone: String,
        subjects: List<String>,
        totalJp: Int,
        teacherType: com.jadwale.core.model.TeacherType = com.jadwale.core.model.TeacherType.KEDUANYA,
        teacherStatus: com.jadwale.core.model.TeacherStatus = com.jadwale.core.model.TeacherStatus.PNS,
        availabilityNote: String = "Senin - Jumat Bersedia Penuh",
        availableDays: List<com.jadwale.core.model.DayOfWeek> = listOf(
            com.jadwale.core.model.DayOfWeek.SENIN,
            com.jadwale.core.model.DayOfWeek.SELASA,
            com.jadwale.core.model.DayOfWeek.RABU,
            com.jadwale.core.model.DayOfWeek.KAMIS,
            com.jadwale.core.model.DayOfWeek.JUMAT
        ),
        schoolId: String = "default_school",
        onSuccess: () -> Unit
    ) {
        val current = _uiState.value as? TeacherUiState.Success
        viewModelScope.launch {
            _uiState.update { current?.copy(isSaving = true) ?: TeacherUiState.Loading }

            val teacher = Teacher(
                id = id ?: "teacher_${System.currentTimeMillis()}",
                name = name,
                nip = nip,
                email = email,
                phone = phone,
                subjects = subjects,
                totalJp = totalJp,
                teacherType = teacherType,
                teacherStatus = teacherStatus,
                availabilityNote = availabilityNote,
                availableDays = availableDays,
                isVerified = true,
                schoolId = schoolId
            )

            val result = if (id.isNullOrBlank()) {
                teacherRepository.addTeacher(schoolId, teacher)
            } else {
                teacherRepository.updateTeacher(teacher)
            }

            if (result.isSuccess) {
                loadTeachers(schoolId)
                onSuccess()
            } else {
                _uiState.update { current?.copy(isSaving = false) ?: TeacherUiState.Error("Gagal menyimpan data guru.") }
            }
        }
    }

    fun deleteTeacher(teacherId: String, schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = teacherRepository.deleteTeacher(teacherId)
            if (result.isSuccess) {
                loadTeachers(schoolId)
            }
        }
    }
}
