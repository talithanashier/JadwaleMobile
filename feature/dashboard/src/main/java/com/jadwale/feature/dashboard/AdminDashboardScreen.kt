package com.jadwale.feature.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
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

data class PendingTeacherApproval(
    val id: String,
    val name: String,
    val nip: String,
    val email: String,
    val teacherType: String,
    val statusGuru: String,
    val requestedAt: String,
    var isApproved: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    data: DashboardData,
    onNavigateToScheduleView: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToRoutines: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedClass by remember { mutableStateOf("3A") }
    var showClassDropdown by remember { mutableStateOf(false) }
    val classOptions = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B")

    var showNotifSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showSchoolProfileSheet by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val pendingTeachers by com.jadwale.core.session.TeacherVerificationStore.pendingList.collectAsState()

    LaunchedEffect(Unit) {
        com.jadwale.core.session.TeacherVerificationStore.loadFromBackend(com.jadwale.core.session.SessionStore.current?.schoolId)
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
                                text = "Beranda • ${data.schoolName}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifSheet = true }) {
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D68E4))
                            .clickable { showProfileSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Pengguna",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
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
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color(0xFF1D68E4),
                        selectedIconColor = Color(0xFF1D68E4)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToScheduleView,
                    icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Jadwal") },
                    label = { Text("Jadwal", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        unselectedTextColor = Color(0xFF64748B),
                        unselectedIconColor = Color(0xFF64748B)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Outlined.Menu, contentDescription = "Menu") },
                    label = { Text("Menu", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        unselectedTextColor = Color(0xFF64748B),
                        unselectedIconColor = Color(0xFF64748B)
                    )
                )
            }
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
            // Status Penjadwalan Banner / Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (data.scheduleStatus == ScheduleStatus.BELUM_DIBUAT) "Jadwal Belum Dibuat" else "Jadwal Siap Digunakan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            // Headline Greetings
            Column {
                Text(
                    text = "Selamat Pagi, Operator ${data.schoolName.ifBlank { "SDN Pancasila" }}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tahun Ajaran 2025/2026 • Semester Ganjil",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Bento Grid Stat Cards (2x2)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    title = "Total Guru",
                    count = if (data.teacherCount > 0) "${data.teacherCount} Guru" else "24 Guru",
                    subtitle = "Semua teralokasi",
                    icon = Icons.Default.People,
                    color = Color(0xFF1D68E4),
                    onClick = onNavigateToTeachers,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Total Kelas",
                    count = if (data.classCount > 0) "${data.classCount} Kelas" else if (com.jadwale.core.model.SchoolConfig.isParallel) "12 Kelas" else "6 Kelas",
                    subtitle = if (com.jadwale.core.model.SchoolConfig.isParallel) "1A-6B Paralel" else "1-6 Tunggal",
                    icon = Icons.Default.School,
                    color = Color(0xFFD97706),
                    onClick = onNavigateToClasses,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    title = "Total Mapel",
                    count = if (data.subjectCount > 0) "${data.subjectCount} Mapel" else "9 Mapel",
                    subtitle = "Standar Kurikulum",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFF16A34A),
                    onClick = onNavigateToSubjects,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Status Bentrok",
                    count = "0 Bentrok",
                    subtitle = "Algoritma Optimal",
                    icon = Icons.Default.VerifiedUser,
                    color = Color(0xFF059669),
                    onClick = onNavigateToScheduleView,
                    modifier = Modifier.weight(1f)
                )
            }

            // Big CTA: Mulai Generator Jadwal Otomatis
            Button(
                onClick = onNavigateToGenerator,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Mulai Generator Jadwal Otomatis",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2563EB))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Proses AI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Quick Actions: Jadwal Kelas & Unduh Berkas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Jadwal Kelas",
                    subtitle = "Lihat tabel per rombel",
                    icon = Icons.Default.CalendarToday,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF1D68E4),
                    onClick = onNavigateToScheduleView,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Unduh Berkas",
                    subtitle = "Cetak PDF & format Excel",
                    icon = Icons.Default.FileDownload,
                    iconBg = Color(0xFFDCFCE7),
                    iconColor = Color(0xFF16A34A),
                    onClick = onNavigateToScheduleView,
                    modifier = Modifier.weight(1f)
                )
            }

            // Section 1: Verifikasi Pendaftaran Guru Baru (@guru.sd.belajar.id)
            if (pendingTeachers.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF3E8FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Verifikasi Pendaftaran Guru", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text("Pendidik yang memilih sekolah ini saat mendaftar", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${pendingTeachers.count { !it.isApproved }} Menunggu",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        pendingTeachers.forEach { teacher ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (teacher.isApproved) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (teacher.isApproved) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                        Text(
                                            text = if (teacher.isApproved) "✓ Terverifikasi Aktif" else "Menunggu Persetujuan",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (teacher.isApproved) Color(0xFF15803D) else Color(0xFFD97706)
                                        )
                                    }
                                    Text("• NIP: ${teacher.nip} • ${teacher.statusGuru}", fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("• Email: ${teacher.email}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D68E4))
                                    Text("• Penugasan: ${teacher.teacherType}", fontSize = 11.sp, color = Color(0xFF64748B))

                                    if (!teacher.isApproved) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        com.jadwale.core.session.TeacherVerificationStore.approveTeacher(teacher.id)
                                                        Toast.makeText(context, "✓ Akun guru ${teacher.name} disetujui & ditambahkan ke daftar aktif!", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f).height(32.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Setujui Guru", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    com.jadwale.core.session.TeacherVerificationStore.rejectTeacher(teacher.id)
                                                    Toast.makeText(context, "Permohonan guru ${teacher.name} ditolak.", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(0.6f).height(32.dp)
                                            ) {
                                                Text("Tolak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Modul Akademik Kurikulum
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    title = "Profil Sekolah",
                    subtitle = "Struktur & Tahun Ajaran",
                    icon = Icons.Default.Apartment,
                    iconBg = Color(0xFFF3E8FF),
                    iconColor = Color(0xFF7E22CE),
                    onClick = { showSchoolProfileSheet = true },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Alokasi JP",
                    subtitle = "Beban Mengajar Guru",
                    icon = Icons.Default.GridView,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF1D68E4),
                    onClick = onNavigateToAssignments,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Kegiatan Rutin",
                    subtitle = "Upacara & Istirahat",
                    icon = Icons.Default.AccessTime,
                    iconBg = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFD97706),
                    onClick = onNavigateToRoutines,
                    modifier = Modifier.weight(1f)
                )
            }

            // Jadwal Hari Ini (Senin) Card with Class Selector
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
                        Column {
                            Text(
                                text = "Jadwal Hari Ini (Senin)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "4 Sesi Pembelajaran",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Class Dropdown Selector
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { showClassDropdown = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Kelas $selectedClass",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showClassDropdown,
                                onDismissRequest = { showClassDropdown = false }
                            ) {
                                classOptions.forEach { cls ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Kelas $cls",
                                                fontWeight = if (cls == selectedClass) FontWeight.Bold else FontWeight.Normal,
                                                color = if (cls == selectedClass) Color(0xFF1D68E4) else Color(0xFF0F172A)
                                            )
                                        },
                                        onClick = {
                                            selectedClass = cls
                                            showClassDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DailyScheduleItem(
                        time = "07:00 - 07:35",
                        badge = "Upacara",
                        badgeBg = Color(0xFFFEF3C7),
                        badgeText = Color(0xFFB45309),
                        title = "Upacara Bendera Senin",
                        subtitle = "Lapangan Utama SDN Pancasila"
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    val (subj1Title, subj1Teacher) = when {
                        selectedClass.startsWith("1") || selectedClass.startsWith("2") ->
                            "Tematik Literasi (2 JP)" to "Ibu Dewi Lestari, S.Pd"
                        selectedClass.startsWith("3") || selectedClass.startsWith("4") ->
                            "Matematika (2 JP)" to "Guru: Bpk. Bambang S., M.Pd"
                        else ->
                            "IPAS (2 JP)" to "Guru: Bpk. Kurniawan, S.Pd"
                    }

                    DailyScheduleItem(
                        time = "07:35 - 08:45",
                        badge = "Mapel Inti",
                        badgeBg = Color(0xFFDBEAFE),
                        badgeText = Color(0xFF1D4ED8),
                        title = subj1Title,
                        subtitle = subj1Teacher
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    DailyScheduleItem(
                        time = "08:45 - 09:00",
                        badge = "Istirahat",
                        badgeBg = Color(0xFFFFEDD5),
                        badgeText = Color(0xFFC2410C),
                        title = "Istirahat 1",
                        subtitle = "15 Menit • Lingkungan Sekolah"
                    )

                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

                    val (subj2Title, subj2Teacher) = when {
                        selectedClass.startsWith("1") || selectedClass.startsWith("2") ->
                            "Seni Rupa (2 JP)" to "Guru: Ibu Siti Aminah, S.Pd"
                        selectedClass.startsWith("3") || selectedClass.startsWith("4") ->
                            "Bahasa Indonesia (2 JP)" to "Guru: Ibu Siti Aminah, S.Pd"
                        else ->
                            "Matematika (2 JP)" to "Guru: Bpk. Bambang S., M.Pd"
                    }

                    DailyScheduleItem(
                        time = "09:00 - 10:10",
                        badge = "Bahasa",
                        badgeBg = Color(0xFFDCFCE7),
                        badgeText = Color(0xFF15803D),
                        title = subj2Title,
                        subtitle = subj2Teacher
                    )
                }
            }

            // Public Share Card (matching mockup Beranda Dashboard)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF1D68E4),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Link Jadwal Publik Orang Tua",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Dapat diakses tanpa kata sandi",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val shareUrl = "jadwale.id/share/sdn-pancasila-${selectedClass.lowercase()}"

                    // URL Box with active green dot
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = shareUrl,
                                fontSize = 12.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16A34A))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Jadwale Public Link", "https://$shareUrl")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "✓ Tautan jadwal Kelas $selectedClass berhasil disalin!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salin Tautan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showNotifSheet) {
        NotificationBottomSheet(
            onDismiss = { showNotifSheet = false },
            onNavigateToScheduleView = {
                showNotifSheet = false
                onNavigateToScheduleView()
            }
        )
    }

    if (showProfileSheet) {
        UserProfileBottomSheet(
            currentRole = UserRole.ADMIN_SEKOLAH,
            userName = com.jadwale.core.model.UserProfileConfig.getNameForRole(UserRole.ADMIN_SEKOLAH),
            userAccount = com.jadwale.core.model.UserProfileConfig.getEmailForRole(UserRole.ADMIN_SEKOLAH),
            schoolName = com.jadwale.core.model.SchoolConfig.schoolName,
            onDismiss = { showProfileSheet = false },
            onOpenSchoolProfile = {
                showProfileSheet = false
                showSchoolProfileSheet = true
            },
            onOpenRoleSwitcher = {
                showProfileSheet = false
                showRoleDialog = true
            },
            onOpenHelpCenter = {
                showProfileSheet = false
                showHelpSheet = true
            },
            onLogout = {
                showProfileSheet = false
                Toast.makeText(context, "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()
                onLogout()
            }
        )
    }

    if (showSchoolProfileSheet) {
        SchoolProfileBottomSheet(
            onDismiss = { showSchoolProfileSheet = false }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = UserRole.ADMIN_SEKOLAH,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { role ->
                showRoleDialog = false
                onSwitchRole(role)
            }
        )
    }

    if (showHelpSheet) {
        HelpCenterBottomSheet(
            onDismiss = { showHelpSheet = false }
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    count: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = count, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun DailyScheduleItem(
    time: String,
    badge: String,
    badgeBg: Color,
    badgeText: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.width(82.dp)) {
            Text(
                text = time,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
