package com.jadwale.feature.routine_activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.DayOfWeek
import com.jadwale.core.model.RoutineActivity
import com.jadwale.core.model.RoutineType
import com.jadwale.core.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RoutineUiState {
    object Loading : RoutineUiState
    data class Success(val routines: List<RoutineActivity>, val isSaving: Boolean = false) : RoutineUiState
    data class Error(val message: String) : RoutineUiState
}

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoutineUiState>(RoutineUiState.Loading)
    val uiState: StateFlow<RoutineUiState> = _uiState.asStateFlow()

    init {
        loadRoutines()
    }

    fun loadRoutines(schoolId: String = "default_school") {
        viewModelScope.launch {
            _uiState.update { RoutineUiState.Loading }
            val result = routineRepository.getRoutines(schoolId)
            if (result.isSuccess) {
                _uiState.update { RoutineUiState.Success(routines = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { RoutineUiState.Error("Gagal memuat kegiatan rutin.") }
            }
        }
    }

    fun saveRoutine(
        id: String?,
        name: String,
        type: RoutineType,
        day: DayOfWeek,
        startTime: String,
        endTime: String,
        schoolId: String = "default_school",
        onSuccess: () -> Unit
    ) {
        val currentList = (_uiState.value as? RoutineUiState.Success)?.routines ?: emptyList()
        viewModelScope.launch {
            _uiState.update { RoutineUiState.Success(routines = currentList, isSaving = true) }
            val routine = RoutineActivity(
                id = id ?: "routine_${System.currentTimeMillis()}",
                name = name,
                type = type,
                day = day,
                startTime = startTime,
                endTime = endTime
            )

            val result = if (id.isNullOrBlank()) {
                routineRepository.addRoutine(schoolId, routine)
            } else {
                routineRepository.updateRoutine(routine)
            }

            if (result.isSuccess) {
                loadRoutines(schoolId)
                onSuccess()
            } else {
                _uiState.update { RoutineUiState.Success(routines = currentList, isSaving = false) }
            }
        }
    }

    fun deleteRoutine(routineId: String, schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = routineRepository.deleteRoutine(routineId)
            if (result.isSuccess) {
                loadRoutines(schoolId)
            }
        }
    }
}
