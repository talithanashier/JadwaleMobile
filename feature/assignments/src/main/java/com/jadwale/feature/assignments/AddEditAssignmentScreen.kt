package com.jadwale.feature.assignments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jadwale.core.model.ClassRoom
import com.jadwale.core.model.Subject
import com.jadwale.core.model.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssignmentScreen(
    viewModel: AssignmentViewModel,
    assignmentId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState as? AssignmentUiState.Success
    val existing = state?.assignments?.firstOrNull { it.id == assignmentId }

    val teachers = state?.teachers ?: emptyList()
    val subjects = state?.subjects ?: emptyList()
    val classes = state?.classes ?: emptyList()

    var selectedTeacher by remember { mutableStateOf(teachers.firstOrNull { it.id == existing?.teacherId } ?: teachers.firstOrNull()) }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull { it.id == existing?.subjectId } ?: subjects.firstOrNull()) }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull { it.id == existing?.classId } ?: classes.firstOrNull()) }
    var totalJp by remember { mutableStateOf((existing?.totalJp ?: 4).toString()) }

    var teacherExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (assignmentId == null) "Tambah Penugasan Guru" else "Edit Penugasan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dropdown Guru
            ExposedDropdownMenuBox(
                expanded = teacherExpanded,
                onExpandedChange = { teacherExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedTeacher?.name ?: "Pilih Guru",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Guru Pengampu *") },
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

            // Dropdown Mapel
            ExposedDropdownMenuBox(
                expanded = subjectExpanded,
                onExpandedChange = { subjectExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.name ?: "Pilih Mata Pelajaran",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Mata Pelajaran *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = subjectExpanded,
                    onDismissRequest = { subjectExpanded = false }
                ) {
                    subjects.forEach { s ->
                        DropdownMenuItem(
                            text = { Text("${s.name} (${s.code})") },
                            onClick = {
                                selectedSubject = s
                                subjectExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown Kelas
            ExposedDropdownMenuBox(
                expanded = classExpanded,
                onExpandedChange = { classExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedClass?.let { "Kelas ${it.name}" } ?: "Pilih Kelas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kelas Tujuan *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = classExpanded,
                    onDismissRequest = { classExpanded = false }
                ) {
                    classes.forEach { c ->
                        DropdownMenuItem(
                            text = { Text("Kelas ${c.name}") },
                            onClick = {
                                selectedClass = c
                                classExpanded = false
                            }
                        )
                    }
                }
            }

            // Input JP
            OutlinedTextField(
                value = totalJp,
                onValueChange = { totalJp = it },
                label = { Text("Total Jam Pelajaran (JP per Minggu)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val t = selectedTeacher ?: return@Button
                    val s = selectedSubject ?: return@Button
                    val c = selectedClass ?: return@Button
                    viewModel.saveAssignment(
                        id = assignmentId,
                        teacher = t,
                        subject = s,
                        classRoom = c,
                        totalJp = totalJp.toIntOrNull() ?: 4,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (assignmentId == null) "Tetapkan Penugasan" else "Perbarui Penugasan", fontWeight = FontWeight.Bold)
            }
        }
    }
}
