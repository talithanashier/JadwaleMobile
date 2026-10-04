package com.jadwale.feature.schedule_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadwale.core.model.ScheduleSlot
import com.jadwale.core.model.Subject
import com.jadwale.core.model.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditScreen(
    viewModel: ScheduleEditViewModel,
    initialClassId: String = "",
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(initialClassId) {
        viewModel.initialize(initialClassId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect((uiState as? ScheduleEditUiState.Success)?.saveSuccessMessage) {
        val msg = (uiState as? ScheduleEditUiState.Success)?.saveSuccessMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Manual Jadwal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ScheduleEditUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ScheduleEditUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ScheduleEditUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Dropdown / Row Pemilihan Kelas
                        Text(
                            text = "Pilih Kelas yang Ingin Diedit:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ScrollableTabRow(
                            selectedTabIndex = state.classes.indexOfFirst { it.id == state.selectedClassId }.coerceAtLeast(0),
                            edgePadding = 0.dp,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        ) {
                            state.classes.forEach { cls ->
                                val isSelected = cls.id == state.selectedClassId
                                Tab(
                                    selected = isSelected,
                                    onClick = { viewModel.selectClass(cls.id) },
                                    text = {
                                        Text(
                                            text = "Kelas ${cls.name}",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Daftar Slot Pembelajaran (Tap untuk Edit)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hanya slot mata pelajaran reguler yang dapat diubah.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // List Slot Kelas yang dipilih
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.classSlots) { slot ->
                                EditSlotCard(
                                    slot = slot,
                                    onClick = { viewModel.openSlotEditor(slot) }
                                )
                            }
                        }
                    }

                    // Dialog Form Edit Slot
                    if (state.editingSlot != null) {
                        EditSlotDialog(
                            slot = state.editingSlot,
                            teachers = state.teachers,
                            subjects = state.subjects,
                            conflictError = state.conflictError,
                            isSaving = state.isSaving,
                            onDismiss = { viewModel.closeSlotEditor() },
                            onSave = { subject, teacher, room ->
                                viewModel.saveSlotEdit(subject, teacher, room)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSlotCard(
    slot: ScheduleSlot,
    onClick: () -> Unit
) {
    val isRoutine = slot.isRoutine
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRoutine, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRoutine) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isRoutine) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = slot.day.displayName,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${slot.timeSlot.startTime} - ${slot.timeSlot.endTime}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isRoutine) slot.routineActivity?.name ?: "Rutin" else slot.subject?.name ?: "-",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (!isRoutine) {
                    Text(
                        text = "Guru: ${slot.teacher?.name ?: "-"}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isRoutine) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Slot",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSlotDialog(
    slot: ScheduleSlot,
    teachers: List<Teacher>,
    subjects: List<Subject>,
    conflictError: String?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Subject, Teacher, String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(slot.subject ?: subjects.first()) }
    var selectedTeacher by remember { mutableStateOf(slot.teacher ?: teachers.first()) }
    var room by remember { mutableStateOf(slot.room) }

    var subjectExpanded by remember { mutableStateOf(false) }
    var teacherExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Ubah Slot (${slot.day.displayName} JP ${slot.timeSlot.jpNumber})", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!conflictError.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = conflictError, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                // Dropdown Mapel
                Text("Mata Pelajaran", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text(subj.name) },
                                onClick = {
                                    selectedSubject = subj
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Guru
                Text("Guru Pengampu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = teacherExpanded,
                    onExpandedChange = { teacherExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTeacher.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teacherExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = teacherExpanded,
                        onDismissRequest = { teacherExpanded = false }
                    ) {
                        teachers.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = {
                                    selectedTeacher = t
                                    teacherExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input Ruangan
                Text("Ruang Kelas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedSubject, selectedTeacher, room) },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Simpan Perubahan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Batal")
            }
        }
    )
}
