package com.jadwale.feature.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

/**
 * Layar Dashboard Guru Pengampu
 * Persona: Bpk. Bambang Sutrisno, M.Pd (Wali Kelas 3A)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    data: DashboardData,
    teacherName: String = "Bpk. Bambang Sutrisno, M.Pd",
    onNavigateToScheduleView: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resolvedTeacherName = if (teacherName != "Bpk. Bambang Sutrisno, M.Pd" && teacherName.isNotBlank()) teacherName
        else com.jadwale.core.model.UserProfileConfig.getNameForRole(UserRole.GURU)
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedTeachingSlot by remember { mutableStateOf<TeacherTeachingSlotData?>(null) }
    var showSkDialog by remember { mutableStateOf(false) }
    var showMetricDialog by remember { mutableStateOf<String?>(null) }
    var isGuestLinkEnabled by remember { mutableStateOf(true) }
    var currentSchoolName by remember {
        mutableStateOf(
            com.jadwale.core.session.SessionStore.current?.schoolName
                ?: com.jadwale.core.model.SchoolConfig.schoolName
        )
    }
    var showSchoolSelectDialog by remember { mutableStateOf(false) }

    val days = listOf("Senin (6 JP)", "Selasa (4 JP)", "Rabu (4 JP)", "Kamis (4 JP)", "Jumat (3 JP)", "Sabtu (Refleksi)")

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
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "GURU",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Portal Jadwal & Presensi Guru",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifSheet = true }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
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
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1D68E4),
                        selectedTextColor = Color(0xFF1D68E4),
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToScheduleView,
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Jadwal") },
                    label = { Text("Jadwal", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMenu,
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                    label = { Text("Menu", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. Teacher Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF1D68E4),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = resolvedTeacherName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "NIP 19820315 200801 1 008 • Wali Kelas 1A",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Sekolah: $currentSchoolName",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E40AF),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                TextButton(
                                    onClick = { showSchoolSelectDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ganti", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                                }
                            }
                        }
                    }
                }
            }

            // 2. Summary Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherMetricCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMetricDialog = "Rincian Beban Mengajar:\n• 18 JP Tatap Muka (Matematika & Wali Kelas)\n• 6 JP Ekuivalensi Tugas Tambahan\n• Total 24 JP: Memenuhi batas minimal sertifikasi Dapodik."
                            },
                        title = "Beban Mengajar",
                        value = "18 JP",
                        subtitle = "Target Min. 18 JP",
                        icon = Icons.Default.AccessTime,
                        iconColor = Color(0xFF1D68E4),
                        iconBg = Color(0xFFEFF6FF)
                    )
                    TeacherMetricCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMetricDialog = "Daftar Rombongan Belajar:\n• Kelas 1A: Wali Kelas & Matematika (28 Siswa)\n• Kelas 1B: Pengampu Matematika (28 Siswa)\n• Kelas 2A: Pengampu Matematika (29 Siswa)\n• Kelas 2B: Pengampu Matematika (29 Siswa)\nTotal 114 siswa binaan."
                            },
                        title = "Rombel Ajar",
                        value = "4 Kelas",
                        subtitle = "1A, 1B, 2A, 2B",
                        icon = Icons.Default.MeetingRoom,
                        iconColor = Color(0xFF16A34A),
                        iconBg = Color(0xFFDCFCE7)
                    )
                    TeacherMetricCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showMetricDialog = "Status Algoritma Jadwal:\nAlgoritma CSP v2.4 menjamin 100% bebas bentrok guru, ruangan kelas, dan waktu mengajar."
                            },
                        title = "Jadwal Bentrok",
                        value = "0 Jam",
                        subtitle = "Bebas Konflik 100%",
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFFD97706),
                        iconBg = Color(0xFFFEF3C7)
                    )
                }
            }

            // 3. Quick Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToScheduleView,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Jadwal Sekolah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showSkDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D68E4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D68E4)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unduh SK Tugas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3b. Fitur Khusus Wali Kelas: Buat Link View Guest untuk Orang Tua
            item {
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
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Tautan Guest View Kelas", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                            Text("WALI KELAS 1A", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("Bagi orang tua / wali murid untuk lihat jadwal tanpa login", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            Switch(
                                checked = isGuestLinkEnabled,
                                onCheckedChange = {
                                    isGuestLinkEnabled = it
                                    Toast.makeText(context, if (it) "Tautan publik guest diaktifkan" else "Tautan publik dinonaktifkan", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF1D68E4))
                            )
                        }

                        if (isGuestLinkEnabled) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "https://jadwale.id/guest/sdn-pancasila-01/kelas-1a",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D4ED8),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Link Guest Jadwal Kelas 1A", "https://jadwale.id/guest/sdn-pancasila-01/kelas-1a")
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "✓ Tautan Guest Kelas 1A berhasil disalin!", Toast.LENGTH_SHORT).show()
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Salin Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Day Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jadwal Mengajar Harian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        TextButton(
                            onClick = onNavigateToScheduleView,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1D68E4))
                        ) {
                            Text("Full Grid", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(days.size) { index ->
                            val isSelected = index == selectedDayIndex
                            Surface(
                                onClick = { selectedDayIndex = index },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF1D68E4) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF1D68E4) else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = days[index],
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Teaching Sessions List (Dynamic based on selected day tab)
            val currentDaySlots = when (selectedDayIndex) {
                0 -> listOf( // Senin (6 JP)
                    TeacherTeachingSlotData("07.35 - 08.45", "2 JP", "Matematika", "Ruang Kelas 1A", "Materi: Pengenalan Angka & Penjumlahan Dasar"),
                    TeacherTeachingSlotData("09.00 - 10.10", "2 JP", "Matematika", "Ruang Kelas 1B", "Materi: Konsep Satuan & Puluhan Interaktif"),
                    TeacherTeachingSlotData("11.35 - 12.45", "2 JP", "Matematika", "Ruang Kelas 2A", "Materi: Operasi Hitung Perkalian Dasar")
                )
                1 -> listOf( // Selasa (4 JP)
                    TeacherTeachingSlotData("07.35 - 08.45", "2 JP", "Matematika", "Ruang Kelas 2B", "Materi: Latihan Soal Cerita Perkalian Bersusun"),
                    TeacherTeachingSlotData("10.10 - 11.20", "2 JP", "Matematika", "Ruang Kelas 1A", "Materi: Penjumlahan Bilangan Bergambar")
                )
                2 -> listOf( // Rabu (4 JP)
                    TeacherTeachingSlotData("07.35 - 08.45", "2 JP", "Matematika", "Ruang Kelas 1B", "Materi: Pengenalan Bentuk Bangun Datar"),
                    TeacherTeachingSlotData("09.00 - 10.10", "2 JP", "Bimbingan Wali Kelas", "Ruang Kelas 1A", "Materi: Pembinaan Karakter, Disiplin & Portofolio")
                )
                3 -> listOf( // Kamis (4 JP)
                    TeacherTeachingSlotData("07.35 - 08.45", "2 JP", "Matematika", "Ruang Kelas 2A", "Materi: Latihan Pengurangan Bersusun"),
                    TeacherTeachingSlotData("10.10 - 11.20", "2 JP", "Matematika", "Ruang Kelas 2B", "Materi: Pengenalan Satuan Baku Panjang")
                )
                else -> listOf( // Jumat (2 JP + Refleksi)
                    TeacherTeachingSlotData("07.35 - 08.45", "2 JP", "Matematika", "Ruang Kelas 1A", "Materi: Evaluasi Formatif & Kuis Interaktif"),
                    TeacherTeachingSlotData("08.45 - 09.30", "1 Jam", "Refleksi Wali Kelas", "Ruang Kelas 1A", "Materi: Rekap Presensi Mingguan & Koordinasi Wali Murid")
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentDaySlots.forEach { slot ->
                        TeacherTeachingSlotCard(
                            time = slot.time,
                            jpBadge = slot.jpBadge,
                            subject = slot.subject,
                            room = slot.room,
                            notes = slot.notes,
                            onClick = { selectedTeachingSlot = slot }
                        )
                    }
                }
            }

            // 6. Role Switcher Link
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRoleDialog = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Beralih ke Peran Lain (Admin / Super Admin / Tamu Publik)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D68E4))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            currentRole = UserRole.GURU,
            userName = resolvedTeacherName,
            userAccount = "guru@sdnpancasila.sch.id (NIP 19820315 200801 1 008)",
            schoolName = "SDN Pancasila 01 Pagi",
            onDismiss = { showProfileSheet = false },
            onOpenSchoolProfile = { showProfileSheet = false },
            onOpenRoleSwitcher = {
                showProfileSheet = false
                showRoleDialog = true
            },
            onOpenHelpCenter = { showProfileSheet = false },
            onLogout = {
                showProfileSheet = false
                onLogout()
            }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = UserRole.GURU,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { role ->
                showRoleDialog = false
                onSwitchRole(role)
            }
        )
    }

    if (selectedTeachingSlot != null) {
        val slot = selectedTeachingSlot!!
        AlertDialog(
            onDismissRequest = { selectedTeachingSlot = null },
            icon = { Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(28.dp)) },
            title = { Text("${slot.subject} (${slot.jpBadge})", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1D68E4))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Waktu: ${slot.time}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF15803D))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ruang: ${slot.room}", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(slot.notes, fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val room = slot.room
                        selectedTeachingSlot = null
                        Toast.makeText(context, "Jurnal & Presensi $room berhasil disimpan", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Isi Jurnal & Presensi")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTeachingSlot = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (showSkDialog) {
        AlertDialog(
            onDismissRequest = { showSkDialog = false },
            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("SK Pembagian Beban Tugas", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nomor: 421.2/045/SDN-01/VII/2025", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text("Nama: $teacherName", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Jam Mengajar: 18 JP\n• Tugas Tambahan: 6 JP\n• Total: 24 JP (Memenuhi Syarat Sertifikasi)", fontSize = 12.sp, color = Color(0xFF334155))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkDialog = false
                        Toast.makeText(context, "✓ Berhasil mengunduh SK_Tugas_${teacherName.take(7)}.pdf", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Unduh PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (showMetricDialog != null) {
        AlertDialog(
            onDismissRequest = { showMetricDialog = null },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(28.dp)) },
            title = { Text("Informasi Beban Guru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(showMetricDialog ?: "", fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp)
            },
            confirmButton = {
                Button(onClick = { showMetricDialog = null }) {
                    Text("Paham")
                }
            }
        )
    }

    if (showSchoolSelectDialog) {
        TeacherSchoolSelectionDialog(
            initialSchoolName = currentSchoolName,
            canDismiss = true,
            onDismiss = { showSchoolSelectDialog = false },
            onSchoolSelected = { newSchool, _ ->
                currentSchoolName = newSchool
                showSchoolSelectDialog = false
            }
        )
    }
}

@Composable
private fun TeacherMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
            Text(text = subtitle, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF15803D))
        }
    }
}

data class TeacherTeachingSlotData(
    val time: String,
    val jpBadge: String,
    val subject: String,
    val room: String,
    val notes: String
)

@Composable
private fun TeacherTeachingSlotCard(
    time: String,
    jpBadge: String,
    subject: String,
    room: String,
    notes: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = jpBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = subject,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = room, fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = notes, fontSize = 11.sp, color = Color(0xFF64748B))
            }
        }
    }
}
