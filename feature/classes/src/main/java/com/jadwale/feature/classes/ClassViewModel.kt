package com.jadwale.feature.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.ClassRoom
import com.jadwale.core.repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ClassUiState {
    object Loading : ClassUiState
    data class Success(val classes: List<ClassRoom>, val isSaving: Boolean = false) : ClassUiState
    data class Error(val message: String) : ClassUiState
}

@HiltViewModel
class ClassViewModel @Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClassUiState>(ClassUiState.Loading)
    val uiState: StateFlow<ClassUiState> = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    fun loadClasses(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { ClassUiState.Loading }
            val result = classRepository.getClasses(schoolId)
            if (result.isSuccess) {
                _uiState.update { ClassUiState.Success(classes = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { ClassUiState.Error("Gagal memuat data kelas.") }
            }
        }
    }

    fun saveClass(
        id: String?,
        name: String,
        grade: Int,
        studentCount: Int,
        homeroomTeacher: String,
        schoolId: String = "default_school",
        onSuccess: () -> Unit
    ) {
        val current = _uiState.value as? ClassUiState.Success
        viewModelScope.launch {
            _uiState.update { current?.copy(isSaving = true) ?: ClassUiState.Loading }
            val classRoom = ClassRoom(
                id = id ?: "class_${System.currentTimeMillis()}",
                name = name,
                grade = grade,
                studentCount = studentCount,
                homeroomTeacher = homeroomTeacher
            )

            val result = if (id.isNullOrBlank()) {
                classRepository.addClass(schoolId, classRoom)
            } else {
                classRepository.updateClass(classRoom)
            }

            if (result.isSuccess) {
                loadClasses(schoolId)
                onSuccess()
            } else {
                _uiState.update { current?.copy(isSaving = false) ?: ClassUiState.Error("Gagal menyimpan kelas.") }
            }
        }
    }

    fun deleteClass(classId: String, schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = classRepository.deleteClass(classId)
            if (result.isSuccess) {
                loadClasses(schoolId)
            }
        }
    }
}
