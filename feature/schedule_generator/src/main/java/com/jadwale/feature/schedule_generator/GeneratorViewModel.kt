package com.jadwale.feature.schedule_generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadwale.core.model.GenerateProgress
import com.jadwale.core.model.Schedule
import com.jadwale.core.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneratorSummary(
    val totalClasses: Int = 12,
    val totalSlots: Int = 540,
    val conflictCount: Int = 0,
    val durationSeconds: Long = 4
)

sealed interface GeneratorUiState {
    object Idle : GeneratorUiState
    data class Generating(
        val progress: Float,
        val message: String,
        val elapsedSeconds: Long = 0
    ) : GeneratorUiState
    data class Success(
        val schedule: Schedule,
        val summary: GeneratorSummary
    ) : GeneratorUiState
    data class Error(val message: String) : GeneratorUiState
}

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GeneratorUiState>(GeneratorUiState.Idle)
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    private var hasExistingSchedule: Boolean = false
    private var timerJob: Job? = null
    private var generatorJob: Job? = null

    init {
        checkExistingSchedule()
    }

    fun checkExistingSchedule(schoolId: String = "default_school") {
        viewModelScope.launch {
            val result = scheduleRepository.getSchedule(schoolId)
            hasExistingSchedule = result.isSuccess && (result.getOrNull()?.slots?.isNotEmpty() == true)
        }
    }

    fun isRegenerate(): Boolean = hasExistingSchedule

    fun startGenerating(schoolId: String = "default_school") {
        generatorJob?.cancel()
        timerJob?.cancel()

        var elapsed = 0L
        _uiState.update { GeneratorUiState.Generating(0.0f, "Memulai algoritma CSP...", 0) }

        // Timer Realtime
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsed++
                val current = _uiState.value
                if (current is GeneratorUiState.Generating) {
                    _uiState.update { current.copy(elapsedSeconds = elapsed) }
                }
            }
        }

        // Jalankan Generator Flow dari Repository
        generatorJob = viewModelScope.launch {
            try {
                scheduleRepository.generateSchedule(schoolId).collect { update ->
                    _uiState.update {
                        GeneratorUiState.Generating(
                            progress = update.progress,
                            message = update.message,
                            elapsedSeconds = elapsed
                        )
                    }

                    val finalResult = update.result
                    if (update.progress >= 1.0f && finalResult != null) {
                        timerJob?.cancel()
                        hasExistingSchedule = true
                        val summary = GeneratorSummary(
                            totalClasses = 12,
                            totalSlots = finalResult.slots.size,
                            conflictCount = 0,
                            durationSeconds = elapsed.coerceAtLeast(3)
                        )
                        _uiState.update {
                            GeneratorUiState.Success(
                                schedule = finalResult,
                                summary = summary
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                timerJob?.cancel()
                _uiState.update {
                    GeneratorUiState.Error(e.message ?: "Gagal menyusun jadwal otomatis.")
                }
            }
        }
    }

    fun resetToIdle() {
        timerJob?.cancel()
        generatorJob?.cancel()
        _uiState.update { GeneratorUiState.Idle }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        generatorJob?.cancel()
    }
}
