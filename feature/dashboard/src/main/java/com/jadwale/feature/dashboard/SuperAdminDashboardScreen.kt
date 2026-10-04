package com.jadwale.feature.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.jadwale.core.model.UserRole
import kotlinx.coroutines.launch

data class SuperAdminSchoolInfo(
    val code: String,
    val name: String,
    val npsn: String,
    val rombelCount: Int,
    val teacherCount: Int,
    val headmaster: String,
    val statusText: String,
    val statusColor: Color,
    val isPublished: Boolean
)

typealias PendingSchoolVerification = com.jadwale.core.session.PendingSchoolItem

data class SystemModuleState(
    val id: String,
    val name: String,
    val description: String,
    var isEnabled: Boolean,
    val category: String,
    val icon: ImageVector
)

data class AuditLogEntry(
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color,
    val title: String,
    val time: String,
    val user: String,
    val desc: String,
    val category: String
)

/**
 * Konsol Pusat Super Admin & Pengawas Sistem
 * Sesuai spesifikasi:
 * 1. Kelola semua sekolah (CRUD Lengkap: Tambah, Edit, Hapus, Detail)
 * 2. Verifikasi semua akun sekolah yang mendaftar (.sch.id)
 * 3. Manajemen modul-modul sistem (Toggle ON/OFF)
 * 4. Log audit aktivitas sistem (Search & Filter)
 * 5. Ekspor cadangan database (.JSON / .SQL)
 * 6. Tidak bisa akses menu jadwal karena perannya murni manajemen & tata kelola sistem
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(
    data: DashboardData,
    onNavigateToScheduleView: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentMainTab by remember { mutableIntStateOf(0) } // 0: Beranda/Sekolah, 1: Verifikasi, 2: Modul & Log

    LaunchedEffect(Unit) {
        com.jadwale.core.session.SchoolVerificationStore.loadFromBackend()
    }

    var maintenanceMode by remember { mutableStateOf(false) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    // Dialog CRUD Sekolah
    var selectedSchoolDetail by remember { mutableStateOf<SuperAdminSchoolInfo?>(null) }
    var showAddSchoolDialog by remember { mutableStateOf(false) }
    var schoolToEdit by remember { mutableStateOf<SuperAdminSchoolInfo?>(null) }
    var schoolToDelete by remember { mutableStateOf<SuperAdminSchoolInfo?>(null) }

    var newSchoolName by remember { mutableStateOf("") }
    var newSchoolNpsn by remember { mutableStateOf("") }
    var newSchoolKepsek by remember { mutableStateOf("") }
    var newSchoolRombel by remember { mutableStateOf("12") }

    var editSchoolName by remember { mutableStateOf("") }
    var editSchoolNpsn by remember { mutableStateOf("") }
    var editSchoolKepsek by remember { mutableStateOf("") }
    var editSchoolRombel by remember { mutableStateOf("12") }

    // 1. Data Semua Sekolah Binaan (CRUD Target)
    var managedSchools by remember {
        mutableStateOf(
            listOf(
                SuperAdminSchoolInfo(
                    code = "P01",
                    name = "SDN Percobaan 01",
                    npsn = "20104011",
                    rombelCount = 18,
                    teacherCount = 26,
                    headmaster = "Hj. Maryati, M.Pd",
                    statusText = "✓ Terbit Aktif",
                    statusColor = Color(0xFF15803D),
                    isPublished = true
                ),
                SuperAdminSchoolInfo(
                    code = "P02",
                    name = "SDN Pancasila 01",
                    npsn = "20108922",
                    rombelCount = 12,
                    teacherCount = 24,
                    headmaster = "Drs. H. Subagyo, M.M",
                    statusText = "✓ Terbit Bebas Bentrok",
                    statusColor = Color(0xFF15803D),
                    isPublished = true
                ),
                SuperAdminSchoolInfo(
                    code = "C03",
                    name = "SDN Cibubur 03",
                    npsn = "20103450",
                    rombelCount = 14,
                    teacherCount = 22,
                    headmaster = "Dra. Endang S., M.Pd",
                    statusText = "Draf Evaluasi",
                    statusColor = Color(0xFF1D68E4),
                    isPublished = false
                ),
                SuperAdminSchoolInfo(
                    code = "M02",
                    name = "SDN Menteng 02",
                    npsn = "20107718",
                    rombelCount = 12,
                    teacherCount = 20,
                    headmaster = "Bpk. Kurniawan, M.Pd",
                    statusText = "✓ Terbit Aktif",
                    statusColor = Color(0xFF15803D),
                    isPublished = true
                ),
                SuperAdminSchoolInfo(
                    code = "R12",
                    name = "SDN Rawamangun 12",
                    npsn = "20109931",
                    rombelCount = 16,
                    teacherCount = 25,
                    headmaster = "Ibu Yuliana, S.Pd",
                    statusText = "ðŸ“ Penyusunan Kurikulum",
                    statusColor = Color(0xFFD97706),
                    isPublished = false
                )
            )
        )
    }

    // 2. Data Pendaftaran Akun Sekolah Website (.sch.id) Menunggu Verifikasi
    val pendingSchools by com.jadwale.core.session.SchoolVerificationStore.pendingSchools.collectAsState()

    // 3. Data Modul-Modul Sistem
    var systemModules by remember {
        mutableStateOf(
            listOf(
                SystemModuleState(
                    id = "csp_solver",
                    name = "Generator CSP AI V2.4 (Bebas Bentrok)",
                    description = "Algoritma otomatisasi penyusunan jadwal sekolah bebas bentrok guru & ruangan.",
                    isEnabled = true,
                    category = "Komputasi & Jadwal",
                    icon = Icons.Default.AutoAwesome
                ),
                SystemModuleState(
                    id = "guest_view",
                    name = "Tautan Guest View Orang Tua / Tamu",
                    description = "Memungkinkan wali murid melihat jadwal kelas melalui link publik tanpa login.",
                    isEnabled = true,
                    category = "Akses & Publikasi",
                    icon = Icons.Default.Share
                ),
                SystemModuleState(
                    id = "pdf_export",
                    name = "Ekspor PDF & Cetak SK Beban Tugas",
                    description = "Penerbitan dokumen format resmi Surat Keputusan Kepala Sekolah & format A4 siap cetak.",
                    isEnabled = true,
                    category = "Dokumen Resmi",
                    icon = Icons.Default.Description
                ),
                SystemModuleState(
                    id = "dapodik_sync",
                    name = "Sinkronisasi Dapodik Wilayah Otomatis",
                    description = "Integrasi data pokok pendidikan, master mapel, dan NUPTK se-wilayah binaan.",
                    isEnabled = true,
                    category = "Integrasi Kemdikbud",
                    icon = Icons.Default.CloudSync
                ),
                SystemModuleState(
                    id = "digital_journal",
                    name = "Jurnal & Presensi Digital Pendidik",
                    description = "Modul pengisian materi ajar pertemuan dan absensi harian kelas oleh wali kelas.",
                    isEnabled = true,
                    category = "Akademik Guru",
                    icon = Icons.Default.EditNote
                )
            )
        )
    }

    // 4. Data Log Audit Aktivitas
    var auditLogSearch by remember { mutableStateOf("") }
    val allAuditLogs = remember {
        listOf(
            AuditLogEntry(
                icon = Icons.Default.Bolt,
                iconBg = Color(0xFFDCFCE7),
                iconColor = Color(0xFF16A34A),
                title = "Generator CSP Selesai",
                time = "10:42 WIB",
                user = "operator@sdnpancasila01.sch.id",
                desc = "Operator SDN Pancasila men-generate jadwal Semester Ganjil (Berhasil, 3.2 detik).",
                category = "Generator CSP"
            ),
            AuditLogEntry(
                icon = Icons.Default.Download,
                iconBg = Color(0xFFEFF6FF),
                iconColor = Color(0xFF1D4ED8),
                title = "Unduh SK Tugas & PDF",
                time = "09:15 WIB",
                user = "bambang.sutrisno@guru.sd.belajar.id",
                desc = "Guru Bpk. Bambang Sutrisno mengunduh berkas SK Pembagian Tugas Mengajar Semester Ganjil.",
                category = "Dokumen"
            ),
            AuditLogEntry(
                icon = Icons.Default.VerifiedUser,
                iconBg = Color(0xFFEFF6FF),
                iconColor = Color(0xFF1D68E4),
                title = "Verifikasi Sekolah Baru",
                time = "08:30 WIB",
                user = "superadmin@jadwale.id",
                desc = "Super Admin menyetujui pendaftaran domain sch.id SDN Menteng 02 ke kluster binaan.",
                category = "Otorisasi"
            ),
            AuditLogEntry(
                icon = Icons.Default.Storage,
                iconBg = Color(0xFFFEF3C7),
                iconColor = Color(0xFFD97706),
                title = "Ekspor Cadangan Database",
                time = "Kemarin, 22:00 WIB",
                user = "superadmin@jadwale.id",
                desc = "Pencadangan berkala snapshot database PostgreSQL (28.4 MB) berhasil diunduh.",
                category = "Database"
            ),
            AuditLogEntry(
                icon = Icons.Default.Sync,
                iconBg = Color(0xFFEFF6FF),
                iconColor = Color(0xFF1D68E4),
                title = "Sinkronisasi Dapodik",
                time = "Kemarin, 16:10 WIB",
                user = "system_scheduler_daemon",
                desc = "Sinkronisasi master referensi kurikulum merdeka se-wilayah binaan selesai.",
                category = "Integrasi"
            )
        )
    }

    val filteredAuditLogs = if (auditLogSearch.isBlank()) allAuditLogs else allAuditLogs.filter {
        it.title.contains(auditLogSearch, ignoreCase = true) ||
        it.user.contains(auditLogSearch, ignoreCase = true) ||
        it.desc.contains(auditLogSearch, ignoreCase = true) ||
        it.category.contains(auditLogSearch, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "SUPERADMIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D68E4),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Pusat Manajemen & Pengawas Sistem",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifSheet = true }) {
                        BadgedBox(badge = {
                            val pendingCount = pendingSchools.count { it.status == "Menunggu Verifikasi" }
                            if (pendingCount > 0) Badge { Text("$pendingCount") }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color(0xFF334155)
                            )
                        }
                    }
                    IconButton(onClick = { showProfileSheet = true }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1D68E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Sesuai requirement: Superadmin GABISA AKSES MENU JADWAL karena perannya murni manajemen sistem
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentMainTab == 0,
                    onClick = { currentMainTab = 0 },
                    icon = { Icon(Icons.Default.Apartment, contentDescription = "Sekolah") },
                    label = { Text("Sekolah", fontSize = 11.sp, fontWeight = if (currentMainTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D68E4),
                        selectedTextColor = Color(0xFF1D68E4),
                        indicatorColor = Color(0xFFEFF6FF),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
                NavigationBarItem(
                    selected = currentMainTab == 1,
                    onClick = { currentMainTab = 1 },
                    icon = {
                        val pendingCount = pendingSchools.count { it.status == "Menunggu Verifikasi" }
                        if (pendingCount > 0) {
                            BadgedBox(badge = { Badge { Text("$pendingCount") } }) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verifikasi")
                            }
                        } else {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Verifikasi")
                        }
                    },
                    label = { Text("Verifikasi", fontSize = 11.sp, fontWeight = if (currentMainTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D68E4),
                        selectedTextColor = Color(0xFF1D68E4),
                        indicatorColor = Color(0xFFEFF6FF),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
                NavigationBarItem(
                    selected = currentMainTab == 2,
                    onClick = { currentMainTab = 2 },
                    icon = { Icon(Icons.Default.Extension, contentDescription = "Modul & Log") },
                    label = { Text("Modul & Log", fontSize = 11.sp, fontWeight = if (currentMainTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D68E4),
                        selectedTextColor = Color(0xFF1D68E4),
                        indicatorColor = Color(0xFFEFF6FF),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                    label = { Text("Menu", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D68E4),
                        selectedTextColor = Color(0xFF1D68E4),
                        indicatorColor = Color(0xFFEFF6FF),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    )
                )
            }
        }
    ) { innerPadding ->
        when (currentMainTab) {
            0 -> {
                // TAB 0: BERANDA MANAJEMEN SEMUA SEKOLAH
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(2.dp)) }

                    // Role Badge & Status
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF1D68E4),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SUPERADMIN • MANAJEMEN SISTEM",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D68E4)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF16A34A))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Server Normal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }
                            }
                        }
                    }

                    // Title
                    item {
                        Column {
                            Text(
                                text = "Kelola Semua Sekolah",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tata kelola registrasi, kuota rombel, dan verifikasi sekolah binaan wilayah.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Metric Grid
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Sekolah Binaan", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("${managedSchools.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                                    Text("Aktif Terdaftar", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Perlu Verifikasi", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("${pendingSchools.count { it.status == "Menunggu Verifikasi" }}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    Text("Sekolah Baru (.sch.id)", fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Total Rombel", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text("${managedSchools.sumOf { it.rombelCount }}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text("Seluruh Wilayah", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }

                    // Backup Database Quick Action Banner
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1D68E4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Ekspor Cadangan Database", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                        Text("Unduh snapshot database lengkap seluruh sekolah (.JSON / .SQL)", fontSize = 11.sp, color = Color(0xFF1D68E4))
                                    }
                                }
                                Button(
                                    onClick = { showBackupDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Ekspor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Header List Sekolah
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daftar Semua Sekolah Binaan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Klik kartu sekolah untuk edit atau hapus dari kluster",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Button(
                                onClick = { showAddSchoolDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Sekolah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // List Sekolah (CRUD Items)
                    items(managedSchools.size) { idx ->
                        val school = managedSchools[idx]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSchoolDetail = school },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (school.isPublished) Color(0xFFEFF6FF) else Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = school.code, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (school.isPublished) Color(0xFF1D68E4) else Color(0xFFD97706))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = school.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "NPSN: ${school.npsn} • ${school.rombelCount} Rombel • ${school.teacherCount} Guru",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "Kepsek: ${school.headmaster}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            schoolToEdit = school
                                            editSchoolName = school.name
                                            editSchoolNpsn = school.npsn
                                            editSchoolKepsek = school.headmaster
                                            editSchoolRombel = school.rombelCount.toString()
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1D68E4), modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { schoolToDelete = school }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            1 -> {
                // TAB 1: VERIFIKASI SEMUA AKUN SEKOLAH YANG MENDAFTAR (.sch.id)
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Verifikasi Pendaftaran Sekolah",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sekolah mendaftarkan nama & akun (.sch.id) di website, lalu Super Admin memverifikasi sebelum guru dapat memilih sekolah tersebut.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    if (pendingSchools.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Semua Pendaftaran Sekolah Telah Diverifikasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tidak ada permohonan sekolah baru yang pending saat ini.", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    } else {
                        items(pendingSchools.size) { idx ->
                            val pending = pendingSchools[idx]
                            val isPending = pending.status == "Menunggu Verifikasi"
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) Color(0xFFDBEAFE) else Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(pending.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                        Surface(
                                            color = if (isPending) Color(0xFFFEF3C7) else if (pending.status.contains("Setujui")) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = pending.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPending) Color(0xFFD97706) else if (pending.status.contains("Setujui")) Color(0xFF15803D) else Color(0xFFDC2626),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("• NPSN: ${pending.npsn}", fontSize = 12.sp, color = Color(0xFF334155))
                                            Text("• Akun Admin Sekolah: ${pending.email}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D68E4))
                                            Text("• Kepala Sekolah: ${pending.headmaster}", fontSize = 12.sp, color = Color(0xFF334155))
                                            Text("• Alamat: ${pending.address}", fontSize = 11.sp, color = Color(0xFF64748B))
                                            Text("• Terdaftar: ${pending.registrationDate}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        }
                                    }

                                    if (isPending) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    // Setujui sekolah -> pindah ke managedSchools aktif
                                                    coroutineScope.launch {
                                                        com.jadwale.core.session.SchoolVerificationStore.approveSchool(pending.id)
                                                    }
                                                    val newCode = "S" + (managedSchools.size + 1)
                                                    managedSchools = managedSchools + SuperAdminSchoolInfo(
                                                        code = newCode,
                                                        name = pending.name,
                                                        npsn = pending.npsn,
                                                        rombelCount = 12,
                                                        teacherCount = 20,
                                                        headmaster = pending.headmaster,
                                                        statusText = "✓ Terbit Aktif",
                                                        statusColor = Color(0xFF15803D),
                                                        isPublished = true
                                                    )
                                                    Toast.makeText(context, "✓ ${pending.name} berhasil diverifikasi & aktif!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Setujui & Verifikasi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    com.jadwale.core.session.SchoolVerificationStore.rejectSchool(pending.id)
                                                    Toast.makeText(context, "Permohonan ${pending.name} ditolak.", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(0.7f)
                                            ) {
                                                Text("Tolak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: MANAJEMEN MODUL-MODUL & LOG AUDIT SISTEM
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Modul
                    item {
                        Column {
                            Text(
                                text = "Manajemen Modul-Modul Sistem",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Super Admin dapat mengaktifkan / menonaktifkan fitur operasional yang tersedia untuk seluruh sekolah.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // List Modul Toggles
                    items(systemModules.size) { idx ->
                        val module = systemModules[idx]
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (module.isEnabled) Color(0xFFEFF6FF) else Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = module.icon,
                                            contentDescription = null,
                                            tint = if (module.isEnabled) Color(0xFF1D68E4) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(module.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(module.description, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
                                    }
                                }

                                Switch(
                                    checked = module.isEnabled,
                                    onCheckedChange = { isChecked ->
                                        systemModules = systemModules.map { m ->
                                            if (m.id == module.id) m.copy(isEnabled = isChecked) else m
                                        }
                                        val statusStr = if (isChecked) "DIAKTIFKAN" else "DINONAKTIFKAN"
                                        Toast.makeText(context, "Modul '${module.name}' $statusStr secara global!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = Color(0xFF1D68E4)
                                    )
                                )
                            }
                        }
                    }

                    // Header Log Audit
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Log Audit Aktivitas Sistem",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Catatan kronologis aktivitas login, generasi CSP, dan transaksi data",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Button(
                                onClick = { showBackupDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ekspor DB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Search Log Bar
                    item {
                        OutlinedTextField(
                            value = auditLogSearch,
                            onValueChange = { auditLogSearch = it },
                            placeholder = { Text("Cari log audit berdasarkan user, aksi, modul...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Audit Logs List
                    items(filteredAuditLogs.size) { idx ->
                        val log = filteredAuditLogs[idx]
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(log.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = log.icon, contentDescription = null, tint = log.iconColor, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = log.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Text(text = log.time, fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "User: ${log.user}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D68E4))
                                    Text(text = log.desc, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // DIALOG: Tambah Sekolah Binaan Baru
    if (showAddSchoolDialog) {
        AlertDialog(
            onDismissRequest = { showAddSchoolDialog = false },
            icon = { Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Color(0xFF1D68E4)) },
            title = { Text("Tambah Sekolah Binaan", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSchoolName,
                        onValueChange = { newSchoolName = it },
                        label = { Text("Nama Sekolah (contoh: SDN Cempaka 01)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSchoolNpsn,
                        onValueChange = { newSchoolNpsn = it },
                        label = { Text("NPSN (8 Digit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSchoolKepsek,
                        onValueChange = { newSchoolKepsek = it },
                        label = { Text("Nama Kepala Sekolah") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSchoolRombel,
                        onValueChange = { newSchoolRombel = it },
                        label = { Text("Jumlah Rombel Kelas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSchoolName.isNotBlank()) {
                            val newCode = "S0" + (managedSchools.size + 1)
                            val rombelInt = newSchoolRombel.toIntOrNull() ?: 12
                            managedSchools = managedSchools + SuperAdminSchoolInfo(
                                code = newCode,
                                name = newSchoolName.trim(),
                                npsn = if (newSchoolNpsn.isNotBlank()) newSchoolNpsn.trim() else "2010" + (1000..9999).random(),
                                rombelCount = rombelInt,
                                teacherCount = rombelInt * 2,
                                headmaster = if (newSchoolKepsek.isNotBlank()) newSchoolKepsek.trim() else "Bpk/Ibu Kepala Sekolah",
                                statusText = "Terbit Aktif",
                                statusColor = Color(0xFF15803D),
                                isPublished = true
                            )
                            Toast.makeText(context, "$newSchoolName berhasil ditambahkan ke daftar sekolah!", Toast.LENGTH_LONG).show()
                            newSchoolName = ""
                            newSchoolNpsn = ""
                            newSchoolKepsek = ""
                            showAddSchoolDialog = false
                        } else {
                            Toast.makeText(context, "Silakan isi nama sekolah terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Simpan Sekolah") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSchoolDialog = false }) { Text("Batal") }
            }
        )
    }

    // DIALOG: Edit Sekolah Binaan
    if (schoolToEdit != null) {
        val editing = schoolToEdit!!
        AlertDialog(
            onDismissRequest = { schoolToEdit = null },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF1D68E4)) },
            title = { Text("Edit Data Sekolah", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editSchoolName,
                        onValueChange = { editSchoolName = it },
                        label = { Text("Nama Sekolah") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSchoolNpsn,
                        onValueChange = { editSchoolNpsn = it },
                        label = { Text("NPSN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSchoolKepsek,
                        onValueChange = { editSchoolKepsek = it },
                        label = { Text("Kepala Sekolah") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSchoolRombel,
                        onValueChange = { editSchoolRombel = it },
                        label = { Text("Jumlah Rombel") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editSchoolName.isNotBlank()) {
                            val rombelInt = editSchoolRombel.toIntOrNull() ?: editing.rombelCount
                            managedSchools = managedSchools.map {
                                if (it.code == editing.code) {
                                    it.copy(
                                        name = editSchoolName.trim(),
                                        npsn = editSchoolNpsn.trim(),
                                        headmaster = editSchoolKepsek.trim(),
                                        rombelCount = rombelInt,
                                        teacherCount = rombelInt * 2
                                    )
                                } else it
                            }
                            Toast.makeText(context, "Data $editSchoolName berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            schoolToEdit = null
                        } else {
                            Toast.makeText(context, "Nama sekolah tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Perbarui") }
            },
            dismissButton = {
                TextButton(onClick = { schoolToEdit = null }) { Text("Batal") }
            }
        )
    }

    // DIALOG: Hapus Sekolah Binaan
    if (schoolToDelete != null) {
        val deleting = schoolToDelete!!
        AlertDialog(
            onDismissRequest = { schoolToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp)) },
            title = { Text("Hapus Sekolah dari Binaan?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus ${deleting.name} (NPSN: ${deleting.npsn}) dari daftar sekolah binaan? Tindakan ini akan mencabut akses operasional sekolah.",
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        managedSchools = managedSchools.filter { it.code != deleting.code }
                        Toast.makeText(context, "Sekolah ${deleting.name} telah dihapus.", Toast.LENGTH_SHORT).show()
                        schoolToDelete = null
                        selectedSchoolDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { schoolToDelete = null }) { Text("Batal") }
            }
        )
    }

    // DIALOG: Detail Info Sekolah
    if (selectedSchoolDetail != null) {
        val school = selectedSchoolDetail!!
        AlertDialog(
            onDismissRequest = { selectedSchoolDetail = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Apartment,
                    contentDescription = null,
                    tint = Color(0xFF1D68E4),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = school.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Kode Kluster:", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(school.code, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("NPSN:", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(school.npsn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Kepala Sekolah:", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(school.headmaster, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Kapasitas:", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text("${school.rombelCount} Rombel • ${school.teacherCount} Guru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Status Operasional:", fontSize = 12.sp, color = Color(0xFF64748B))
                                Text(school.statusText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = school.statusColor)
                            }
                        }
                    }
                    Text(
                        text = "Sebagai Super Admin Pengawas Wilayah, Anda bertugas mengelola master data sekolah, verifikasi akun, dan pemeliharaan modul sistem.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = school
                        selectedSchoolDetail = null
                        schoolToEdit = target
                        editSchoolName = target.name
                        editSchoolNpsn = target.npsn
                        editSchoolKepsek = target.headmaster
                        editSchoolRombel = target.rombelCount.toString()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Edit Sekolah")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSchoolDetail = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    // DIALOG: Ekspor Cadangan Database (.JSON & .SQL Dump)
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            icon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(36.dp)) },
            title = { Text("Ekspor Cadangan Database", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Pencadangan snapshot data lengkap seluruh sekolah binaan, guru, rombel kelas, alokasi jam mengajar, dan log audit wilayah.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Ringkasan Entitas Ter-backup:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                            Text("• Total Sekolah: ${managedSchools.size} Instansi", fontSize = 11.sp, color = Color(0xFF1D68E4))
                            Text("• Total Guru & NUPTK: 116 Tenaga Pendidik", fontSize = 11.sp, color = Color(0xFF1D68E4))
                            Text("• Total Rombel: ${managedSchools.sumOf { it.rombelCount }} Kelas", fontSize = 11.sp, color = Color(0xFF1D68E4))
                            Text("• Format: JSON Schema Kemdikbud & SQL Dump", fontSize = 11.sp, color = Color(0xFF1D68E4))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "✓ Cadangan database jadwale_backup_full_2026.json (28.4 MB) berhasil diunduh!", Toast.LENGTH_LONG).show()
                        showBackupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Unduh .JSON")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(context, "✓ Berkas SQL dump PostgreSQL berhasil diekspor!", Toast.LENGTH_SHORT).show()
                        showBackupDialog = false
                    }
                ) {
                    Text("Unduh .SQL")
                }
            }
        )
    }

    if (showNotifSheet) {
        NotificationBottomSheet(
            onDismiss = { showNotifSheet = false },
            onNavigateToScheduleView = {}
        )
    }

    if (showProfileSheet) {
        UserProfileBottomSheet(
            currentRole = UserRole.SUPER_ADMIN,
            userName = com.jadwale.core.model.UserProfileConfig.getNameForRole(UserRole.SUPER_ADMIN),
            userAccount = com.jadwale.core.model.UserProfileConfig.getEmailForRole(UserRole.SUPER_ADMIN),
            schoolName = "Pusat Kontrol Kemdikbud",
            onDismiss = { showProfileSheet = false },
            onOpenSchoolProfile = {},
            onOpenRoleSwitcher = {
                showProfileSheet = false
                showRoleDialog = true
            },
            onOpenHelpCenter = {},
            onLogout = {
                showProfileSheet = false
                onLogout()
            }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = UserRole.SUPER_ADMIN,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { role ->
                showRoleDialog = false
                onSwitchRole(role)
            }
        )
    }
}
