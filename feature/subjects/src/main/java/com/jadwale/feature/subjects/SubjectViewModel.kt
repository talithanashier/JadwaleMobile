package com.jadwale.feature.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.Subject
import com.jadwale.core.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SubjectUiState {
    object Loading : SubjectUiState
    data class Success(val subjects: List<Subject>, val isSaving: Boolean = false) : SubjectUiState
    data class Error(val message: String) : SubjectUiState
}

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubjectUiState>(SubjectUiState.Loading)
    val uiState: StateFlow<SubjectUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    fun loadSubjects(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { SubjectUiState.Loading }
            val result = subjectRepository.getSubjects(schoolId)
            if (result.isSuccess) {
                _uiState.update { SubjectUiState.Success(subjects = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { SubjectUiState.Error("Gagal memuat mata pelajaran.") }
            }
        }
    }

    fun saveSubject(
        id: String?,
        code: String,
        name: String,
        jpPerWeek: Int,
        colorCategory: String,
        isPriority: Boolean = false,
        schoolId: String = "default_school",
        onSuccess: () -> Unit
    ) {
        val current = _uiState.value as? SubjectUiState.Success ?: return
        viewModelScope.launch {
            _uiState.update { current.copy(isSaving = true) }
            val subject = Subject(
                id = id ?: "subj_${System.currentTimeMillis()}",
                code = code,
                name = name,
                jpPerWeek = jpPerWeek,
                colorCategory = colorCategory,
                isPriority = isPriority
            )

            val result = if (id.isNullOrBlank()) {
                subjectRepository.addSubject(schoolId, subject)
            } else {
                subjectRepository.updateSubject(subject)
            }

            if (result.isSuccess) {
                loadSubjects(schoolId)
                onSuccess()
            } else {
                _uiState.update { current.copy(isSaving = false) }
            }
        }
    }

    fun toggleSubjectPriority(subjectId: String, isPriority: Boolean, schoolId: String = "default_school") {
        val current = _uiState.value as? SubjectUiState.Success ?: return
        val subject = current.subjects.find { it.id == subjectId } ?: return
        val updated = subject.copy(isPriority = isPriority)
        viewModelScope.launch {
            subjectRepository.updateSubject(updated)
            loadSubjects(schoolId)
        }
    }

    fun deleteSubject(subjectId: String, schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = subjectRepository.deleteSubject(subjectId)
            if (result.isSuccess) {
                loadSubjects(schoolId)
            }
        }
    }
}
