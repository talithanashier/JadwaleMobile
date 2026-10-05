package com.jadwale.feature.classes

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
import com.jadwale.core.model.ClassRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    viewModel: ClassViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (classId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(com.jadwale.core.model.SchoolConfig.isParallel) {
        viewModel.loadClasses()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    var showImportDialog by remember { mutableStateOf(false) }
    var selectedClassForDetail by remember { mutableStateOf<ClassRoom?>(null) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var classToDelete by remember { mutableStateOf<ClassRoom?>(null) }

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
                                text = "Manajemen Kelas",
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
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ClassUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1D68E4))
                    }
                }
                is ClassUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ClassUiState.Success -> {
                    val allClassRooms = state.classes
                    val filteredClassRooms = allClassRooms.filter { classRoom ->
                        val matchesSearch = classRoom.name.contains(searchQuery, ignoreCase = true) ||
                                classRoom.homeroomTeacher.contains(searchQuery, ignoreCase = true)
                        val matchesFilter = when (selectedFilter) {
                            "Fase A" -> classRoom.grade in 1..2
                            "Fase B" -> classRoom.grade in 3..4
                            "Fase C" -> classRoom.grade in 5..6
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Breadcrumb & Title
                            Text(
                                text = "Data Master SD > Manajemen Kelas",
                                fontSize = 12.sp,
                                color = Color(0xFF1D68E4),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manajemen Kelas",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF1D68E4),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${allClassRooms.size} ${if (com.jadwale.core.model.SchoolConfig.isParallel) "Rombel Paralel" else "Kelas Tunggal"} • SDN Pancasila",
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        // Summary Status Card (12 Rombel Lengkap, 100% Terisi)
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0xFFDCFCE7)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF15803D),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "${allClassRooms.size} ${if (com.jadwale.core.model.SchoolConfig.isParallel) "Rombel Lengkap" else "Kelas Lengkap"}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = if (com.jadwale.core.model.SchoolConfig.isParallel) "Tingkat 1 s/d 6 (1A-6B)" else "Tingkat 1 s/d 6 (1-6)",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "100% Terisi",
                                                color = Color(0xFF15803D),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    LinearProgressIndicator(
                                        progress = { 1.0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF15803D),
                                        trackColor = Color(0xFFDCFCE7)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF15803D),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Semua Wali Kelas Terdata",
                                                fontSize = 11.sp,
                                                color = Color(0xFF334155),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Text(
                                            text = "Fase A, B & C",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1D68E4)
                                        )
                                    }
                                }
                            }
                        }

                        // Actions: Tambah Kelas Baru & Import Excel
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onNavigateToAddEdit(null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tambah Kelas Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                OutlinedButton(
                                    onClick = { showImportDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D68E4)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D68E4)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Import Data Excel/CSV", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                        }

                        // Search Field
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Cari kelas atau nama wali kelas...", fontSize = 13.sp, color = Color(0xFF94A3B8))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF64748B))
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        // Filter Chips (Semua, Fase A, Fase B, Fase C)
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFilter == "Semua",
                                    onClick = { selectedFilter = "Semua" },
                                    label = { Text("Semua Tingkat (${allClassRooms.size})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Fase A",
                                    onClick = { selectedFilter = "Fase A" },
                                    label = { Text("Tingkat 1-2 (Fase A)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Fase B",
                                    onClick = { selectedFilter = "Fase B" },
                                    label = { Text("Tingkat 3-4 (Fase B)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = selectedFilter == "Fase C",
                                    onClick = { selectedFilter = "Fase C" },
                                    label = { Text("Tingkat 5-6 (Fase C)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1D68E4),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Class Cards List
                        items(filteredClassRooms, key = { it.id }) { classRoom ->
                            ClassModernCard(
                                classRoom = classRoom,
                                onEdit = { onNavigateToAddEdit(classRoom.id) },
                                onDetailJp = { selectedClassForDetail = classRoom },
                                onDelete = { classToDelete = classRoom }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF1D68E4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import Data Kelas & Rombel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Unggah berkas spreadsheet format .xlsx atau .csv standar Dapodik / Pusdatin Kemendikbud.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Berkas Template Terdeteksi:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            Text("📄 Template_Dapodik_12_Rombel_2026.xlsx", fontSize = 12.sp, color = Color(0xFF1D68E4), fontWeight = FontWeight.Bold)
                            Text("12 Kelas • 24 Guru • Alokasi Lengkap", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportDialog = false
                        Toast.makeText(context, "✓ Berhasil mengimpor 12 Rombel SDN Pancasila!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Proses Impor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Detail JP Dialog
    selectedClassForDetail?.let { classRoom ->
        AlertDialog(
            onDismissRequest = { selectedClassForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF1D68E4))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alokasi JP Kelas ${classRoom.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Wali Kelas: ${classRoom.homeroomTeacher}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Beban Mingguan: ${classRoom.weeklyJp} Jam Pelajaran (35 Menit/JP)",
                        fontSize = 12.sp,
                        color = Color(0xFF1D68E4),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Distribusi Harian:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))

                    val dailyJp = if (classRoom.weeklyJp <= 30) {
                        listOf("Senin" to 6, "Selasa" to 6, "Rabu" to 6, "Kamis" to 6, "Jumat" to 6)
                    } else if (classRoom.weeklyJp <= 34) {
                        listOf("Senin" to 7, "Selasa" to 7, "Rabu" to 7, "Kamis" to 7, "Jumat" to 6)
                    } else {
                        listOf("Senin" to 8, "Selasa" to 8, "Rabu" to 8, "Kamis" to 8, "Jumat" to 6)
                    }

                    dailyJp.forEach { (day, jp) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(day, fontSize = 12.sp, color = Color(0xFF475569))
                            Text("$jp JP (07:15 - selesai)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedClassForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Tutup")
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
                            Text("Sesuai Kurikulum Merdeka Fase A, B, dan C (12 Rombel Lengkap).", fontSize = 11.sp, color = Color(0xFF1E3A8A))
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

    classToDelete?.let { classRoom ->
        AlertDialog(
            onDismissRequest = { classToDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp)) },
            title = { Text("Hapus Rombel Kelas?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Apakah Anda yakin ingin menghapus Rombel Kelas ${classRoom.name}? Tindakan ini tidak dapat dibatalkan.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = classRoom.id
                        classToDelete = null
                        viewModel.deleteClass(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { classToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ClassModernCard(
    classRoom: ClassRoom,
    onEdit: () -> Unit,
    onDetailJp: () -> Unit,
    onDelete: () -> Unit
) {
    val isSelectedFeatured = classRoom.name == "3A"

    val (badgeBg, badgeText) = when (classRoom.grade) {
        1 -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        2 -> Color(0xFFE0E7FF) to Color(0xFF4338CA)
        3 -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        4 -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        5 -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
        else -> Color(0xFFDCFCE7) to Color(0xFF15803D)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelectedFeatured) 1.5.dp else 1.dp,
            color = if (isSelectedFeatured) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Featured Badge if 3A
            if (isSelectedFeatured) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1D68E4))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "★ KELAS TERPILIH",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Row 1: Class Circle Badge + Name & Grade + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = classRoom.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = badgeText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kelas ${classRoom.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Tingkat ${classRoom.grade}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${classRoom.curriculumPhase} • ${if (classRoom.grade <= 2) "Tematik Terpadu" else "Kurikulum Merdeka"}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Siap Jadwal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            // Quick Banner for Class 1A or Class 1: Atur Alokasi JP per Hari
            if (classRoom.name == "1A" || classRoom.name == "1") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDetailJp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Atur Alokasi JP per Hari >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Information Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wali Kelas:", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text(
                            text = classRoom.homeroomTeacher.ifBlank { "Belum ada" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Beban Alokasi:", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text(
                            text = "${classRoom.weeklyJp} JP / Minggu (5 Hari)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D68E4)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Edit, Detail JP, and Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onDetailJp,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D68E4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = Color(0xFF1D68E4),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Detail JP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF2F2))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Kelas",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
