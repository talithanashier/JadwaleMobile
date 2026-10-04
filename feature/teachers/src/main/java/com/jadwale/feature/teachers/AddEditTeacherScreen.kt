package com.jadwale.feature.teachers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTeacherScreen(
    viewModel: TeacherViewModel,
    teacherId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingTeacher = (uiState as? TeacherUiState.Success)?.teachers?.firstOrNull { it.id == teacherId }

    var name by remember { mutableStateOf(existingTeacher?.name ?: "") }
    var nip by remember { mutableStateOf(existingTeacher?.nip ?: "") }
    var email by remember { mutableStateOf(existingTeacher?.email ?: "") }
    var phone by remember { mutableStateOf(existingTeacher?.phone ?: "") }
    var selectedTeacherType by remember { mutableStateOf(existingTeacher?.teacherType ?: com.jadwale.core.model.TeacherType.KEDUANYA) }
    var selectedTeacherStatus by remember { mutableStateOf(existingTeacher?.teacherStatus ?: com.jadwale.core.model.TeacherStatus.PNS) }
    var availabilityNote by remember { mutableStateOf(existingTeacher?.availabilityNote ?: "Senin - Jumat Bersedia Penuh") }
    var selectedSubject by remember { mutableStateOf(existingTeacher?.subjects?.firstOrNull() ?: "Matematika") }
    var maxJp by remember { mutableIntStateOf(existingTeacher?.totalJp ?: 24) }

    val defaultDays = existingTeacher?.availableDays ?: listOf(
        com.jadwale.core.model.DayOfWeek.SENIN,
        com.jadwale.core.model.DayOfWeek.SELASA,
        com.jadwale.core.model.DayOfWeek.RABU,
        com.jadwale.core.model.DayOfWeek.KAMIS,
        com.jadwale.core.model.DayOfWeek.JUMAT
    )

    var mondayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.SENIN)) }
    var tuesdayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.SELASA)) }
    var wednesdayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.RABU)) }
    var thursdayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.KAMIS)) }
    var fridayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.JUMAT)) }
    var saturdayAvailable by remember { mutableStateOf(defaultDays.contains(com.jadwale.core.model.DayOfWeek.SABTU)) }

    var hasError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val commonSubjects = listOf("Matematika", "IPAS", "B. Indonesia", "PAI & BP", "PJOK", "Seni Rupa")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onNavigateBack)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color(0xFF1D68E4)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Kembali",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1D68E4)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Data Pokok",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Description Card
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1D68E4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (teacherId == null) "Tambah Data Guru" else "Edit Data Guru",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Formulir pendaftaran tenaga pendidik dan jadwal ketersediaan tatap muka SDN Pancasila.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                }
            }

            // Section 1: Identitas Pokok Guru
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF1D68E4))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Identitas Pokok Guru",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Column {
                        Text(
                            text = "Nama Lengkap & Gelar *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tuliskan nama lengkap beserta gelar akademik guru yang bersangkutan.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("cth. Bambang Sutrisno, M.Pd", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            isError = hasError && name.isBlank(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NIP atau NUPTK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Text(
                                text = "Opsional",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bisa dikosongkan apabila guru berstatus honorer atau belum memiliki NUPTK resmi.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = nip,
                            onValueChange = { nip = it },
                            placeholder = { Text("18 digit NIP / 16 digit NUPTK", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Email Akun Pendidik (Kemdikbud) *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Text(
                                text = "@guru.sd.belajar.id",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Email akun belajar.id resmi untuk akses login guru ke aplikasi Jadwale.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("cth. nama.guru@guru.sd.belajar.id", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Jenis Guru: Mapel / Wali Kelas / Keduanya
                    Column {
                        Text(
                            text = "Jenis Penugasan Guru *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilih apakah guru mengampu mata pelajaran tertentu, wali kelas rombel, atau keduanya.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.jadwale.core.model.TeacherType.values().forEach { type ->
                                val isSelected = selectedTeacherType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTeacherType = type },
                                    label = { Text(type.displayName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFEFF6FF),
                                        selectedLabelColor = Color(0xFF1D68E4)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) Color(0xFF1D68E4) else Color(0xFFCBD5E1),
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }

                    // Status Kepegawaian: PNS / PPPK / Honorer
                    Column {
                        Text(
                            text = "Status Kepegawaian Guru *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status kepegawaian resmi untuk pelaporan Dapodik dan sertifikasi.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.jadwale.core.model.TeacherStatus.values().forEach { status ->
                                val isSelected = selectedTeacherStatus == status
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTeacherStatus = status },
                                    label = { Text(status.displayName.split(" ").first(), fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDCFCE7),
                                        selectedLabelColor = Color(0xFF15803D)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) Color(0xFF15803D) else Color(0xFFCBD5E1),
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }

                    // Ketersediaan Guru / Hari Berhalangan
                    Column {
                        Text(
                            text = "Ketersediaan & Catatan Berhalangan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tuliskan hari atau jam berhalangan (misal: 'Jumat hanya sampai jam 10.00', 'Sabtu libur PPG').",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = availabilityNote,
                            onValueChange = { availabilityNote = it },
                            placeholder = { Text("cth. Bersedia penuh Senin-Jumat, Rabu jam ke-5 ada diklat", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Text(
                            text = "Nomor WhatsApp Guru *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text("+62 812-3456-7890", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Section 2: Matriks Hari Mengajar
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF15803D))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Matriks Hari Mengajar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    // Info box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Panduan Penjadwalan Adil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                                Text(
                                    text = "Pilih hari dan jam di mana guru ini bersedia mengajar di SDN Pancasila agar sistem tidak menaruh jam di luar kesepakatan.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    DayAvailabilityRow(
                        day = "Senin",
                        timeRange = "07:35 - 12:45",
                        isChecked = mondayAvailable,
                        onCheckedChange = { mondayAvailable = it }
                    )
                    DayAvailabilityRow(
                        day = "Selasa",
                        timeRange = "07:35 - 12:45",
                        isChecked = tuesdayAvailable,
                        onCheckedChange = { tuesdayAvailable = it }
                    )
                    DayAvailabilityRow(
                        day = "Rabu",
                        timeRange = "07:35 - 12:45",
                        isChecked = wednesdayAvailable,
                        onCheckedChange = { wednesdayAvailable = it }
                    )
                    DayAvailabilityRow(
                        day = "Kamis",
                        timeRange = "07:35 - 12:45",
                        isChecked = thursdayAvailable,
                        onCheckedChange = { thursdayAvailable = it }
                    )
                    DayAvailabilityRow(
                        day = "Jumat",
                        timeRange = "Libur / Instansi Luar",
                        isChecked = fridayAvailable,
                        onCheckedChange = { fridayAvailable = it }
                    )
                }
            }

            // Section 3: Penugasan Awal
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFEA580C))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Penugasan Awal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Column {
                        Text(
                            text = "Mata Pelajaran Utama Yang Diampu *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sentuh salah satu mapel utama di bawah ini:",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Grid of subject pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            commonSubjects.take(3).forEach { subj ->
                                val isSelected = selectedSubject == subj
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF1D68E4) else Color(0xFFF1F5F9))
                                        .clickable { selectedSubject = subj }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subj,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            commonSubjects.drop(3).forEach { subj ->
                                val isSelected = selectedSubject == subj
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF1D68E4) else Color(0xFFF1F5F9))
                                        .clickable { selectedSubject = subj }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subj,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }

                    // Stepper: Batas Maksimum JP
                    Column {
                        Text(
                            text = "Batas Maksimum Jam Pelajaran (JP) / Minggu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sistem akan menghentikan alokasi jadwal jika total jam guru telah mencapai batas ini.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (maxJp > 2) maxJp -= 2 },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$maxJp JP",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D68E4)
                                )
                                Text(
                                    text = "setara ±${maxJp / 3} sesi per minggu",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            IconButton(
                                onClick = { if (maxJp < 36) maxJp += 2 },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1D68E4))
                            ) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        hasError = true
                        return@Button
                    }
                    val selectedDays = buildList {
                        if (mondayAvailable) add(com.jadwale.core.model.DayOfWeek.SENIN)
                        if (tuesdayAvailable) add(com.jadwale.core.model.DayOfWeek.SELASA)
                        if (wednesdayAvailable) add(com.jadwale.core.model.DayOfWeek.RABU)
                        if (thursdayAvailable) add(com.jadwale.core.model.DayOfWeek.KAMIS)
                        if (fridayAvailable) add(com.jadwale.core.model.DayOfWeek.JUMAT)
                        if (saturdayAvailable) add(com.jadwale.core.model.DayOfWeek.SABTU)
                    }
                    val autoNote = if (selectedDays.size == 6) {
                        "Senin - Sabtu Bersedia Penuh"
                    } else if (selectedDays == listOf(com.jadwale.core.model.DayOfWeek.SENIN, com.jadwale.core.model.DayOfWeek.SELASA, com.jadwale.core.model.DayOfWeek.RABU, com.jadwale.core.model.DayOfWeek.KAMIS, com.jadwale.core.model.DayOfWeek.JUMAT)) {
                        "Senin - Jumat Bersedia Penuh"
                    } else if (selectedDays.isEmpty()) {
                        "Tidak ada hari mengajar"
                    } else {
                        "Bersedia hari: " + selectedDays.joinToString(", ") { it.displayName }
                    }

                    viewModel.saveTeacher(
                        id = teacherId,
                        name = name,
                        nip = nip.ifBlank { "-" },
                        email = if (email.isNotBlank()) email else "${name.lowercase().replace(" ", ".")}@guru.sd.belajar.id",
                        phone = phone,
                        subjects = listOf(selectedSubject),
                        totalJp = maxJp,
                        teacherType = selectedTeacherType,
                        teacherStatus = selectedTeacherStatus,
                        availabilityNote = autoNote,
                        availableDays = selectedDays,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Data Guru & Jadwal Hadir", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Delete Button (when editing existing teacher)
            if (teacherId != null) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Data Guru Ini", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFDC2626))
                }
            }

            // Help link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = Color(0xFF1D68E4),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Butuh bantuan? Panduan pengisian guru honorer",
                    fontSize = 12.sp,
                    color = Color(0xFF1D68E4),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showDeleteDialog && teacherId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp)) },
            title = { Text("Hapus Data Guru?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Apakah Anda yakin ingin menghapus data guru ${name.ifBlank { "ini" }}? Tindakan ini tidak dapat dibatalkan.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTeacher(teacherId)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
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

@Composable
private fun DayAvailabilityRow(
    day: String,
    timeRange: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isChecked) Color(0xFFF8FAFC) else Color(0xFFF1F5F9))
            .border(
                1.dp,
                if (isChecked) Color(0xFFE2E8F0) else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable { onCheckedChange(!isChecked) }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1D68E4))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = day,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = if (isChecked) "Bisa Mengajar" else "Tidak Hadir (Sekolah Lain)",
                    fontSize = 11.sp,
                    color = if (isChecked) Color(0xFF15803D) else Color(0xFFDC2626)
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = timeRange,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )
        }
    }
}
