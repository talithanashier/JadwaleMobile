package com.jadwale.feature.subjects

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.jadwale.core.model.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(
    viewModel: SubjectViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (subjectId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedFilter by remember { mutableStateOf("Semua") }
    var showPedomanDialog by remember { mutableStateOf(false) }
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
                                text = "Mata Pelajaran",
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
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Apply to generator button
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "✓ Pengaturan prioritas mapel disimpan & diterapkan ke Generator AI!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan & Terapkan ke Generator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateBack,
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF64748B),
                            unselectedIconColor = Color(0xFF64748B)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateBack,
                        icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Jadwal") },
                        label = { Text("Jadwal", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            unselectedTextColor = Color(0xFF64748B),
                            unselectedIconColor = Color(0xFF64748B)
                        )
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                        label = { Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedTextColor = Color(0xFF1D68E4),
                            selectedIconColor = Color(0xFF1D68E4)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is SubjectUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1D68E4))
                    }
                }
                is SubjectUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is SubjectUiState.Success -> {
                    val allSubjects = state.subjects
                    val filteredSubjects = when (selectedFilter) {
                        "Prioritas Pagi" -> allSubjects.filter { it.isPriority }
                        "Mapel Umum" -> allSubjects.filter { !it.isPriority }
                        else -> allSubjects
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Header title and book icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Mata Pelajaran",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Kurikulum Merdeka • ${allSubjects.size} Mapel Baku Kemendikbud",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF))
                                        .clickable { showPedomanDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = "Panduan",
                                        tint = Color(0xFF1D68E4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Pedoman Cerdas Card
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1D68E4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Pedoman Penjadwalan Cerdas",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Mata Pelajaran Prioritas (Matematika, B. Indonesia, IPA) otomatis ditempatkan pada jam pagi sebelum istirahat pertama sesuai kaidah pedagogik.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF1E40AF),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Tambah Mapel Button
                        item {
                            Button(
                                onClick = { onNavigateToAddEdit(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDBEAFE)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tambah Mapel Baru",
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Filter Chips
                        item {
                            val priorityCount = allSubjects.count { it.isPriority }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFilter == "Semua",
                                    onClick = { selectedFilter = "Semua" },
                                    label = { Text("Semua ${allSubjects.size}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Prioritas Pagi",
                                    onClick = { selectedFilter = "Prioritas Pagi" },
                                    label = { Text("Prioritas Pagi $priorityCount", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Mapel Umum",
                                    onClick = { selectedFilter = "Mapel Umum" },
                                    label = { Text("Mapel Umum", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // List of Subjects
                        items(filteredSubjects, key = { it.id }) { subject ->
                            SubjectModernCard(
                                subject = subject,
                                onTogglePriority = { isChecked ->
                                    viewModel.toggleSubjectPriority(subject.id, isChecked)
                                },
                                onClick = { onNavigateToAddEdit(subject.id) },
                                onDelete = { viewModel.deleteSubject(subject.id) }
                            )
                        }

                        // Pedagogik Info Footer Card
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = Color(0xFF1D68E4),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Pagi Hari (07.15 - 09.35)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "Optimal untuk mata pelajaran numerasi & literasi",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE2E8F0))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "Kemdikbud",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showPedomanDialog) {
        AlertDialog(
            onDismissRequest = { showPedomanDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF1D68E4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pedoman Alokasi JP SD", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Berdasarkan Kurikulum Merdeka (Kemendikbudristek):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text("• Jam Pelajaran (JP) SD berdurasi 35 menit per sesi.", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Mata pelajaran kognitif tinggi (Matematika & Bahasa Indonesia) disarankan ditempatkan sebelum Istirahat 1.", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Alokasi mingguan berkisar antara 30 JP (Fase A) hingga 38 JP (Fase C).", fontSize = 12.sp, color = Color(0xFF475569))
                }
            },
            confirmButton = {
                TextButton(onClick = { showPedomanDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
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
                            Text("Sesuai Kurikulum Merdeka Fase A, B, dan C (9 Mapel Baku).", fontSize = 11.sp, color = Color(0xFF1E3A8A))
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
private fun SubjectModernCard(
    subject: Subject,
    onTogglePriority: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (badgeBg, badgeText) = when {
        subject.code.contains("MTK", ignoreCase = true) || subject.name.contains("Matematika", ignoreCase = true) ->
            Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        subject.code.contains("BIND", ignoreCase = true) || subject.name.contains("Indonesia", ignoreCase = true) ->
            Color(0xFFDCFCE7) to Color(0xFF15803D)
        subject.code.contains("IPA", ignoreCase = true) ->
            Color(0xFFFEF3C7) to Color(0xFFB45309)
        subject.code.contains("PAI", ignoreCase = true) || subject.name.contains("Agama", ignoreCase = true) ->
            Color(0xFFF3E8FF) to Color(0xFF7E22CE)
        subject.code.contains("PJOK", ignoreCase = true) || subject.name.contains("Jasmani", ignoreCase = true) ->
            Color(0xFFD1FAE5) to Color(0xFF047857)
        subject.code.contains("PPKN", ignoreCase = true) || subject.name.contains("Pancasila", ignoreCase = true) ->
            Color(0xFFFFE4E6) to Color(0xFFBE123C)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    val categoryTag = when {
        subject.isPriority -> "Prioritas Pagi"
        subject.name.contains("Agama", ignoreCase = true) -> "Musholla / Khusus"
        subject.name.contains("Jasmani", ignoreCase = true) || subject.code.contains("PJOK", ignoreCase = true) -> "Lapangan Olahraga"
        else -> "Reguler"
    }

    val (tagBg, tagText) = when (categoryTag) {
        "Prioritas Pagi" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        "Musholla / Khusus" -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
        "Lapangan Olahraga" -> Color(0xFFD1FAE5) to Color(0xFF047857)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Code Badge + Title + Category Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subject.code.ifBlank { subject.name.take(3).uppercase() },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = badgeText
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tagBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = categoryTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tagText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Beban: ${subject.jpPerWeek} JP / Minggu (35 mnt/JP)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Teacher assignment bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF1D68E4),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subject.defaultTeacher.ifBlank {
                            if (subject.isPriority) "Bpk. Bambang Sutrisno, M.Pd & 2 Guru lain"
                            else "Guru Mata Pelajaran / Honorer"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Priority Toggle Switch & Delete Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (subject.isPriority) Color(0xFF16A34A) else Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (subject.isPriority) "Slot Pagi (Sblm Istirahat 1)" else "Prioritas Pagi",
                        fontSize = 12.sp,
                        fontWeight = if (subject.isPriority) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (subject.isPriority) Color(0xFF0F172A) else Color(0xFF64748B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = subject.isPriority,
                        onCheckedChange = onTogglePriority,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF1D68E4)
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
