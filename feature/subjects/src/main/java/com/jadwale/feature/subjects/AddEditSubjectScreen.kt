package com.jadwale.feature.subjects

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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubjectScreen(
    viewModel: SubjectViewModel,
    subjectId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val existing = (uiState as? SubjectUiState.Success)?.subjects?.firstOrNull { it.id == subjectId }

    var code by remember { mutableStateOf(existing?.code ?: "") }
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var jpPerWeek by remember { mutableStateOf((existing?.jpPerWeek ?: 4).toString()) }
    var colorCategory by remember { mutableStateOf(existing?.colorCategory ?: "Matematika") }
    var isPriority by remember { mutableStateOf(existing?.isPriority ?: false) }

    var hasError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (subjectId == null) "Tambah Mata Pelajaran" else "Edit Mapel", fontWeight = FontWeight.Bold) },
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
                label = { Text("Nama Mata Pelajaran *") },
                placeholder = { Text("Contoh: Matematika, IPA, PPKn") },
                singleLine = true,
                isError = hasError && name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Kode Singkatan Mapel *") },
                placeholder = { Text("Contoh: MTK, IPA, PAI") },
                singleLine = true,
                isError = hasError && code.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = jpPerWeek,
                onValueChange = { jpPerWeek = it },
                label = { Text("Alokasi JP per Minggu (Kemendikbud) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = colorCategory,
                onValueChange = { colorCategory = it },
                label = { Text("Kategori Warna") },
                placeholder = { Text("Matematika, IPA, IPS, Bahasa Indonesia, dll.") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mata Pelajaran Prioritas (Pagi Hari)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = androidx.compose.ui.graphics.Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Algoritma CSP akan menempatkan mapel ini pada jam pelajaran awal (JP 1-3) saat daya konsentrasi siswa maksimal.",
                            fontSize = 11.sp,
                            color = androidx.compose.ui.graphics.Color(0xFF64748B),
                            lineHeight = 15.sp
                        )
                    }
                    Switch(
                        checked = isPriority,
                        onCheckedChange = { isPriority = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = androidx.compose.ui.graphics.Color(0xFF1D68E4)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (name.isBlank() || code.isBlank()) {
                        hasError = true
                        return@Button
                    }
                    viewModel.saveSubject(
                        id = subjectId,
                        code = code,
                        name = name,
                        jpPerWeek = jpPerWeek.toIntOrNull() ?: 4,
                        colorCategory = colorCategory,
                        isPriority = isPriority,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (subjectId == null) "Simpan Mata Pelajaran" else "Perbarui Mapel", fontWeight = FontWeight.Bold)
            }
        }
    }
}
