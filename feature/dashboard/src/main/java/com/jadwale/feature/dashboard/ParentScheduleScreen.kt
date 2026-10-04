package com.jadwale.feature.dashboard

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadwale.core.model.UserRole

data class ParentLessonSession(
    val timeTag: String,
    val timeBadge: String,
    val isFlag: Boolean,
    val title: String,
    val teacher: String?,
    val note: String,
    val noteIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val uniform: String = "Seragam Putih Merah Lengkap"
)

data class ParentBreakSession(
    val title: String,
    val desc: String,
    val duration: String,
    val isGreen: Boolean
)

sealed interface ParentTimelineItem {
    data class Lesson(val session: ParentLessonSession) : ParentTimelineItem
    data class Break(val session: ParentBreakSession) : ParentTimelineItem
}

/**
 * Layar Jadwal Siswa (Tampilan Orang Tua / Tamu Publik)
 * Sesuai mockup: UI JADWALE/Jadwal Siswa (Tampilan Orang Tua) - Jadwale.png
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentScheduleScreen(
    onLoginClick: () -> Unit = {},
    onSwitchRole: (UserRole) -> Unit = {},
    onNavigateToScheduleView: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedClass by remember { mutableStateOf("Kelas 3A — Bpk. Bambang Sutrisno, M.Pd") }
    var showClassDropdown by remember { mutableStateOf(false) }
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedLessonDetail by remember { mutableStateOf<ParentLessonSession?>(null) }
    var showDownloadSuccessDialog by remember { mutableStateOf(false) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var showUserProfileSheet by remember { mutableStateOf(false) }
    var showSchoolProfileSheet by remember { mutableStateOf(false) }
    var showUniformDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showHomeroomNoteDialog by remember { mutableStateOf(false) }

    val classes = listOf(
        "Kelas 1A — Ibu Siti Rahma, S.Pd",
        "Kelas 1B — Bpk. Ahmad Hidayat, M.Pd",
        "Kelas 2A — Ibu Dewi Lestari, S.Pd",
        "Kelas 2B — Bpk. Rahmat Santoso, S.Pd",
        "Kelas 3A — Bpk. Bambang Sutrisno, M.Pd",
        "Kelas 3B — Ibu Rina Kusuma, S.Pd",
        "Kelas 4A — Ibu Nurul Hidayati, S.Pd",
        "Kelas 4B — Bpk. Hendra Gunawan, S.Pd",
        "Kelas 5A — Ibu Sri Wahyuni, M.Pd",
        "Kelas 5B — Bpk. Joko Supriyanto, S.Pd",
        "Kelas 6A — Bpk. Budi Santoso, S.Pd",
        "Kelas 6B — Ibu Ratna Dewi, S.Pd"
    )

    val dayTabs = listOf(
        "Hari Ini\nSenin",
        "Besok\nSelasa",
        "Lusa\nRabu",
        "Sekolah\nKamis",
        "Sekolah\nJumat"
    )

    val classCode = when {
        selectedClass.contains("1A") -> "1A"
        selectedClass.contains("1B") -> "1B"
        selectedClass.contains("2A") -> "2A"
        selectedClass.contains("2B") -> "2B"
        selectedClass.contains("3A") -> "3A"
        selectedClass.contains("3B") -> "3B"
        selectedClass.contains("4A") -> "4A"
        selectedClass.contains("4B") -> "4B"
        selectedClass.contains("5A") -> "5A"
        selectedClass.contains("5B") -> "5B"
        selectedClass.contains("6A") -> "6A"
        selectedClass.contains("6B") -> "6B"
        else -> "3A"
    }
    val gradeNumber = classCode.firstOrNull()?.digitToIntOrNull() ?: 3
    val homeroomTeacherName = selectedClass.substringAfter("— ", "Bpk. Bambang Sutrisno, M.Pd").trim()

    val homeroomNote = when (gradeNumber) {
        1 -> when (selectedDayIndex) {
            0 -> "Membawa buku gambar A4 dan pensil warna 12 warna untuk Seni Rupa."
            1 -> "Bawa buku cerita bergambar untuk pojok baca literasi pagi."
            2 -> "Mengenakan seragam olahraga dari rumah dan membawa air minum tumbler."
            3 -> "Mengenakan pakaian batik sekolah dan membawa pensil 2B untuk latihan."
            else -> "Mengenakan seragam Pramuka Siaga lengkap + infaq Jumat berkah."
        }
        2 -> when (selectedDayIndex) {
            0 -> "Membawa gunting kertas berujung tumpul dan lem stik untuk kolase."
            1 -> "Bawa buku tulis garis tiga untuk latihan menulis tegak bersambung."
            2 -> "Seragam kaos olahraga lengkap + membawa handuk kecil dan air minum."
            3 -> "Mengenakan batik sekolah dan membawa kamus saku Bahasa Daerah."
            else -> "Seragam Pramuka Siaga lengkap + membawa botol minum sendiri."
        }
        3 -> when (selectedDayIndex) {
            0 -> "Bawa perlengkapan gambar ukuran A3 dan krayon untuk pelajaran SBdP jam ke-6."
            1 -> "Bawa daun kering dan wadah plastik kecil untuk observasi materi IPAS."
            2 -> "Pakaian seragam olahraga lengkap dari rumah + membawa botol minum sendiri."
            3 -> "Mengenakan seragam Batik Sekolah dan membawa kamus Bahasa Daerah."
            else -> "Mengenakan seragam Pramuka Siaga lengkap + membawa infaq Jumat berkah."
        }
        4 -> when (selectedDayIndex) {
            0 -> "Bawa busur derajat, jangka, dan penggaris 30 cm untuk Matematika."
            1 -> "Membawa modul IPAS Bab 2 'Wujud Zat dan Perubahannya'."
            2 -> "Kaos olahraga SDN Pancasila lengkap + membawa sepatu olahraga."
            3 -> "Mengenakan seragam batik dan membawa atlas peta provinsi."
            else -> "Seragam Pramuka Penggalang lengkap dengan kacu dan topi/baret."
        }
        5 -> when (selectedDayIndex) {
            0 -> "Membawa atlas peta Indonesia untuk materi IPAS Kenampakan Alam Nusantara."
            1 -> "Bawa modul Bahasa Indonesia Bab 3 tentang teks eksplanasi ilmiah."
            2 -> "Seragam olahraga sekolah lengkap + membawa bola kasti atau perlengkapan."
            3 -> "Mengenakan seragam batik sekolah + buku latihan soal matematika volume."
            else -> "Seragam Pramuka Penggalang lengkap + uang kas regu mingguan."
        }
        else -> when (selectedDayIndex) {
            0 -> "Membawa modul pengayaan persiapan Asesmen Standar Pendidikan Bab 1."
            1 -> "Bawa modul IPAS Tata Surya dan penggaris segitiga untuk Matematika."
            2 -> "Seragam olahraga lengkap + botol minum 1 liter untuk materi atletik."
            3 -> "Mengenakan batik sekolah dan membawa naskah pidato Bahasa Indonesia."
            else -> "Seragam Pramuka Penggalang Terap lengkap + portofolio bimbingan kelas."
        }
    }

    // Dynamic timeline items based on selected class and day tab
    val timelineItems: List<ParentTimelineItem> = buildParentTimeline(selectedClass, selectedDayIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1D68E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Jadwale Siswa",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Portal Orang Tua & Murid",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    // Quick Login as Teacher/Admin
                    TextButton(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1D68E4))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Masuk Staf", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    IconButton(onClick = { showNotifSheet = true }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
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
                            .background(Color(0xFF0F766E))
                            .clickable { showUserProfileSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Tamu",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. School Header & Verified Badge
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSchoolProfileSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF1D68E4),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SDN Pancasila 01 Pagi",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Tahun Ajaran 2025/2026 • Info Lengkap Sekolah",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Verified Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tautan Resmi Terverifikasi Sekolah",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Class Selector (Dropdown)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pilihan Rombel / Kelas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showClassDropdown = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedClass,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF475569)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showClassDropdown,
                                onDismissRequest = { showClassDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                classes.forEach { cls ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = cls,
                                                fontSize = 13.sp,
                                                fontWeight = if (cls == selectedClass) FontWeight.Bold else FontWeight.Normal,
                                                color = if (cls == selectedClass) Color(0xFF1D68E4) else Color(0xFF0F172A)
                                            )
                                        },
                                        onClick = {
                                            selectedClass = cls
                                            showClassDropdown = false
                                            Toast.makeText(context, "Menampilkan jadwal $cls", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }

                        // Quick 12-Class Chips
                        Text(
                            text = "Pilih Cepat Rombel (12 Kelas):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(classes.size) { idx ->
                                val cls = classes[idx]
                                val code = cls.substringBefore(" —").replace("Kelas ", "").trim()
                                val isSelected = cls == selectedClass
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF1D68E4) else Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedClass = cls
                                        Toast.makeText(context, "Menampilkan jadwal Kelas $code", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text(
                                        text = code,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Menampilkan jadwal aktif semester ganjil • $classCode",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // 3. Day Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dayTabs.size) { index ->
                        val isSelected = index == selectedDayIndex
                        val tabText = dayTabs[index]
                        val lines = tabText.split("\n")

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF1D68E4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .clickable { selectedDayIndex = index }
                                .width(68.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = lines[0],
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFFDBEAFE) else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lines[1],
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Catatan Wali Kelas Hari Ini
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHomeroomNoteDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1D68E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Catatan Wali Kelas Hari Ini",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = homeroomNote,
                                fontSize = 12.sp,
                                color = Color(0xFF1D4ED8),
                                lineHeight = 16.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color(0xFF93C5FD),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Quick Info Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showUniformDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Seragam", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { showCalendarDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kalender", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = { showContactDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kontak", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // 5. Susunan Jam Belajar Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Susunan Jam Belajar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "${timelineItems.size} Sesi Kegiatan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 6. Schedule Sessions
            items(timelineItems.size) { idx ->
                when (val item = timelineItems[idx]) {
                    is ParentTimelineItem.Lesson -> {
                        ParentSessionCard(
                            timeTag = item.session.timeTag,
                            timeBadge = item.session.timeBadge,
                            isFlag = item.session.isFlag,
                            title = item.session.title,
                            room = null,
                            teacher = item.session.teacher,
                            note = item.session.note,
                            noteIcon = item.session.noteIcon,
                            onClick = { selectedLessonDetail = item.session }
                        )
                    }
                    is ParentTimelineItem.Break -> {
                        ParentBreakCard(
                            title = item.session.title,
                            desc = item.session.desc,
                            duration = item.session.duration,
                            isGreen = item.session.isGreen,
                            onClick = {
                                Toast.makeText(context, "${item.session.title}: ${item.session.desc}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // 7. Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            showDownloadSuccessDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan / Unduh Jadwal Ini (PDF Gambar)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val dayName = when (selectedDayIndex) {
                                0 -> "Senin"
                                1 -> "Selasa"
                                2 -> "Rabu"
                                3 -> "Kamis"
                                else -> "Jumat"
                            }
                            val shareText = "Jadwal Pelajaran $selectedClass (Hari $dayName):\n" +
                                timelineItems.mapNotNull {
                                    if (it is ParentTimelineItem.Lesson) "- ${it.session.timeTag}: ${it.session.title} (${it.session.note})" else null
                                }.joinToString("\n") +
                                "\n\nCatatan Wali Kelas: $homeroomNote\n\nInformasi resmi SDN Pancasila 01 Pagi: https://jadwale.kemdikbud.go.id"

                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan ke WhatsApp Wali Murid"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF15803D)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan ke Grup WhatsApp Wali Murid", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // 8. Info Footer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Bila ada pertanyaan terkait penyesuaian jam atau seragam, silakan hubungi langsung pihak tata usaha sekolah atau wali kelas.",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aplikasi Jadwale v3.1 • Informasi resmi SDN Pancasila 01 Pagi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "Akses Tamu Publik • Tanpa Perlu Masuk Akun",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // 9. Quick Switch Role Link
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
                        Text("Beralih ke Peran Lain (Admin / Guru / Super Admin)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D68E4))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDownloadSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadSuccessDialog = false },
            icon = { Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Jadwal Berhasil Diunduh", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    text = "Berkas Jadwal_${selectedClass.take(8).replace(" ", "_")}.pdf telah tersimpan di memori perangkat Anda dalam format siap cetak.",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDownloadSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    if (selectedLessonDetail != null) {
        val detail = selectedLessonDetail!!
        AlertDialog(
            onDismissRequest = { selectedLessonDetail = null },
            icon = { Icon(detail.noteIcon, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(28.dp)) },
            title = { Text(detail.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1D68E4))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Waktu: ${detail.timeTag} (${detail.timeBadge})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    }
                    if (detail.teacher != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guru Pengampu: ${detail.teacher}", fontSize = 12.sp, color = Color(0xFF334155))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF15803D))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Seragam: ${detail.uniform}", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Perlengkapan / Catatan: ${detail.note}", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedLessonDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Tutup Detail")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Pengingat untuk ${detail.title} disetel pada perangkat", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Setel Pengingat")
                }
            }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = UserRole.UMUM,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { role ->
                showRoleDialog = false
                onSwitchRole(role)
            }
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
            currentRole = UserRole.UMUM,
            userName = "Tamu / Orang Tua Siswa",
            userAccount = "Akses Tamu Publik SDN Pancasila 01",
            schoolName = "SDN Pancasila 01 Pagi",
            onDismiss = { showUserProfileSheet = false },
            onOpenSchoolProfile = {
                showUserProfileSheet = false
                showSchoolProfileSheet = true
            },
            onOpenRoleSwitcher = {
                showUserProfileSheet = false
                showRoleDialog = true
            },
            onOpenHelpCenter = {
                showUserProfileSheet = false
                showContactDialog = true
            },
            onLogout = onLoginClick
        )
    }

    if (showSchoolProfileSheet) {
        SchoolProfileBottomSheet(
            onDismiss = { showSchoolProfileSheet = false }
        )
    }

    if (showUniformDialog) {
        AlertDialog(
            onDismissRequest = { showUniformDialog = false },
            icon = { Icon(Icons.Default.Checkroom, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Jadwal Seragam Siswa Mingguan", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                Button(
                    onClick = { showUniformDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) { Text("Paham") }
            }
        )
    }

    if (showCalendarDialog) {
        AlertDialog(
            onDismissRequest = { showCalendarDialog = false },
            icon = { Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Kalender Akademik & Hari Libur", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• 15 - 20 September: Penilaian Tengah Semester (PTS)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 20 - 24 Oktober: Asesmen Nasional Berbasis Komputer (ANBK)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 01 - 10 Desember: Penilaian Akhir Semester (PAS)", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("• 19 Desember: Pembagian Rapor Semester Ganjil", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCalendarDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Tutup") }
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
                        Toast.makeText(context, "Membuka WhatsApp Tata Usaha...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                ) { Text("Chat WhatsApp TU") }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showHomeroomNoteDialog) {
        AlertDialog(
            onDismissRequest = { showHomeroomNoteDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(32.dp)) },
            title = { Text("Pengumuman Wali Kelas", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(selectedClass, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1D68E4))
                    Text(homeroomNote, fontSize = 13.sp, color = Color(0xFF0F172A), lineHeight = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pesan ini disiarkan secara resmi oleh wali kelas kepada seluruh orang tua siswa rombel.", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHomeroomNoteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Dimengerti") }
            }
        )
    }
}

@Composable
private fun ParentSessionCard(
    timeTag: String,
    timeBadge: String,
    isFlag: Boolean,
    title: String,
    room: String?,
    teacher: String?,
    note: String,
    noteIcon: androidx.compose.ui.graphics.vector.ImageVector,
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
                    color = if (isFlag) Color(0xFFFEE2E2) else Color(0xFFEFF6FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isFlag) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = timeTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFlag) Color(0xFFDC2626) else Color(0xFF1D68E4)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = timeBadge,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            if (teacher != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = teacher,
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = noteIcon,
                    contentDescription = null,
                    tint = Color(0xFF1D68E4),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = note,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}

@Composable
private fun ParentBreakCard(
    title: String,
    desc: String,
    duration: String,
    isGreen: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGreen) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGreen) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isGreen) Color(0xFF16A34A) else Color(0xFF94A3B8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGreen) Icons.Default.Restaurant else Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGreen) Color(0xFF14532D) else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        fontSize = 11.sp,
                        color = if (isGreen) Color(0xFF166534) else Color(0xFF64748B)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White
            ) {
                Text(
                    text = duration,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGreen) Color(0xFF166534) else Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun buildParentTimeline(
    selectedClass: String,
    selectedDayIndex: Int
): List<ParentTimelineItem> {
    val classCode = when {
        selectedClass.contains("1A") -> "1A"
        selectedClass.contains("1B") -> "1B"
        selectedClass.contains("2A") -> "2A"
        selectedClass.contains("2B") -> "2B"
        selectedClass.contains("3A") -> "3A"
        selectedClass.contains("3B") -> "3B"
        selectedClass.contains("4A") -> "4A"
        selectedClass.contains("4B") -> "4B"
        selectedClass.contains("5A") -> "5A"
        selectedClass.contains("5B") -> "5B"
        selectedClass.contains("6A") -> "6A"
        selectedClass.contains("6B") -> "6B"
        else -> "3A"
    }

    val grade = classCode.firstOrNull()?.digitToIntOrNull() ?: 3
    val homeroomTeacher = selectedClass.substringAfter("— ", "Bpk. Bambang Sutrisno, M.Pd").trim()
    val room = "Ruang Kelas $classCode"

    return when (selectedDayIndex) {
        0 -> { // Senin
            val mapel1 = when (grade) {
                1, 2 -> "Pendidikan Pancasila"
                3, 4 -> "Matematika"
                else -> "IPAS (Sains & Sosial)"
            }
            val mapel1Teacher = when (grade) {
                1, 2 -> homeroomTeacher
                3, 4 -> homeroomTeacher
                else -> "Ibu Siti Rahma, S.Pd"
            }
            val mapel1Note = when (grade) {
                1, 2 -> "Materi: Simbol Garuda Pancasila & Nilai Kebangsaan"
                3, 4 -> "Membawa Buku Paket Matematika Bab 3 & Penggaris"
                else -> "Materi: Sistem Organ Tubuh Manusia & Modul IPA"
            }

            val mapel2 = when (grade) {
                1 -> "Bahasa Indonesia (Mengenal Huruf)"
                2 -> "Bahasa Indonesia (Membaca Cerita)"
                3 -> "Bahasa Indonesia"
                4 -> "Bahasa Indonesia (Ide Pokok Cerita)"
                5 -> "Matematika (Pecahan Campuran)"
                else -> "Matematika (Operasi Bilangan Bulat)"
            }
            val mapel2Teacher = when (grade) {
                1, 2 -> homeroomTeacher
                3, 4 -> "Ibu Siti Aminah, S.Pd"
                5 -> "Ibu Sri Wahyuni, M.Pd"
                else -> "Bpk. Budi Santoso, S.Pd"
            }
            val mapel2Note = when (grade) {
                1, 2 -> "Latihan mengeja suku kata dan menulis di buku bergaris"
                3, 4 -> "Materi: Membaca & Menulis Cerita Rakyat Nusantara"
                else -> "Membawa buku berpetak dan penggaris 30 cm"
            }

            val mapel3 = when (grade) {
                1, 2 -> "Seni Rupa (Bentuk & Warna)"
                3, 4 -> "Seni Budaya & Prakarya"
                else -> "Seni Rupa & Kriya Nusantara"
            }

            listOf(
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.00 - 07.35", "35 Menit", true, "Upacara Bendera", null, "Pakaian Seragam Putih Merah Lengkap + Topi", Icons.Default.CheckCircle, "Seragam Putih Merah + Topi + Dasi")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.35 - 08.45 (2 JP)", room, false, mapel1, mapel1Teacher, mapel1Note, Icons.AutoMirrored.Filled.MenuBook, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat Pagi", "Makan snack sehat di kelas atau kantin", "08.45 - 09.00\n(15 Menit)", true)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("09.00 - 10.10 (2 JP)", room, false, mapel2, mapel2Teacher, mapel2Note, Icons.AutoMirrored.Filled.MenuBook, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("10.10 - 11.20 (2 JP)", "Musholla / $room", false, "Pendidikan Agama Islam", "Bpk. Ahmad Fauzi, S.Ag", "Bawa perlengkapan ibadah (mukena / peci)", Icons.Default.CalendarToday, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat Siang", "Minum air & cuci tangan sebelum jam prakarya", "11.20 - 11.35", false)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("11.35 - 12.45 (2 JP)", "Ruang Kesenian / $room", false, mapel3, "Ibu Ratna Dewi, S.Pd", "Praktek mewarnai gradasi krayon buku A3", Icons.Default.Edit, "Seragam Putih Merah")
                )
            )
        }
        1 -> { // Selasa
            val mapel1 = when (grade) {
                1, 2 -> "Matematika"
                3, 4 -> "Pendidikan Pancasila"
                else -> "Bahasa Indonesia"
            }
            val mapel1Note = when (grade) {
                1, 2 -> "Mengenal lambang bilangan 1-50 & penjumlahan"
                3, 4 -> "Materi: Hak dan Kewajiban Anak di Rumah"
                else -> "Materi: Menganalisis Teks Eksplanasi Ilmiah"
            }

            val mapel2 = when (grade) {
                1, 2 -> "Bahasa Indonesia"
                3, 4 -> "Matematika"
                else -> "IPAS (Sains & Sosial)"
            }
            val mapel2Note = when (grade) {
                1, 2 -> "Membaca nyaring dan menulis tegak bersambung"
                3, 4 -> "Latihan Soal Cerita Perkalian Bersusun"
                else -> "Materi: Ekosistem Alam & Jaring Makanan"
            }

            val mapel3 = when (grade) {
                1, 2 -> "Pendidikan Karakter & Budi Pekerti"
                3, 4 -> "IPAS (Sains & Sosial)"
                else -> "Pendidikan Pancasila"
            }
            val mapel3Teacher = when (grade) {
                1, 2 -> homeroomTeacher
                3, 4 -> "Ibu Siti Rahma, S.Pd"
                else -> homeroomTeacher
            }

            listOf(
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.00 - 07.15", "15 Menit", false, "Literasi Pagi & Doa", null, "Membaca Senyap 15 Menit di Pojok Baca $room", Icons.AutoMirrored.Filled.MenuBook, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.15 - 08.25 (2 JP)", room, false, mapel1, homeroomTeacher, mapel1Note, Icons.Default.MenuBook, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("08.25 - 09.35 (2 JP)", room, false, mapel2, homeroomTeacher, mapel2Note, Icons.AutoMirrored.Filled.MenuBook, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat Pagi", "Makan snack sehat & istirahat sejenak", "09.35 - 09.50\n(15 Menit)", true)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("09.50 - 11.00 (2 JP)", "Ruang Bahasa / $room", false, "Bahasa Inggris", "Ibu Dewi Lestari, S.Pd", "Unit 3: Daily Activities & Vocabulary Kelas $grade", Icons.Default.Language, "Seragam Putih Merah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("11.00 - 12.10 (2 JP)", "Lab IPA / $room", false, mapel3, mapel3Teacher, "Pengamatan Wujud Benda & Diskusi Kelompok", Icons.Default.Science, "Seragam Putih Merah")
                )
            )
        }
        2 -> { // Rabu
            val pjokDesc = when (grade) {
                1, 2 -> "Gerak Dasar Lokomotor & Kebugaran Anak"
                3, 4 -> "Gerak Dasar Lokomotor, Senam & Kebugaran"
                else -> "Permainan Bola Voli Mini, Kasti & Atletik"
            }

            val mapelRabu = when (grade) {
                1, 2 -> "Bahasa Indonesia (Membaca Mandiri)"
                3, 4 -> "Bahasa Indonesia (Menyusun Paragraf Cerita)"
                else -> "Matematika (Perbandingan & Skala)"
            }

            listOf(
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.00 - 07.15", "15 Menit", false, "Pembiasaan Karakter Pagi", null, "Menyanyikan Lagu Indonesia Raya & Doa", Icons.Default.Stars, "Kaos Olahraga Lengkap")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.15 - 08.55 (3 JP)", "Lapangan Sekolah", false, "PJOK / Olahraga", "Bpk. Joko Supriyanto, S.Pd", "Materi: $pjokDesc", Icons.Default.FitnessCenter, "Kaos Olahraga Sekolah")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat & Ganti Baju", "Minum air putih & berganti seragam", "08.55 - 09.15\n(20 Menit)", true)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("09.15 - 10.25 (2 JP)", room, false, mapelRabu, homeroomTeacher, "Latihan membaca dan menulis mandiri", Icons.AutoMirrored.Filled.MenuBook, "Seragam Olahraga / Putih Merah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("10.25 - 11.35 (2 JP)", "Ruang Kesenian / $room", false, "Seni Musik", "Ibu Ratna Dewi, S.Pd", "Membawa Pianika / Rekorder & Belajar Not Balok", Icons.Default.MusicNote, "Seragam Sekolah")
                )
            )
        }
        3 -> { // Kamis
            val ipasKamis = when (grade) {
                1, 2 -> "Muatan Lokal Keterampilan"
                3, 4 -> "IPAS (Sosial)"
                else -> "IPAS (Sejarah Nusantara)"
            }
            val mathKamis = when (grade) {
                1, 2 -> "Matematika (Mengenal Pola Bentuk)"
                3, 4 -> "Matematika (Satuan Panjang & Berat)"
                else -> "Matematika (Volume Bangun Ruang)"
            }

            listOf(
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.00 - 07.15", "15 Menit", false, "Kamis Budaya Pagi", null, "Menyapa & Menyanyi Lagu Daerah", Icons.Default.LibraryMusic, "Seragam Batik Sekolah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.15 - 08.25 (2 JP)", room, false, "Bahasa Daerah", "Ibu Nurul Hidayati, S.Pd", "Materi: Undak Usuk Basa & Percakapan Santun", Icons.Default.Translate, "Seragam Batik Sekolah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("08.25 - 09.35 (2 JP)", room, false, ipasKamis, "Ibu Siti Rahma, S.Pd", "Materi: Pengamatan Lingkungan Alam Sekitar", Icons.Default.Public, "Seragam Batik Sekolah")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat Pagi", "Makan bekal bersama teman di kelas", "09.35 - 09.50\n(15 Menit)", true)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("09.50 - 11.00 (2 JP)", room, false, mathKamis, homeroomTeacher, "Latihan soal dan perhitungan terstruktur", Icons.AutoMirrored.Filled.MenuBook, "Seragam Batik Sekolah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("11.00 - 12.10 (2 JP)", room, false, "Pendidikan Pancasila", homeroomTeacher, "Materi: Nilai Gotong Royong di Sekolah", Icons.Default.Groups, "Seragam Batik Sekolah")
                )
            )
        }
        else -> { // Jumat
            val pramukaTitle = if (grade <= 3) "Pramuka Siaga Wajib" else "Pramuka Penggalang Wajib"
            val pramukaDesc = if (grade <= 3) "Latihan PBB Dasar, Semaphore Ceria & Dwi Darma" else "Latihan Semaphore, Tali Temali & Dasa Darma"
            val seragamPramuka = if (grade <= 3) "Seragam Pramuka Siaga Lengkap" else "Seragam Pramuka Penggalang Lengkap"

            listOf(
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.00 - 07.35", "35 Menit", false, "Senam Pagi SKJ Bersama", null, "Senam Bersama Seluruh Siswa & Guru di Lapangan", Icons.Default.DirectionsRun, "Baju Olahraga Sekolah")
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("07.35 - 08.45 (2 JP)", "Musholla / $room", false, "Pendidikan Agama & Budi Pekerti", "Bpk. Ahmad Fauzi, S.Ag", "Materi: Akhlak Terpuji & Pembiasaan Sholat Dhuha", Icons.Default.Mosque, "Baju Muslim / Seragam Pramuka")
                ),
                ParentTimelineItem.Break(
                    ParentBreakSession("Waktu Istirahat Pagi", "Makan snack sehat & minum air", "08.45 - 09.00\n(15 Menit)", true)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("09.00 - 10.10 (2 JP)", "Halaman Sekolah", false, pramukaTitle, "Pembina Pramuka SD", pramukaDesc, Icons.Default.EmojiFlags, seragamPramuka)
                ),
                ParentTimelineItem.Lesson(
                    ParentLessonSession("10.10 - 10.45", "35 Menit", false, "Apel & Evaluasi Wali Kelas", homeroomTeacher, "Pengumuman akhir pekan & doa penutupan", Icons.Default.Campaign, seragamPramuka)
                )
            )
        }
    }
}
