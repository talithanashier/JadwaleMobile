package com.jadwale.feature.dashboard

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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

private data class MenuUserIdentity(
    val name: String,
    val roleTitle: String,
    val subtitle: String,
    val badgeBg: Color,
    val badgeText: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color,
    val iconBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    currentRole: UserRole = UserRole.ADMIN_SEKOLAH,
    userName: String? = null,
    schoolName: String = "SDN Pancasila 01",
    onNavigateToDashboard: () -> Unit,
    onNavigateToScheduleView: () -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToClasses: () -> Unit,
    onNavigateToSubjects: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onLogout: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPublicLinkActive by remember { mutableStateOf(true) }

    var showNotifSheet by remember { mutableStateOf(false) }
    var showUserProfileSheet by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showConsoleSheet by remember { mutableStateOf(false) }
    var showHelpSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    // Dialogs for dynamic actions
    var showSkDialog by remember { mutableStateOf(false) }
    var showJurnalDialog by remember { mutableStateOf(false) }
    var showPresensiDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showSwapDialog by remember { mutableStateOf(false) }
    var showUniformDialog by remember { mutableStateOf(false) }
    var showBellDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showServerStatusDialog by remember { mutableStateOf(false) }
    var showFlushCacheDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

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
                                text = "Menu Modul Lengkap",
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
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Pengaturan",
                            tint = Color(0xFF334155)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D68E4))
                            .clickable { showUserProfileSheet = true },
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
        },
        bottomBar = {
            if (currentRole == UserRole.SUPER_ADMIN) {
                // Navigasi Superadmin Konsisten 4 Item: Sekolah, Verifikasi, Modul & Log, Menu
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToDashboard,
                        icon = { Icon(Icons.Default.Apartment, contentDescription = "Sekolah") },
                        label = { Text("Sekolah", fontSize = 11.sp) },
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
                        onClick = onNavigateToDashboard,
                        icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Verifikasi") },
                        label = { Text("Verifikasi", fontSize = 11.sp) },
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
                        onClick = onNavigateToDashboard,
                        icon = { Icon(Icons.Default.Extension, contentDescription = "Modul & Log") },
                        label = { Text("Modul & Log", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D68E4),
                            selectedTextColor = Color(0xFF1D68E4),
                            indicatorColor = Color(0xFFEFF6FF),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        )
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                        label = { Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D68E4),
                            selectedTextColor = Color(0xFF1D68E4),
                            indicatorColor = Color(0xFFEFF6FF),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        )
                    )
                }
            } else {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToDashboard,
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            val resolvedUserName = if (!userName.isNullOrBlank() && userName != "default") userName
                else com.jadwale.core.model.UserProfileConfig.getNameForRole(currentRole)

            // Role-Tailored User Identity Card
            val identity = when (currentRole) {
                UserRole.GURU -> {
                    val sub = if (resolvedUserName.contains("Bambang", ignoreCase = true)) {
                        "NIP. 19820315 200801 1 008 • Sertifikasi Pendidik"
                    } else {
                        "Tenaga Pendidik Aktif • $schoolName"
                    }
                    MenuUserIdentity(
                        name = resolvedUserName,
                        roleTitle = "Guru Pengampu",
                        subtitle = sub,
                        badgeBg = Color(0xFFDBEAFE),
                        badgeText = Color(0xFF1D4ED8),
                        icon = Icons.Default.School,
                        iconTint = Color(0xFF1D4ED8),
                        iconBg = Color(0xFFEFF6FF)
                    )
                }
                UserRole.SUPER_ADMIN -> MenuUserIdentity(
                    name = resolvedUserName,
                    roleTitle = "Super Admin • Pengawas Wilayah",
                    subtitle = "Dinas Pendidikan Wilayah • Multi-Sekolah Binaan (Non-Operator)",
                    badgeBg = Color(0xFFF3E8FF),
                    badgeText = Color(0xFF7E22CE),
                    icon = Icons.Default.AdminPanelSettings,
                    iconTint = Color(0xFF7E22CE),
                    iconBg = Color(0xFFFAF5FF)
                )
                UserRole.UMUM -> MenuUserIdentity(
                    name = resolvedUserName,
                    roleTitle = "Wali Murid • Akses Tamu",
                    subtitle = "$schoolName • Semester Ganjil 2025/2026",
                    badgeBg = Color(0xFFCCFBF1),
                    badgeText = Color(0xFF0F766E),
                    icon = Icons.Default.People,
                    iconTint = Color(0xFF0F766E),
                    iconBg = Color(0xFFF0FDFA)
                )
                UserRole.ADMIN_SEKOLAH -> MenuUserIdentity(
                    name = resolvedUserName,
                    roleTitle = "Admin Sekolah",
                    subtitle = "NPSN : ${com.jadwale.core.model.SchoolConfig.npsn} • Operator Dapodik",
                    badgeBg = Color(0xFFDCFCE7),
                    badgeText = Color(0xFF15803D),
                    icon = Icons.Default.Badge,
                    iconTint = Color(0xFF1D68E4),
                    iconBg = Color(0xFFEFF6FF)
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showUserProfileSheet = true }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(identity.iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = identity.icon,
                                contentDescription = null,
                                tint = identity.iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = identity.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(identity.badgeBg)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = identity.roleTitle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = identity.badgeText
                                    )
                                }
                            }
                            Text(
                                text = identity.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentRole) {
                                    UserRole.GURU -> "Akun Pendidik Terhubung"
                                    UserRole.SUPER_ADMIN -> "Sesi Konsol Pusat Aktif"
                                    UserRole.UMUM -> "Mode Tamu Publik"
                                    UserRole.ADMIN_SEKOLAH -> "Mode Operator Aktif"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showUserProfileSheet = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ubah Profil", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { showRoleDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SyncAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ganti Role", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ROLE-SPECIFIC MENU GROUPS
            when (currentRole) {
                UserRole.GURU -> {
                    // Group 1: Tugas & Mengajar
                    MenuGroupSection(
                        title = "Aktivitas Mengajar & Tugas Guru",
                        subtitle = "Alokasi jam ajar, SK beban kerja, dan catatan kelas"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Today,
                            iconBg = Color(0xFFEFF6FF),
                            iconColor = Color(0xFF1D68E4),
                            title = "Jadwal Mengajar Hari Ini",
                            subtitle = "Sesi mengajar harian, ruang kelas & isi jurnal",
                            badge = "Hari Ini",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onNavigateToDashboard
                        )
                        MenuItemCard(
                            icon = Icons.Default.CalendarMonth,
                            iconBg = Color(0xFFEFF6FF),
                            iconColor = Color(0xFF1D68E4),
                            title = "Jadwal Mengajar Mingguan",
                            subtitle = "Matriks jadwal per kelas binaan (3A, 4A, 5A)",
                            badge = "18 JP",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onNavigateToScheduleView
                        )
                        MenuItemCard(
                            icon = Icons.Default.Description,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            title = "SK Pembagian Beban Tugas (PDF)",
                            subtitle = "Surat Keputusan Kepala Sekolah Semester Ganjil",
                            badge = "24 JP Valid",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = { showSkDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.EditNote,
                            iconBg = Color(0xFFFEF3C7),
                            iconColor = Color(0xFFD97706),
                            title = "Jurnal Pembelajaran Digital",
                            subtitle = "Catatan materi pertemuan & ketuntasan belajar",
                            badge = "Kurikulum Merdeka",
                            badgeColor = Color(0xFFFEF3C7),
                            badgeTextColor = Color(0xFFB45309),
                            onClick = { showJurnalDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Groups,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Presensi & Bimbingan Kelas 3A",
                            subtitle = "Rekap kehadiran 28 siswa & catatan konseling",
                            badge = "28 Siswa",
                            badgeColor = Color(0xFFE2E8F0),
                            badgeTextColor = Color(0xFF334155),
                            onClick = { showPresensiDialog = true }
                        )
                    }

                    // Group 2: Kalender & Modul Ajar
                    MenuGroupSection(
                        title = "Kalender & Dokumen Kurikulum",
                        subtitle = "Agenda semester, jadwal PTS/PAS, dan modul ajar"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Event,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Kalender Akademik & Hari Libur",
                            subtitle = "Jadwal PTS, PAS, Asesmen Nasional, & Libur",
                            badge = null,
                            onClick = { showCalendarDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.MenuBook,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Format Modul Ajar & RPP",
                            subtitle = "Unduh template RPP & lembar kerja siswa",
                            badge = "Kemdikbud",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = { showTemplateDialog = true }
                        )
                    }

                    // Group 3: Layanan & Akun
                    MenuGroupSection(
                        title = "Layanan & Bantuan Guru",
                        subtitle = "Permohonan jadwal dan pusat bantuan pengajar"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.SwapCalls,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Ajukan Permohonan Tukar Jam Mengajar",
                            subtitle = "Kirim pengajuan tukar jadwal ke operator kurikulum",
                            badge = null,
                            onClick = { showSwapDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.HelpOutline,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pusat Bantuan & Panduan Guru",
                            subtitle = "Hotline WhatsApp & video panduan Jadwale",
                            badge = null,
                            onClick = { showHelpSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Settings,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pengaturan Aplikasi",
                            subtitle = "Pengingat jam mengajar, tampilan, & dokumen cetak",
                            badge = null,
                            onClick = { showSettingsSheet = true }
                        )
                    }
                }

                UserRole.SUPER_ADMIN -> {
                    // Group 1: Monitoring Pusat
                    MenuGroupSection(
                        title = "Pusat Kontrol Sistem & Server",
                        subtitle = "Monitoring multi-instansi, status solver AI, dan audit log"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Apartment,
                            iconBg = Color(0xFFF3E8FF),
                            iconColor = Color(0xFF7E22CE),
                            title = "Konsol Multi-Sekolah",
                            subtitle = "5 Sekolah Binaan Wilayah Supervisi Dinas",
                            badge = "5 Sekolah Terdaftar",
                            badgeColor = Color(0xFFF3E8FF),
                            badgeTextColor = Color(0xFF7E22CE),
                            onClick = { showConsoleSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Speed,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            title = "Status CSP Solver AI & Latensi",
                            subtitle = "Algoritma CSP V2.4 • 0.3s waktu generasi rata-rata",
                            badge = "Normal / 99.9%",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = { showServerStatusDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Security,
                            iconBg = Color(0xFFEFF6FF),
                            iconColor = Color(0xFF1D68E4),
                            title = "Audit Log & Jejak Aktivitas",
                            subtitle = "Catatan pembuatan jadwal dan pergantian sesi",
                            badge = "128 Log",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = { showConsoleSheet = true }
                        )
                    }

                    // Group 2: Basis Data
                    MenuGroupSection(
                        title = "Basis Data & Pemeliharaan",
                        subtitle = "Operasi Redis cache dan cadangan basis data"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.CleaningServices,
                            iconBg = Color(0xFFFEF3C7),
                            iconColor = Color(0xFFD97706),
                            title = "Flush Redis Cache & Buffer",
                            subtitle = "Bersihkan antrean komputasi dan memori sementara",
                            badge = null,
                            onClick = { showFlushCacheDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.CloudDownload,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Ekspor Cadangan Database (.JSON)",
                            subtitle = "Unduh dump snapshot data seluruh sekolah",
                            badge = null,
                            onClick = { showBackupDialog = true }
                        )
                    }

                    // Group 3: Akun & Konfigurasi
                    MenuGroupSection(
                        title = "Akun & Keamanan",
                        subtitle = "Simulasi peran dan pengaturan otentikasi"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.SyncAlt,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Mode Demonstrasi / Role Switcher",
                            subtitle = "Uji coba akses sebagai Guru, Admin, atau Tamu",
                            badge = null,
                            onClick = { showRoleDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.HelpOutline,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Dokumentasi API & Dukungan Teknis",
                            subtitle = "Spesifikasi integrasi Kemdikbud & hotline dev",
                            badge = null,
                            onClick = { showHelpSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Settings,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pengaturan Aplikasi & Sistem",
                            subtitle = "Preferensi notifikasi pengawas, format waktu, dan cache",
                            badge = null,
                            onClick = { showSettingsSheet = true }
                        )
                    }
                }

                UserRole.UMUM -> {
                    // Group 1: Informasi Pembelajaran Siswa
                    MenuGroupSection(
                        title = "Informasi Pembelajaran Siswa",
                        subtitle = "Jadwal mata pelajaran, perlengkapan, dan seragam sekolah"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.School,
                            iconBg = Color(0xFFEFF6FF),
                            iconColor = Color(0xFF1D68E4),
                            title = "Jadwal Pelajaran Kelas (1A s/d 6B)",
                            subtitle = "Lihat jadwal lengkap siswa per hari & guru pengampu",
                            badge = "12 Rombel",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onNavigateToScheduleView
                        )
                        MenuItemCard(
                            icon = Icons.Default.Checkroom,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            title = "Jadwal Seragam Sekolah Mingguan",
                            subtitle = "Putih Merah, Batik Sekolah, Olahraga, & Pramuka",
                            badge = "Panduan Resmi",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = { showUniformDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Event,
                            iconBg = Color(0xFFFEF3C7),
                            iconColor = Color(0xFFD97706),
                            title = "Kalender Libur & Kegiatan Sekolah",
                            subtitle = "Jadwal Penilaian Tengah Semester, Asesmen & Libur",
                            badge = null,
                            onClick = { showCalendarDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.AccessTime,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Jam Masuk & Tata Waktu Belajar",
                            subtitle = "Pukul 07.00 - 12.45 WIB • Jadwal Istirahat 2 Sesi",
                            badge = null,
                            onClick = { showBellDialog = true }
                        )
                    }

                    // Group 2: Kontak & Tata Usaha
                    MenuGroupSection(
                        title = "Layanan & Komunikasi Sekolah",
                        subtitle = "Hotline informasi tata usaha dan tata tertib"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Phone,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            title = "Kontak Tata Usaha & Hotline Sekolah",
                            subtitle = "Telepon, WhatsApp resmi, & surel informasi",
                            badge = "Buka 07.00-15.00",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = { showContactDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.FactCheck,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Tata Tertib Siswa SDN Pancasila",
                            subtitle = "Pedoman kedisiplinan, jam kehadiran & bekal makanan",
                            badge = null,
                            onClick = { showRulesDialog = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Settings,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pengaturan Aplikasi",
                            subtitle = "Pengingat jadwal anak, format waktu, dan cache offline",
                            badge = null,
                            onClick = { showSettingsSheet = true }
                        )
                    }

                    // Group 3: Akses Staf
                    MenuGroupSection(
                        title = "Akses Staf & Guru",
                        subtitle = "Masuk menggunakan kredensial pengajar atau operator"
                    ) {
                        MenuItemCard(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconBg = Color(0xFFEFF6FF),
                            iconColor = Color(0xFF1D68E4),
                            title = "Masuk sebagai Guru / Staf Sekolah",
                            subtitle = "Login untuk mengakses fitur absensi dan data sekolah",
                            badge = "Login Staf",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onLogout
                        )
                    }
                }

                UserRole.ADMIN_SEKOLAH -> {
                    // Group 1: Master Data & Akademik
                    MenuGroupSection(
                        title = "Master Data & Akademik",
                        subtitle = "Kelola data pokok sekolah dan beban kerja"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Apartment,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Profil Sekolah",
                            subtitle = "Alamat, Kepala Sekolah, Semester & Reg.",
                            badge = null,
                            onClick = { showProfileSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.School,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Manajemen Kelas",
                            subtitle = "Alokasi Kelas 1A s/d 6B & Wali Kelas",
                            badge = "12 Kelas",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onNavigateToClasses
                        )
                        MenuItemCard(
                            icon = Icons.Default.People,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Manajemen Guru",
                            subtitle = "24 Guru Terdaftar & Ketersediaan Jam",
                            badge = "24 Guru",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF15803D),
                            onClick = onNavigateToTeachers
                        )
                        MenuItemCard(
                            icon = Icons.Default.MenuBook,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Manajemen Mata Pelajaran",
                            subtitle = "9 Mapel Kurikulum Merdeka & Prioritas",
                            badge = "9 Mapel",
                            badgeColor = Color(0xFFDBEAFE),
                            badgeTextColor = Color(0xFF1D4ED8),
                            onClick = onNavigateToSubjects
                        )
                        MenuItemCard(
                            icon = Icons.Default.AccessTime,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Kegiatan Rutin & Istirahat",
                            subtitle = "Upacara Senin, Dhuha/Senam, Istirahat",
                            badge = null,
                            onClick = onNavigateToRoutines
                        )
                        MenuItemCard(
                            icon = Icons.Default.GridView,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Alokasi Jam Mengajar (JP)",
                            subtitle = "Matriks beban guru per kelas & keterikatan",
                            badge = null,
                            onClick = onNavigateToAssignments
                        )
                    }

                    // Group 2: Otomatisasi & Distribusi
                    MenuGroupSection(
                        title = "Otomatisasi & Distribusi",
                        subtitle = "Penyusunan jadwal otomatis dan publikasi"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.AutoAwesome,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            title = "Generator AI Bebas Bentrok",
                            subtitle = "CSP Solver V2.4 • Kompatibel Kurikulum",
                            badge = "Siap Generate",
                            badgeColor = Color(0xFF15803D),
                            badgeTextColor = Color.White,
                            onClick = onNavigateToGenerator
                        )
                        MenuItemCard(
                            icon = Icons.Default.Print,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Ekspor & Cetak Jadwal",
                            subtitle = "Format PDF A4 Siap Cetak & Excel .xlsx",
                            badge = null,
                            onClick = onNavigateToScheduleView
                        )
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = Color(0xFF475569),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Link Publik Orang Tua",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Tautan lihat jadwal tanpa harus login",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Switch(
                                    checked = isPublicLinkActive,
                                    onCheckedChange = {
                                        isPublicLinkActive = it
                                        Toast.makeText(context, if (it) "Tautan publik diaktifkan" else "Tautan publik dinonaktifkan", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }

                    // Group 3: Sistem & Keamanan
                    MenuGroupSection(
                        title = "Sistem & Keamanan",
                        subtitle = "Audit pembaruan dan bantuan penggunaan"
                    ) {
                        MenuItemCard(
                            icon = Icons.Default.Security,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Konsol Super Admin & Log",
                            subtitle = "Audit aktivitas operator & multi-sekolah",
                            badge = null,
                            onClick = { showConsoleSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.HelpOutline,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pusat Bantuan & Panduan Gaptek",
                            subtitle = "Video langkah mudah dan hotline WA",
                            badge = null,
                            onClick = { showHelpSheet = true }
                        )
                        MenuItemCard(
                            icon = Icons.Default.Settings,
                            iconBg = Color(0xFFF1F5F9),
                            iconColor = Color(0xFF475569),
                            title = "Pengaturan Aplikasi & Cetak",
                            subtitle = "Format kertas cetak PDF, notifikasi bel, & cache lokal",
                            badge = null,
                            onClick = { showSettingsSheet = true }
                        )
                    }
                }
            }

            // Bottom Action Button (Logout / Switch)
            if (currentRole == UserRole.UMUM) {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFF1D68E4)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuk sebagai Guru / Staf",
                        color = Color(0xFF1D68E4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Button(
                    onClick = { showLogoutConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar dari Akun",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Kemdikbud Footer Note
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jadwale v3.1 Standar Kemendikbudristek",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bebas Bentrok 100% • Algoritma CSP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF15803D)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { role ->
                showRoleDialog = false
                onSwitchRole(role)
            }
        )
    }

    if (showProfileSheet) {
        SchoolProfileBottomSheet(
            onDismiss = { showProfileSheet = false }
        )
    }

    if (showConsoleSheet) {
        SuperAdminConsoleBottomSheet(
            onDismiss = { showConsoleSheet = false }
        )
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

    if (showUserProfileSheet) {
        UserProfileBottomSheet(
            currentRole = currentRole,
            userName = userName,
            schoolName = schoolName,
            onDismiss = { showUserProfileSheet = false },
            onOpenSchoolProfile = {
                showUserProfileSheet = false
                showProfileSheet = true
            },
            onOpenRoleSwitcher = {
                showUserProfileSheet = false
                showRoleDialog = true
            },
            onOpenHelpCenter = {
                showUserProfileSheet = false
                showHelpSheet = true
            },
            onOpenSettings = {
                showUserProfileSheet = false
                showSettingsSheet = true
            },
            onLogout = onLogout
        )
    }

    if (showSettingsSheet) {
        AppSettingsBottomSheet(
            onDismiss = { showSettingsSheet = false }
        )
    }

    if (showHelpSheet) {
        HelpCenterBottomSheet(
            onDismiss = { showHelpSheet = false }
        )
    }

    // Role-specific Interactive Action Dialogs
    if (showSkDialog) {
        AlertDialog(
            onDismissRequest = { showSkDialog = false },
            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("SK Pembagian Beban Tugas Guru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nomor: 421.2/045/SDN-01/VII/2025", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text("Nama: ${userName ?: "Bpk. Bambang Sutrisno, M.Pd"}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Jam Mengajar Wajib: 18 JP (Matematika & Wali Kelas 3A)\n• Jam Ekuivalensi Tugas Tambahan: 6 JP\n• Total Beban Mingguan: 24 JP (Memenuhi Syarat TPG)", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkDialog = false
                        Toast.makeText(context, "Berhasil mengunduh SK_Beban_Tugas_2025.pdf", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Unduh Salinan PDF") }
            },
            dismissButton = {
                TextButton(onClick = { showSkDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showJurnalDialog) {
        AlertDialog(
            onDismissRequest = { showJurnalDialog = false },
            icon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Jurnal Pembelajaran Digital", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kelas: 3A • Mata Pelajaran: Matematika", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Materi Hari Ini: Perkalian Bersusun Bilangan Cacah (Bab 3)\nStatus: 26 dari 28 siswa memahami materi secara tuntas.", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showJurnalDialog = false
                        Toast.makeText(context, "Jurnal harian tersimpan ke sistem sekolah", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Simpan Jurnal") }
            },
            dismissButton = {
                TextButton(onClick = { showJurnalDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showPresensiDialog) {
        AlertDialog(
            onDismissRequest = { showPresensiDialog = false },
            icon = { Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Presensi Siswa Kelas 3A", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Siswa: 28 Anak", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Hadir: 27 Siswa\nSakit: 1 Siswa (Aditya Pratama)\nIzin / Alpa: 0", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPresensiDialog = false
                        Toast.makeText(context, "Presensi kelas 3A disinkronkan ke Dapodik", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Kirim ke Dapodik") }
            },
            dismissButton = {
                TextButton(onClick = { showPresensiDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showCalendarDialog) {
        AlertDialog(
            onDismissRequest = { showCalendarDialog = false },
            icon = { Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Kalender Akademik Semester", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• 15 - 20 September: Penilaian Tengah Semester (PTS)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 20 - 24 Oktober: Asesmen Nasional Berbasis Komputer (ANBK)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 01 - 10 Desember: Penilaian Akhir Semester (PAS)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 19 Desember: Pembagian Rapor Semester Ganjil", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                }
            },
            confirmButton = {
                Button(onClick = { showCalendarDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateDialog = false },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Format Modul Ajar Merdeka", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Tersedia format modul ajar lengkap Fase A, B, C berstandar Kemendikbudristek 2025/2026 dalam format DOCX siap edit.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showTemplateDialog = false
                        Toast.makeText(context, "Template Modul Ajar terunduh ke perangkat", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Unduh Template (.DOCX)") }
            },
            dismissButton = {
                TextButton(onClick = { showTemplateDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showSwapDialog) {
        AlertDialog(
            onDismissRequest = { showSwapDialog = false },
            icon = { Icon(Icons.Default.SwapCalls, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Ajukan Tukar Jam Mengajar", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih rekan guru dan jadwal pengganti yang diinginkan. Sistem AI akan memvalidasi agar tidak terjadi bentrok ruangan/waktu.", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("Contoh: Tukar Matematika Senin Jam 1-2 dengan Rabu Jam 3-4 bersama Ibu Siti Aminah.", fontSize = 12.sp, color = Color(0xFF1D68E4))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSwapDialog = false
                        Toast.makeText(context, "Permohonan tukar jadwal terkirim ke Wakil Kepala Kurikulum", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Kirim Permohonan") }
            },
            dismissButton = {
                TextButton(onClick = { showSwapDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showUniformDialog) {
        AlertDialog(
            onDismissRequest = { showUniformDialog = false },
            icon = { Icon(Icons.Default.Checkroom, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Jadwal Seragam Sekolah Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• Senin: Putih Merah Lengkap (Topi & Dasi)", fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                    Text("• Selasa: Putih Merah Standar", fontSize = 12.sp, color = Color(0xFF0F172A))
                    Text("• Rabu: Kaos Olahraga Sekolah (saat jam PJOK)", fontSize = 12.sp, color = Color(0xFF0F172A))
                    Text("• Kamis: Seragam Batik Khas Sekolah", fontSize = 12.sp, color = Color(0xFF0F172A))
                    Text("• Jumat: Seragam Pramuka Siaga Lengkap / Busana Muslim", fontSize = 12.sp, color = Color(0xFF0F172A))
                }
            },
            confirmButton = {
                Button(onClick = { showUniformDialog = false }) { Text("Mengerti") }
            }
        )
    }

    if (showBellDialog) {
        AlertDialog(
            onDismissRequest = { showBellDialog = false },
            icon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Jadwal Bel & Jam Masuk Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• 06.45 WIB: Pintu Gerbang Dibuka & Guru Piket Menyambut", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 07.00 WIB: Bel Masuk (Upacara / Literasi Pagi / Senam)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                    Text("• 08.45 - 09.00 WIB: Istirahat Pertama (Snack Pagi)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 11.20 - 11.35 WIB: Istirahat Kedua (Makan Siang & Sholat)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 12.45 WIB: Bel Pulang Sekolah Seluruh Siswa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            },
            confirmButton = {
                Button(onClick = { showBellDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Kontak Tata Usaha Sekolah", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("SDN Pancasila 01 Pagi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("📍 Alamat: Jl. Pancasila No. 12, Jakarta", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("📞 Telepon TU: (021) 7890-1234", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("💬 WhatsApp Informasi: 0812-3456-7890", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                    Text("✉️ Email: info@sdnpancasila01.sch.id", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        Toast.makeText(context, "Membuka hotline WhatsApp sekolah", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) { Text("Chat WhatsApp TU") }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            icon = { Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Tata Tertib Siswa SDN Pancasila", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("1. Hadir di sekolah paling lambat pukul 06.50 WIB.", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("2. Mengenakan seragam lengkap sesuai jadwal harian.", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("3. Membawa bekal makanan sehat dan tumbler air minum.", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("4. Dilarang membawa mainan berbahaya atau benda tajam.", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("5. Menjaga kebersihan ruang kelas dan halaman sekolah.", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(onClick = { showRulesDialog = false }) { Text("Paham") }
            }
        )
    }

    if (showServerStatusDialog) {
        AlertDialog(
            onDismissRequest = { showServerStatusDialog = false },
            icon = { Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Status CSP Solver AI", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Status Node: ONLINE (Cluster Jakarta-1)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF15803D))
                    Text("• Rata-rata komputasi jadwal: 0.32 detik\n• Tingkat bentrok: 0.00% (Bebas Bentrok Murni)\n• Thread Solver: 8 Worker Aktif\n• Memori dialokasikan: 256 MB / 1024 MB", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(onClick = { showServerStatusDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showFlushCacheDialog) {
        AlertDialog(
            onDismissRequest = { showFlushCacheDialog = false },
            icon = { Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(32.dp)) },
            title = { Text("Kosongkan Redis Cache?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Operasi ini akan membersihkan seluruh antrean sementara dan cache memori query jadwal. Data primer sekolah tetap aman.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFlushCacheDialog = false
                        Toast.makeText(context, "Redis cache berhasil dibersihkan (0 keys remaining)", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) { Text("Bersihkan Sekarang") }
            },
            dismissButton = {
                TextButton(onClick = { showFlushCacheDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            icon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Unduh Cadangan Basis Data", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text("Berkas snapshot basis data lengkap (Guru, Kelas, Mapel, Rutinitas, Matriks Jadwal) siap diekspor dalam format JSON.", fontSize = 13.sp, color = Color(0xFF475569))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBackupDialog = false
                        Toast.makeText(context, "Cadangan Jadwale_Backup_All.json berhasil diunduh", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Unduh JSON") }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp)) },
            title = { Text("Keluar dari Akun?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = { Text("Pastikan perubahan jadwal draf Anda telah tersimpan ke server basis data sekolah. Apakah Anda yakin ingin keluar?", fontSize = 13.sp, color = Color(0xFF475569)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        Toast.makeText(context, "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Keluar Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { newRole ->
                showRoleDialog = false
                onSwitchRole(newRole)
            }
        )
    }
}

@Composable
private fun MenuGroupSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
        content()
    }
}

@Composable
private fun MenuItemCard(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    badgeColor: Color = Color(0xFFDBEAFE),
    badgeTextColor: Color = Color(0xFF1D4ED8),
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
