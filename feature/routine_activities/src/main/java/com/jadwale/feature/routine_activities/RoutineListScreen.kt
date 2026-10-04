package com.jadwale.feature.routine_activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.jadwale.core.model.RoutineActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineListScreen(
    viewModel: RoutineViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (routineId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRoutines()
    }

    var isUpacaraActive by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.isUpacaraActive) }
    var isPembiasaanActive by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.isPembiasaanActive) }
    var isSenamActive by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.isSenamActive) }
    var istirahat1Duration by remember { mutableIntStateOf(com.jadwale.core.model.SchoolConfig.break1DurationMinutes) }
    var istirahat2Duration by remember { mutableIntStateOf(com.jadwale.core.model.SchoolConfig.break2DurationMinutes) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val isDark = isSystemInDarkTheme()
                    val logoRes = if (isDark) R.drawable.logo_jadwale_light else R.drawable.logo_jadwale_dark
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = "Logo Jadwale",
                            modifier = Modifier.height(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Jadwale",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "KEMDIKBUD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                            Text(
                                text = "Kegiatan Rutin & Waktu Istirahat",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifDialog = true }) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = Color(0xFF334155))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D68E4))
                            .clickable { showProfileDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profil Pengguna", tint = Color.White, modifier = Modifier.size(18.dp))
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Breadcrumb & Title
            Column {
                Text(
                    text = "Konfigurasi > Kegiatan Rutin & Istirahat",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kegiatan Rutin & Istirahat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Aturan baku Upacara, Pembiasaan, dan Waktu Istirahat Siswa",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Info Box: Terkunci Secara Sistem
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF1D68E4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Terkunci Secara Sistem",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Waktu istirahat dan pembiasaan dikunci otomatis pada slot jadwal sehingga tidak akan tertabrak oleh jam pelajaran guru manapun.",
                            fontSize = 11.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Section 1: Kegiatan Pembiasaan & Upacara
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kegiatan Pembiasaan & Upacara",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "3 Terjadwal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }
            }

            // Card Upacara
            RoutineSwitchCard(
                badge = "WAJIB KEMDIKBUD",
                badgeBg = Color(0xFFEFF6FF),
                badgeText = Color(0xFF1D4ED8),
                title = "Upacara Bendera Hari Senin",
                details = "Senin, 07:00 - 07:35 (35 Menit / 1 JP)\nLapangan Utama Sekolah",
                isChecked = isUpacaraActive,
                onCheckedChange = { isUpacaraActive = it }
            )

            // Card Pembiasaan
            RoutineSwitchCard(
                badge = "RUTINITAS PAGI",
                badgeBg = Color(0xFFDCFCE7),
                badgeText = Color(0xFF15803D),
                title = "Pembiasaan Pagi / Literasi / Sholat",
                details = "Pembiasaan literasi, numerasi & doa bersama di kelas masing-masing.\nSelasa s/d Kamis • 07:00 - 07:15 (Durasi 15 Menit)",
                isChecked = isPembiasaanActive,
                onCheckedChange = { isPembiasaanActive = it }
            )

            // Card Senam
            RoutineSwitchCard(
                badge = "JUMAT SEHAT",
                badgeBg = Color(0xFFFFEDD5),
                badgeText = Color(0xFFC2410C),
                title = "Senam Kebugaran Jasmani",
                details = "Jumat, 07:00 - 07:35 (Durasi 35 Menit)",
                isChecked = isSenamActive,
                onCheckedChange = { isSenamActive = it }
            )

            // Section 2: Pengaturan Waktu Istirahat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengaturan Waktu Istirahat",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "2 Sesi Terjadwal",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Istirahat 1 Card
            BreakSettingCard(
                title = "Istirahat 1 (Pagi)",
                slotText = "Setelah JP ke-3 (Pukul 08:45 - 09:00)",
                infoText = "Pemisah mata pelajaran bernalar tinggi (Matematika & B. Indonesia)",
                durationMinutes = istirahat1Duration,
                onDurationChange = { istirahat1Duration = it }
            )

            // Istirahat 2 Card
            BreakSettingCard(
                title = "Istirahat 2 (Siang)",
                slotText = "Setelah JP ke-5 (Pukul 11:20 - 11:35)",
                infoText = "Persiapan makan siang & sholat dzuhur berjamaah",
                durationMinutes = istirahat2Duration,
                onDurationChange = { istirahat2Duration = it }
            )

            // Section 3: Daftar Kegiatan Rutin & Khusus (CRUD)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Kegiatan Rutin & Khusus",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                val routinesCount = (uiState as? RoutineUiState.Success)?.routines?.size ?: 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$routinesCount Kegiatan",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }

            when (val state = uiState) {
                is RoutineUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color(0xFF1D68E4))
                    }
                }
                is RoutineUiState.Error -> {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(state.message, fontSize = 11.sp, color = Color(0xFFB91C1C))
                        }
                    }
                }
                is RoutineUiState.Success -> {
                    if (state.routines.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.EventNote, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Belum Ada Kegiatan Rutin Tambahan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                Text("Gunakan tombol di bawah untuk menambah jadwal pembiasaan atau istirahat khusus.", fontSize = 10.sp, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    } else {
                        state.routines.forEach { routine ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val badgeColor = when (routine.type) {
                                                com.jadwale.core.model.RoutineType.UPACARA -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
                                                com.jadwale.core.model.RoutineType.ISTIRAHAT -> Color(0xFFFEF3C7) to Color(0xFFB45309)
                                                else -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(badgeColor.first)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = routine.type.displayName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor.second)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = routine.day.displayName, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = routine.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Text(text = "Pukul ${routine.startTime} - ${routine.endTime}", fontSize = 11.sp, color = Color(0xFF475569))
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { onNavigateToAddEdit(routine.id) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Kegiatan", tint = Color(0xFF1D68E4), modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteRoutine(routine.id) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus Kegiatan", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Actions
            OutlinedButton(
                onClick = { onNavigateToAddEdit(null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Tambah Kegiatan Khusus Baru", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    com.jadwale.core.model.SchoolConfig.isUpacaraActive = isUpacaraActive
                    com.jadwale.core.model.SchoolConfig.isPembiasaanActive = isPembiasaanActive
                    com.jadwale.core.model.SchoolConfig.isSenamActive = isSenamActive
                    com.jadwale.core.model.SchoolConfig.break1DurationMinutes = istirahat1Duration
                    com.jadwale.core.model.SchoolConfig.break2DurationMinutes = istirahat2Duration
                    android.widget.Toast.makeText(
                        context,
                        "✓ Aturan kegiatan rutin & waktu istirahat berhasil diterapkan ke jadwal!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    onNavigateBack()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Terapkan Aturan ke Jadwal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showNotifDialog) {
        AlertDialog(
            onDismissRequest = { showNotifDialog = false },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF1D68E4)) },
            title = { Text("Notifikasi Sistem Jadwale", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("✓ Jadwal Siap Digunakan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF15803D))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Semester Ganjil 2025/2026 SDN Pancasila 01 telah 100% teralokasi bebas bentrok (480 JP).", fontSize = 11.sp, color = Color(0xFF166534))
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🏛️ Validasi Kemdikbudristek", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1D4ED8))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Aturan Kegiatan Rutin & Istirahat telah terkunci secara sistem.", fontSize = 11.sp, color = Color(0xFF1E3A8A))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotifDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Mengerti") }
            }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Operator SDN Pancasila 01", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                    Text("operator@sdnpancasila01.sch.id", fontSize = 12.sp, color = Color(0xFF64748B))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Admin Sekolah • Terverifikasi Dapodik", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("NPSN: 1234567890 • Tahun Ajaran 2025/2026 Ganjil", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Tutup") }
            }
        )
    }
}

@Composable
private fun RoutineSwitchCard(
    badge: String,
    badgeBg: Color,
    badgeText: Color,
    title: String,
    details: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeText)
                }
                Switch(checked = isChecked, onCheckedChange = onCheckedChange)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(8.dp)
            ) {
                Text(text = details, fontSize = 10.sp, color = Color(0xFF64748B), lineHeight = 14.sp)
            }
        }
    }
}

@Composable
private fun BreakSettingCard(
    title: String,
    slotText: String,
    infoText: String,
    durationMinutes: Int,
    onDurationChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF15803D)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "Wajib", fontSize = 9.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                }
            }

            Column {
                Text(text = "Penempatan Slot:", fontSize = 10.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = slotText, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Alokasi Durasi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Text(text = "Standar nasional 15-20 mnt", fontSize = 10.sp, color = Color(0xFF64748B))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { if (durationMinutes > 5) onDurationChange(durationMinutes - 5) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }
                    Text(
                        text = "$durationMinutes Menit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D68E4),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { if (durationMinutes < 45) onDurationChange(durationMinutes + 5) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = infoText, fontSize = 10.sp, color = Color(0xFF1E40AF))
                }
            }
        }
    }
}
