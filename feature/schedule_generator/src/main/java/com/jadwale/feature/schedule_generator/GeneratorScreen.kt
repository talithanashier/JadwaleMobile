package com.jadwale.feature.schedule_generator

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel,
    onNavigateToScheduleView: () -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                title = {
                    val isDark = isSystemInDarkTheme()
                    val logoRes = if (isDark) R.drawable.logo_jadwale_light else R.drawable.logo_jadwale_dark
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = "Logo Jadwale",
                            modifier = Modifier.height(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Jadwale",
                                    fontSize = 17.sp,
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
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                            Text(
                                text = "Generator Jadwal Otomatis",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifDialog = true }) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color(0xFF334155)
                            )
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
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Pengguna",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
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
            when (val state = uiState) {
                is GeneratorUiState.Idle -> {
                    // Header Banner: Mesin Otomasi Aktif
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "MESIN OTOMASI AKTIF",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Terverifikasi Kemdikbud",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Penyusunan Jadwal Otomatis",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Solusi Berbasis Algoritma CSP & Heuristik MRV v2.4",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Stepper Card: Tahapan Pengerjaan Sistem
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tahapan Pengerjaan Sistem",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Siap Eksekusi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D68E4)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StepIndicatorItem(
                                    stepNum = "1",
                                    title = "Validasi",
                                    subtitle = "Data Lengkap",
                                    isDone = true,
                                    isActive = false
                                )
                                StepIndicatorItem(
                                    stepNum = "2",
                                    title = "Proses AI",
                                    subtitle = "Bebas Bentrok",
                                    isDone = false,
                                    isActive = true
                                )
                                StepIndicatorItem(
                                    stepNum = "3",
                                    title = "Hasil Siap",
                                    subtitle = "Tinjau Opsi",
                                    isDone = false,
                                    isActive = false
                                )
                            }
                        }
                    }

                    // Checklist Card: Persiapan Sistem 4/4 Lengkap
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Persiapan Sistem",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "4/4 Lengkap",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            ChecklistRow(title = "Guru & Ketersediaan Jam", detail = "24 Guru Terdaftar")
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                            ChecklistRow(title = "Rombel & Ruang Belajar", detail = "12 Rombel Kelas 1A s/d 6B")
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                            ChecklistRow(title = "Beban Kurikulum Merdeka", detail = "480 Jam Pelajaran/Minggu")
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                            ChecklistRow(title = "Aturan Rutin & Khusus", detail = "Upacara & Istirahat Terkunci")
                        }
                    }

                    // Card Heuristik & Kapasitas
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color(0xFF1D68E4),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Konfigurasi Algoritma CSP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Minimum Remaining Values (MRV) + Forward Checking\n• Distribusi Guru Maksimal 4 JP/Hari per kelas\n• Pemisahan Mapel Konsentrasi Tinggi (MTK & IPA) di pagi hari",
                                fontSize = 11.sp,
                                color = Color(0xFF1E3A8A),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Action Button: Mulai Generate
                    val isRegenerate = viewModel.isRegenerate()
                    Button(
                        onClick = {
                            if (isRegenerate) {
                                showConfirmDialog = true
                            } else {
                                viewModel.startGenerating()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isRegenerate) "Regenerate Jadwal Sekarang" else "Mulai Generate Jadwal Sekarang",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                is GeneratorUiState.Generating -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier.size(76.dp),
                                strokeWidth = 6.dp,
                                color = Color(0xFF1D68E4),
                                trackColor = Color(0xFFE2E8F0)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "${(state.progress * 100).toInt()}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D68E4)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            val minutes = state.elapsedSeconds / 60
                            val seconds = state.elapsedSeconds % 60
                            Text(
                                text = String.format("Waktu Komputasi: %02d:%02d", minutes, seconds),
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                is GeneratorUiState.Success -> {
                    // Success Result matching Proses & Hasil Generator AI mockup
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "HASIL SEMPURNA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                                Text(
                                    text = "Jadwal Berhasil Disusun!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "100% Bebas Bentrok Jam & Ruang",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }

                    // Computation Time Banner
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF1D68E4),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Waktu Komputasi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                            Text(
                                text = "${state.summary.durationSeconds} Detik (MRV Heuristik)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D68E4)
                            )
                        }
                    }

                    // 2x2 Bento Stat Cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ResultStatCard(
                            label = "Rombongan Belajar",
                            value = "${state.summary.totalClasses} Rombel",
                            subtext = "100% Terpetakan",
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatCard(
                            label = "Guru Pendidik",
                            value = "24 Guru",
                            subtext = "Beban Kerja Adil",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ResultStatCard(
                            label = "Jam Pelajaran (JP)",
                            value = "${state.summary.totalSlots} JP",
                            subtext = "Terdistribusi Penuh",
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatCard(
                            label = "Tingkat Bentrok",
                            value = "${state.summary.conflictCount} Bentrok",
                            subtext = "Hard Constraint Aman",
                            valueColor = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Aturan Khusus Terakomodasi Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Aturan Khusus Terakomodasi:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            RuleRow(text = "Upacara Bendera Senin terkunci pada JP 1.")
                            Spacer(modifier = Modifier.height(6.dp))
                            RuleRow(text = "Mapel konsentrasi tinggi (MTK & IPA) di pagi hari sebelum istirahat.")
                        }
                    }

                    // Actions
                    Button(
                        onClick = onNavigateToScheduleView,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Terapkan & Lihat Jadwal Lengkap", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onNavigateToScheduleView,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Ekspor Langsung ke PDF / Cetak", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(
                        onClick = { viewModel.resetToIdle() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Kalkulasi Ulang (Regenerate Opsi Alternatif)", fontSize = 12.sp)
                    }
                }

                is GeneratorUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.resetToIdle() },
                        title = { Text("Penyusunan Jadwal Gagal") },
                        text = { Text(state.message) },
                        confirmButton = {
                            Button(onClick = { viewModel.startGenerating() }) {
                                Text("Coba Lagi")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.resetToIdle() }) {
                                Text("Tutup")
                            }
                        }
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEA580C)) },
            title = { Text("Regenerate Jadwal?") },
            text = { Text("Jadwal pelajaran yang sudah ada sebelumnya akan ditimpa dengan jadwal baru dari algoritma AI. Lanjutkan?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.startGenerating()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ya, Susun Ulang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
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
                            Text("Algoritma AI CSP Solver V2.4 siap memproses 480 JP bebas bentrok.", fontSize = 11.sp, color = Color(0xFF166534))
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
                            Text("Sesuai Kurikulum Merdeka Fase A, B, dan C (100% Terpenuhi).", fontSize = 11.sp, color = Color(0xFF1E3A8A))
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
private fun StepIndicatorItem(
    stepNum: String,
    title: String,
    subtitle: String,
    isDone: Boolean,
    isActive: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isDone) Color(0xFF15803D)
                    else if (isActive) Color(0xFF1D68E4)
                    else Color(0xFFE2E8F0)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = stepNum,
                    color = if (isActive) Color.White else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text(text = subtitle, fontSize = 9.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun ChecklistRow(title: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF15803D),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
        }
        Text(text = detail, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun ResultStatCard(
    label: String,
    value: String,
    subtext: String,
    valueColor: Color = Color(0xFF0F172A),
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtext, fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun RuleRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF15803D),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 15.sp)
    }
}
