package com.jadwale.feature.classes

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassScreen(
    viewModel: ClassViewModel,
    classId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingClass = (uiState as? ClassUiState.Success)?.classes?.firstOrNull { it.id == classId }

    var name by remember { mutableStateOf(existingClass?.name ?: "") }
    var grade by remember { mutableStateOf((existingClass?.grade ?: 1).toString()) }
    var studentCount by remember { mutableStateOf((existingClass?.studentCount ?: 28).toString()) }
    var homeroomTeacher by remember { mutableStateOf(existingClass?.homeroomTeacher ?: "") }

    var hasError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (classId == null) "Tambah Kelas Baru" else "Edit Kelas ${existingClass?.name}", fontWeight = FontWeight.Bold) },
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
                label = { Text("Nama Kelas *") },
                placeholder = { Text("Contoh: 1A, 2B, dll.") },
                singleLine = true,
                isError = hasError && name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = grade,
                onValueChange = { grade = it },
                label = { Text("Tingkat Kelas (1-6) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = studentCount,
                onValueChange = { studentCount = it },
                label = { Text("Jumlah Siswa") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = homeroomTeacher,
                onValueChange = { homeroomTeacher = it },
                label = { Text("Nama Guru Wali Kelas") },
                placeholder = { Text("Contoh: Budi Santoso, S.Pd.") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        hasError = true
                        return@Button
                    }
                    viewModel.saveClass(
                        id = classId,
                        name = name,
                        grade = grade.toIntOrNull() ?: 1,
                        studentCount = studentCount.toIntOrNull() ?: 28,
                        homeroomTeacher = homeroomTeacher,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (classId == null) "Simpan Kelas" else "Perbarui Kelas", fontWeight = FontWeight.Bold)
            }

            if (classId != null) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Text("Hapus Rombel Kelas Ini", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDeleteDialog && classId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Rombel Kelas?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus kelas $name? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteClass(classId)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
