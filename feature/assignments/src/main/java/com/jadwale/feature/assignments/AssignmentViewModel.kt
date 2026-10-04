package com.jadwale.feature.assignments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.*
import com.jadwale.core.repository.AssignmentRepository
import com.jadwale.core.repository.ClassRepository
import com.jadwale.core.repository.SubjectRepository
import com.jadwale.core.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AssignmentUiState {
    object Loading : AssignmentUiState
    data class Success(
        val assignments: List<Assignment>,
        val teachers: List<Teacher>,
        val classes: List<ClassRoom>,
        val subjects: List<Subject>,
        val isSaving: Boolean = false
    ) : AssignmentUiState
    data class Error(val message: String) : AssignmentUiState
}

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssignmentUiState>(AssignmentUiState.Loading)
    val uiState: StateFlow<AssignmentUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { AssignmentUiState.Loading }

            val assignmentsRes = assignmentRepository.getAssignments(schoolId)
            val teachersRes = teacherRepository.getTeachers(schoolId)
            val classesRes = classRepository.getClasses(schoolId)
            val subjectsRes = subjectRepository.getSubjects(schoolId)

            if (assignmentsRes.isSuccess) {
                _uiState.update {
                    AssignmentUiState.Success(
                        assignments = assignmentsRes.getOrNull() ?: emptyList(),
                        teachers = teachersRes.getOrNull() ?: emptyList(),
                        classes = classesRes.getOrNull() ?: emptyList(),
                        subjects = subjectsRes.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update { AssignmentUiState.Error("Gagal memuat pembagian tugas mengajar.") }
            }
        }
    }

    fun saveAssignment(
        id: String?,
        teacher: Teacher,
        subject: Subject,
        classRoom: ClassRoom,
        totalJp: Int,
        schoolId: String = "default_school",
        onSuccess: () -> Unit
    ) {
        val current = _uiState.value as? AssignmentUiState.Success ?: return
        viewModelScope.launch {
            _uiState.update { current.copy(isSaving = true) }

            val assignment = Assignment(
                id = id ?: "assign_${System.currentTimeMillis()}",
                teacherId = teacher.id,
                teacherName = teacher.name,
                subjectId = subject.id,
                subjectName = subject.name,
                classId = classRoom.id,
                className = classRoom.name,
                totalJp = totalJp
            )

            val result = if (id.isNullOrBlank()) {
                assignmentRepository.addAssignment(schoolId, assignment)
            } else {
                assignmentRepository.updateAssignment(assignment)
            }

            if (result.isSuccess) {
                loadData(schoolId)
                onSuccess()
            } else {
                _uiState.update { current.copy(isSaving = false) }
            }
        }
    }

    fun deleteAssignment(assignmentId: String, schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = assignmentRepository.deleteAssignment(assignmentId)
            if (result.isSuccess) {
                loadData(schoolId)
            }
        }
    }
}
