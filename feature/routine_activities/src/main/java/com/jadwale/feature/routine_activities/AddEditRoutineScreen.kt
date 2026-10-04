package com.jadwale.feature.routine_activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jadwale.core.model.DayOfWeek
import com.jadwale.core.model.RoutineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRoutineScreen(
    viewModel: RoutineViewModel,
    routineId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val existing = (uiState as? RoutineUiState.Success)?.routines?.firstOrNull { it.id == routineId }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var selectedType by remember { mutableStateOf(existing?.type ?: RoutineType.PEMBIASAAN) }
    var selectedDay by remember { mutableStateOf(existing?.day ?: DayOfWeek.SENIN) }
    var startTime by remember { mutableStateOf(existing?.startTime ?: "07:00") }
    var endTime by remember { mutableStateOf(existing?.endTime ?: "07:15") }

    var typeExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (routineId == null) "Tambah Kegiatan Rutin" else "Edit Kegiatan Rutin", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Kegiatan Rutin *") },
                placeholder = { Text("Contoh: Upacara Bendera, Pembiasaan Pagi") },
                singleLine = true,
                isError = hasError && name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown Tipe
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jenis / Tipe Kegiatan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    RoutineType.values().forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.displayName) },
                            onClick = {
                                selectedType = t
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown Hari
            ExposedDropdownMenuBox(
                expanded = dayExpanded,
                onExpandedChange = { dayExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedDay.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hari Pelaksanaan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dayExpanded,
                    onDismissRequest = { dayExpanded = false }
                ) {
                    DayOfWeek.values().forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d.displayName) },
                            onClick = {
                                selectedDay = d
                                dayExpanded = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Jam Mulai") },
                    placeholder = { Text("07:00") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Jam Selesai") },
                    placeholder = { Text("07:45") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        hasError = true
                        return@Button
                    }
                    viewModel.saveRoutine(
                        id = routineId,
                        name = name,
                        type = selectedType,
                        day = selectedDay,
                        startTime = startTime,
                        endTime = endTime,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (routineId == null) "Simpan Kegiatan Rutin" else "Perbarui Kegiatan Rutin", fontWeight = FontWeight.Bold)
            }
        }
    }
}
