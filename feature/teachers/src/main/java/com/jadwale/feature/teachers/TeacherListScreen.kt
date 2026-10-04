package com.jadwale.feature.teachers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.jadwale.core.model.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListScreen(
    viewModel: TeacherViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (teacherId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNotifDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var teacherToDelete by remember { mutableStateOf<com.jadwale.core.model.Teacher?>(null) }

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
                                text = "Manajemen Guru",
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
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onNavigateToAddEdit(null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Guru Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is TeacherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1D68E4))
                    }
                }
                is TeacherUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color(0xFFDC2626))
                    }
                }
                is TeacherUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Title header
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Manajemen Guru",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${state.teachers.size} Guru Terdaftar • SDN Pancasila",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color(0xFF1D68E4),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Beban Mengajar Nasional Card
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF15803D),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Beban Mengajar Nasional",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Rata-rata JP: 18.5 JP / Minggu (Ideal menurut Kemendikbudristek)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { 0.77f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF15803D),
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }

                        // Search Bar
                        item {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Cari nama guru atau NIP...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Buttons: Tambah Guru & Import Excel
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onNavigateToAddEdit(null) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tambah Guru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {},
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.Article, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Import Excel/CSV", fontSize = 12.sp)
                                }
                            }
                        }

                        // Filter info
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Menampilkan ${state.filteredTeachers.size} dari ${state.teachers.size} guru",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {}
                                ) {
                                    Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Filter", fontSize = 12.sp, color = Color(0xFF1D68E4), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Teacher Cards
                        items(state.filteredTeachers) { teacher ->
                            TeacherCardItem(
                                teacher = teacher,
                                onEdit = { onNavigateToAddEdit(teacher.id) },
                                onDelete = { teacherToDelete = teacher }
                            )
                        }
                    }
                }
            }
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
                            Text("Sesuai Kurikulum Merdeka Fase A, B, dan C (24 Guru Terdaftar).", fontSize = 11.sp, color = Color(0xFF1E3A8A))
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

    teacherToDelete?.let { teacher ->
        AlertDialog(
            onDismissRequest = { teacherToDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp)) },
            title = { Text("Hapus Data Guru?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Apakah Anda yakin ingin menghapus data guru ${teacher.name}? Tindakan ini tidak dapat dibatalkan.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = teacher.id
                        teacherToDelete = null
                        viewModel.deleteTeacher(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { teacherToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun TeacherCardItem(
    teacher: Teacher,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val initials = teacher.name.split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "G" }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDBEAFE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = teacher.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Aktif",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                    Text(
                        text = if (teacher.email.isNotBlank()) teacher.email else "NIP: ${teacher.nip}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges: Jenis Guru, Status (PNS/PPPK), Beban JP
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = teacher.teacherType.displayName,
                        fontSize = 10.sp,
                        color = Color(0xFF1D4ED8),
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = teacher.teacherStatus.displayName.split(" ").first(),
                        fontSize = 10.sp,
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${teacher.totalJp} JP/Mgg",
                        fontSize = 10.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (teacher.availabilityNote.isNotBlank() && teacher.availabilityNote != "Senin - Jumat Bersedia Penuh") {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${teacher.availabilityNote}",
                    fontSize = 10.sp,
                    color = Color(0xFFD97706),
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF8FAFC))
            Spacer(modifier = Modifier.height(6.dp))

            // Action row
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
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hadir ${teacher.availableDays.size} Hari",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Edit", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Atur Jam", fontSize = 11.sp, color = Color(0xFF1D68E4), fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF2F2))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Guru", modifier = Modifier.size(15.dp), tint = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}
