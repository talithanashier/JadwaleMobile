package com.jadwale.feature.schedule_view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.jadwale.core.model.DayOfWeek
import com.jadwale.core.model.ScheduleSlot
import com.jadwale.feature.schedule_view.components.ExportPrintBottomSheet
import com.jadwale.feature.schedule_view.components.ScheduleDetailBottomSheet
import com.jadwale.feature.schedule_view.components.SubjectColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewScreen(
    viewModel: ScheduleViewViewModel,
    userRole: com.jadwale.core.model.UserRole = com.jadwale.core.model.UserRole.ADMIN_SEKOLAH,
    userName: String? = null,
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onNavigateToEdit: (classId: String) -> Unit = {},
    onNavigateToGenerator: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDay by remember { mutableStateOf(DayOfWeek.SENIN) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var selectedSuperAdminSchool by remember { mutableStateOf("SDN Pancasila 01") }
    var showSchoolPickerDropdown by remember { mutableStateOf(false) }
    var selectedGradeFilter by remember { mutableIntStateOf(0) } // 0: Semua (1-6), 1..6: Kelas 1..6
    var matrixViewMode by remember { mutableIntStateOf(0) } // 0: Kartu Rombel, 1: Tabel Matriks Grid

    val monitoredSchools = listOf(
        "SDN Percobaan 01",
        "SDN Pancasila 01",
        "SDN Menteng 02",
        "SDN Cibubur 03",
        "SDN Rawamangun 12"
    )

    LaunchedEffect(userRole, userName, com.jadwale.core.model.SchoolConfig.isParallel) {
        viewModel.setRole(userRole, userName)
        viewModel.loadSchedule()
    }

    val days = if (com.jadwale.core.model.SchoolConfig.daysCount == 6) {
        listOf(
            DayOfWeek.SENIN,
            DayOfWeek.SELASA,
            DayOfWeek.RABU,
            DayOfWeek.KAMIS,
            DayOfWeek.JUMAT,
            DayOfWeek.SABTU
        )
    } else {
        listOf(
            DayOfWeek.SENIN,
            DayOfWeek.SELASA,
            DayOfWeek.RABU,
            DayOfWeek.KAMIS,
            DayOfWeek.JUMAT
        )
    }

    // Proteksi: Superadmin GABISA AKSES MENU JADWAL KARENA TUGASNYA MANAJEMEN
    if (userRole == com.jadwale.core.model.UserRole.SUPER_ADMIN) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Akses Terbatas", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToDashboard) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali ke Dashboard")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = Color(0xFF1D68E4),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Superadmin Tidak Memiliki Jadwal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Sesuai regulasi sistem, peran Super Administrator murni bertugas mengelola semua sekolah, verifikasi akun (.sch.id), manajemen modul, audit log, dan cadangan database.\n\nHalaman penyusunan dan penayangan jadwal tidak dapat diakses oleh Superadmin.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = onNavigateToDashboard,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kembali ke Manajemen Sekolah", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        return
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
                                text = when (userRole) {
                                    com.jadwale.core.model.UserRole.GURU -> "Jadwal Mengajar Guru Pendidik"
                                    com.jadwale.core.model.UserRole.UMUM -> "Jadwal Pelajaran Siswa ($selectedSuperAdminSchool)"
                                    com.jadwale.core.model.UserRole.SUPER_ADMIN -> "Supervisi Wilayah: $selectedSuperAdminSchool"
                                    com.jadwale.core.model.UserRole.ADMIN_SEKOLAH -> "Jadwal Pelajaran Kelas & Guru"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotifDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifikasi",
                            tint = Color(0xFF334155)
                        )
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
        },
        bottomBar = {
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
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Jadwal") },
                    label = { Text("Jadwal", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = Color(0xFF1D68E4),
                        selectedIconColor = Color(0xFF1D68E4)
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ScheduleViewUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF1D68E4))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Memuat jadwal pelajaran...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
                is ScheduleViewUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Terjadi Kesalahan",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.loadSchedule() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                ) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                }
                is ScheduleViewUiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { viewModel.refreshSchedule() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val activeClass = state.classes.getOrNull(state.selectedClassIndex)
                        val daySlots: List<ScheduleSlot> = state.filteredSlots
                            .filter { it.day == selectedDay }
                            .sortedBy { it.timeSlot.startTime }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // 0. Super Admin Multi-School Supervisor Header Banner
                            if (userRole == com.jadwale.core.model.UserRole.SUPER_ADMIN) {
                                item {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFF1D68E4)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Apartment,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = "Supervisi Pengawas Wilayah (Multi-Sekolah)",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1D68E4)
                                                    )
                                                    Text(
                                                        text = selectedSuperAdminSchool,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E1B4B)
                                                    )
                                                }
                                            }

                                            Box {
                                                Button(
                                                    onClick = { showSchoolPickerDropdown = true },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    modifier = Modifier.height(32.dp)
                                                ) {
                                                    Text("Pilih Sekolah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }

                                                DropdownMenu(
                                                    expanded = showSchoolPickerDropdown,
                                                    onDismissRequest = { showSchoolPickerDropdown = false }
                                                ) {
                                                    monitoredSchools.forEach { sch ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(
                                                                    text = sch,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = if (sch == selectedSuperAdminSchool) FontWeight.Bold else FontWeight.Normal,
                                                                    color = if (sch == selectedSuperAdminSchool) Color(0xFF1D68E4) else Color(0xFF0F172A)
                                                                )
                                                            },
                                                            onClick = {
                                                                selectedSuperAdminSchool = sch
                                                                showSchoolPickerDropdown = false
                                                                viewModel.refreshSchedule()
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 1. Segmented Control: Per Kelas | Per Guru | 12 Kelas
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFE2E8F0))
                                        .padding(3.dp)
                                ) {
                                    val segments = listOf("Per Kelas", "Per Guru", "Matriks Rombel")
                                    segments.forEachIndexed { index, title ->
                                        val isSelected = state.selectedSegment == index
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color.White else Color.Transparent)
                                                .clickable {
                                                    viewModel.selectSegment(index)
                                                }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = title,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF1D68E4) else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Selectors based on Segment
                            if (state.selectedSegment == 0) {
                                // Class Selector Horizontal Scroll (1A, 1B, 2A, ..., 6B)
                                item {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        itemsIndexed(state.classes) { index, classItem ->
                                            val isSelected = state.selectedClassIndex == index
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(if (isSelected) Color(0xFF1D68E4) else Color.White)
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0),
                                                        RoundedCornerShape(20.dp)
                                                    )
                                                    .clickable { viewModel.selectClassTab(index) }
                                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val displayName = when {
                                                    classItem.name.matches(Regex("""(?i)^\d+[A-Z]$""")) -> "Kelas ${classItem.name.uppercase()}"
                                                    classItem.name.startsWith("Kelas", ignoreCase = true) -> classItem.name
                                                    else -> "Kelas ${classItem.name}"
                                                }
                                                Text(
                                                    text = displayName,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                                )
                                            }
                                        }
                                    }
                                }

                                // 3. Wali Kelas Info Banner
                                item {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFEFF6FF)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFF1D68E4),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Wali Kelas: ${activeClass?.name ?: "3A"} (${activeClass?.homeroomTeacher ?: "Bpk. Bambang Sutrisno, M.Pd."})",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                                val currentDisplaySchool = if (userRole == com.jadwale.core.model.UserRole.SUPER_ADMIN) selectedSuperAdminSchool else "SDN Pancasila 01"
                                                Text(
                                                    text = "${activeClass?.curriculumPhase ?: "Kurikulum Merdeka"} • $currentDisplaySchool",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            if (activeClass != null && userRole == com.jadwale.core.model.UserRole.ADMIN_SEKOLAH) {
                                                IconButton(onClick = { onNavigateToEdit(activeClass.id) }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit Manual",
                                                        tint = Color(0xFF64748B),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (state.selectedSegment == 1) {
                                // Teacher Selector Horizontal Scroll
                                val activeTeacher = state.teachers.getOrNull(state.selectedTeacherIndex)
                                item {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        itemsIndexed(state.teachers) { index, teacherItem ->
                                            val isSelected = state.selectedTeacherIndex == index
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(if (isSelected) Color(0xFF1D68E4) else Color.White)
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0),
                                                        RoundedCornerShape(20.dp)
                                                    )
                                                    .clickable { viewModel.selectTeacherTab(index) }
                                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = teacherItem.name.split(",").firstOrNull()?.split(" ")?.take(2)?.joinToString(" ") ?: teacherItem.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFDCFCE7)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.School,
                                                    contentDescription = null,
                                                    tint = Color(0xFF15803D),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = activeTeacher?.name ?: "Guru Pendidik",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = "NIP: ${activeTeacher?.nip ?: "-"} • Beban: ${activeTeacher?.totalJp ?: 24} JP / Minggu",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Matriks Rombel Master Control Card
                                item {
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.GridOn,
                                                        contentDescription = null,
                                                        tint = Color(0xFF1D68E4),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Matriks Jadwal Seluruh Sekolah",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E40AF)
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color.White,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                                                ) {
                                                    Text(
                                                        text = if (com.jadwale.core.model.SchoolConfig.isParallel) "12 Rombel" else "6 Kelas",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1D68E4),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${if (com.jadwale.core.model.SchoolConfig.isParallel) "Sekolah Paralel (12 Rombel: 1A-6B)" else "Sekolah Tunggal (6 Kelas: 1-6)"} • ${days.size} Hari Sekolah (${days.first().displayName} - ${days.last().displayName})",
                                                fontSize = 11.sp,
                                                color = Color(0xFF3B82F6)
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // View Mode Switcher: Kartu Rombel vs Tabel Matriks Grid
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFDBEAFE))
                                                    .padding(2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (matrixViewMode == 0) Color.White else Color.Transparent)
                                                        .clickable { matrixViewMode = 0 }
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "Kartu Rombel",
                                                        fontSize = 11.sp,
                                                        fontWeight = if (matrixViewMode == 0) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (matrixViewMode == 0) Color(0xFF1D68E4) else Color(0xFF1E40AF)
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (matrixViewMode == 1) Color.White else Color.Transparent)
                                                        .clickable { matrixViewMode = 1 }
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "Tabel Matriks (Grid)",
                                                        fontSize = 11.sp,
                                                        fontWeight = if (matrixViewMode == 1) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (matrixViewMode == 1) Color(0xFF1D68E4) else Color(0xFF1E40AF)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Grade Filter Pills (Semua, Kelas 1, ..., Kelas 6)
                                            Text(
                                                text = "Filter Tingkat Kelas:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E3A8A)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                item {
                                                    val isSelected = selectedGradeFilter == 0
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(if (isSelected) Color(0xFF1D68E4) else Color.White)
                                                            .border(1.dp, if (isSelected) Color(0xFF1D68E4) else Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                                            .clickable { selectedGradeFilter = 0 }
                                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "Semua (1-6)",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.White else Color(0xFF1E40AF)
                                                        )
                                                    }
                                                }
                                                items(6) { idx ->
                                                    val gradeNum = idx + 1
                                                    val isSelected = selectedGradeFilter == gradeNum
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(if (isSelected) Color(0xFF1D68E4) else Color.White)
                                                            .border(1.dp, if (isSelected) Color(0xFF1D68E4) else Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                                            .clickable { selectedGradeFilter = gradeNum }
                                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "Kelas $gradeNum",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color.White else Color(0xFF1E40AF)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 4. Day Tabs (HARI SENIN, SELASA, RABU, KAMIS, JUMAT, SABTU)
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(days) { day ->
                                        val isSelected = selectedDay == day
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color(0xFFEFF6FF) else Color.White)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedDay = day }
                                                .padding(horizontal = 14.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isSelected) "HARI ${day.displayName.uppercase()}" else day.displayName.uppercase(),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF1D68E4) else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }

                            // 5. Daftar Pelajaran untuk Hari Terpilih
                            if (state.selectedSegment == 2) {
                                // ── Tampilan Matriks Seluruh Sekolah (Kelas 1 s/d 6) ──
                                val allSchoolSlots = if (state.schedule.slots.isNotEmpty()) state.schedule.slots
                                else com.jadwale.core.mock.MockDataProvider.createMockSchedule().slots

                                val allClasses = if (state.classes.isNotEmpty()) state.classes
                                else com.jadwale.core.mock.MockDataProvider.classList

                                if (matrixViewMode == 1) {
                                    // TAMPILAN TABEL MATRIKS GRID (EXCEL / SPREADSHEET STYLE)
                                    item {
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
                                                    Column {
                                                        Text(
                                                            text = "Tabel Matriks Grid Rombel",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                        Text(
                                                            text = "Hari ${selectedDay.displayName} • Geser horizontal untuk melihat seluruh JP",
                                                            fontSize = 11.sp,
                                                            color = Color(0xFF64748B)
                                                        )
                                                    }
                                                    Surface(
                                                        color = Color(0xFFDCFCE7),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "Bebas Bentrok",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF15803D),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))

                                                val distinctTimes = allSchoolSlots
                                                    .filter { it.day == selectedDay }
                                                    .map { it.timeSlot }
                                                    .distinctBy { it.startTime }
                                                    .sortedBy { it.startTime }
                                                    .ifEmpty { com.jadwale.core.mock.MockDataProvider.timeSlotsTemplate }

                                                val targetClasses = run {
                                                    if (!com.jadwale.core.model.SchoolConfig.isParallel) {
                                                        val raw = if (selectedGradeFilter == 0) allClasses
                                                        else allClasses.filter { it.grade == selectedGradeFilter || it.name.startsWith("$selectedGradeFilter") }
                                                        val byGrade = (1..6).filter { selectedGradeFilter == 0 || it == selectedGradeFilter }.map { g ->
                                                            val found = raw.firstOrNull { it.grade == g || it.name.startsWith("$g") }
                                                            found?.copy(name = "$g", grade = g) ?: com.jadwale.core.model.ClassRoom("c_$g", "$g", g, 28, "Wali Kelas $g")
                                                        }
                                                        byGrade
                                                    } else {
                                                        val raw = if (selectedGradeFilter == 0) allClasses
                                                        else allClasses.filter { it.grade == selectedGradeFilter || it.name.startsWith("$selectedGradeFilter") }
                                                        (1..6).filter { selectedGradeFilter == 0 || it == selectedGradeFilter }.flatMap { g ->
                                                            val matches = raw.filter { it.grade == g || it.name.startsWith("$g") }
                                                            val aCls = matches.firstOrNull { it.name.contains("A", ignoreCase = true) }
                                                                ?: matches.firstOrNull()?.copy(name = "${g}A", grade = g)
                                                                ?: com.jadwale.core.model.ClassRoom("c_${g}a", "${g}A", g, 28, "Wali Kelas ${g}A")
                                                            val bCls = matches.firstOrNull { it.name.contains("B", ignoreCase = true) }
                                                                ?: matches.drop(1).firstOrNull()?.copy(name = "${g}B", grade = g)
                                                                ?: com.jadwale.core.model.ClassRoom("c_${g}b", "${g}B", g, 28, "Wali Kelas ${g}B")
                                                            listOf(aCls.copy(name = "${g}A", grade = g), bCls.copy(name = "${g}B", grade = g))
                                                        }
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .horizontalScroll(rememberScrollState())
                                                ) {
                                                    Column {
                                                        // Header Row
                                                        Row(
                                                            modifier = Modifier
                                                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                                .padding(vertical = 8.dp, horizontal = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = if (com.jadwale.core.model.SchoolConfig.isParallel) "Rombel" else "Kelas",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp,
                                                                color = Color(0xFF1E293B),
                                                                modifier = Modifier.width(64.dp),
                                                                textAlign = TextAlign.Center
                                                            )
                                                            distinctTimes.forEach { ts ->
                                                                Text(
                                                                    text = if (ts.isRoutine) (ts.routineType?.name?.take(9) ?: "Kegiatan") else "JP ${ts.jpNumber}\n${ts.startTime}",
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 10.sp,
                                                                    color = Color(0xFF334155),
                                                                    modifier = Modifier.width(96.dp),
                                                                    textAlign = TextAlign.Center
                                                                )
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.height(6.dp))

                                                        // Data Rows for each Class Rombel
                                                        targetClasses.forEach { cls ->
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Surface(
                                                                    shape = RoundedCornerShape(6.dp),
                                                                    color = Color(0xFFEFF6FF),
                                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                                                    modifier = Modifier.width(64.dp)
                                                                ) {
                                                                    Text(
                                                                        text = cls.name,
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        fontSize = 12.sp,
                                                                        color = Color(0xFF1D68E4),
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(vertical = 6.dp)
                                                                    )
                                                                }

                                                                distinctTimes.forEach { ts ->
                                                                    val slot = allSchoolSlots.firstOrNull { s ->
                                                                        s.day == selectedDay &&
                                                                        (s.classId == cls.id || s.className.equals(cls.name, ignoreCase = true) || s.className.startsWith(cls.name)) &&
                                                                        (s.timeSlot.startTime == ts.startTime || s.timeSlot.jpNumber == ts.jpNumber)
                                                                    }

                                                                    Box(
                                                                        modifier = Modifier
                                                                            .width(96.dp)
                                                                            .padding(horizontal = 3.dp),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        if (slot != null) {
                                                                            val style = if (slot.isRoutine) {
                                                                                when {
                                                                                    slot.routineActivity?.name?.contains("Upacara", ignoreCase = true) == true -> SubjectColorPalette.RoutineUpacara
                                                                                    slot.routineActivity?.name?.contains("Istirahat", ignoreCase = true) == true -> SubjectColorPalette.RoutineBreak
                                                                                    else -> SubjectColorPalette.RoutinePembiasaan
                                                                                }
                                                                            } else {
                                                                                SubjectColorPalette.getColorForSubject(slot.subject?.name, slot.subject?.code)
                                                                            }

                                                                            Surface(
                                                                                shape = RoundedCornerShape(6.dp),
                                                                                color = style.backgroundColor,
                                                                                border = androidx.compose.foundation.BorderStroke(0.5.dp, style.textColor.copy(alpha = 0.3f)),
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth()
                                                                                    .clickable { viewModel.selectSlotForDetail(slot) }
                                                                            ) {
                                                                                Column(
                                                                                    modifier = Modifier.padding(4.dp),
                                                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                                                ) {
                                                                                    val label = if (slot.isRoutine) slot.routineActivity?.name ?: "Rutin"
                                                                                    else slot.subject?.code ?: slot.subject?.name?.take(5) ?: "Mapel"
                                                                                    Text(
                                                                                        text = label,
                                                                                        fontSize = 10.sp,
                                                                                        fontWeight = FontWeight.Bold,
                                                                                        color = style.textColor,
                                                                                        maxLines = 1,
                                                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                                                    )
                                                                                    val teacherObj = slot.teacher
                                                                                    if (!slot.isRoutine && teacherObj != null) {
                                                                                        val teacherInit = teacherObj.name.split(" ").take(2).joinToString(" ") { it.take(1) }
                                                                                        Text(
                                                                                            text = teacherInit,
                                                                                            fontSize = 8.sp,
                                                                                            color = style.textColor.copy(alpha = 0.8f)
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else {
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth()
                                                                                    .height(28.dp)
                                                                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp)),
                                                                                contentAlignment = Alignment.Center
                                                                            ) {
                                                                                Text("-", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // TAMPILAN KARTU ROMBEL PER TINGKAT KELAS
                                    val gradesToShow = if (selectedGradeFilter == 0) (1..6).toList() else listOf(selectedGradeFilter)

                                    gradesToShow.forEach { gradeNum ->
                                        item {
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
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(34.dp)
                                                                    .clip(CircleShape)
                                                                    .background(Color(0xFFEFF6FF)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = "$gradeNum",
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    fontSize = 15.sp,
                                                                    color = Color(0xFF1D68E4)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column {
                                                                Text(
                                                                    text = "Tingkat Kelas $gradeNum",
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 14.sp,
                                                                    color = Color(0xFF0F172A)
                                                                )
                                                                Text(
                                                                    text = if (com.jadwale.core.model.SchoolConfig.isParallel) "Rombel Paralel: ${gradeNum}A & ${gradeNum}B" else "Kelas Tunggal: Kelas $gradeNum",
                                                                    fontSize = 11.sp,
                                                                    color = Color(0xFF64748B)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    val rombelsInGrade = run {
                                                        val matches = allClasses.filter {
                                                            it.grade == gradeNum || it.name.startsWith("$gradeNum") || it.name.contains("Kelas $gradeNum")
                                                        }
                                                        val candidateClasses = if (matches.size > 2 && allClasses.size >= 12 && allClasses.count { it.grade == 1 } >= 6) {
                                                            val startIdx = (gradeNum - 1) * 2
                                                            allClasses.subList(startIdx, (startIdx + 2).coerceAtMost(allClasses.size))
                                                        } else {
                                                            matches
                                                        }

                                                        if (candidateClasses.isNotEmpty()) {
                                                            if (!com.jadwale.core.model.SchoolConfig.isParallel) {
                                                                val rawCls = candidateClasses.first()
                                                                val cleanName = if (rawCls.name.matches(Regex("""(?i)^\d+$"""))) rawCls.name else "$gradeNum"
                                                                listOf(rawCls.copy(name = cleanName, grade = gradeNum))
                                                            } else {
                                                                val pair = if (candidateClasses.size == 1) {
                                                                    listOf(candidateClasses[0] to "A", candidateClasses[0] to "B")
                                                                } else {
                                                                    candidateClasses.take(2).mapIndexed { idx, rawCls ->
                                                                        val letter = when {
                                                                            rawCls.name.contains("B", ignoreCase = true) -> "B"
                                                                            rawCls.name.contains("A", ignoreCase = true) -> "A"
                                                                            idx % 2 == 1 -> "B"
                                                                            else -> "A"
                                                                        }
                                                                        rawCls to letter
                                                                    }
                                                                }
                                                                pair.map { (rawCls, letter) ->
                                                                    val cleanName = if (rawCls.name.matches(Regex("""(?i)^\d+[A-Z]$"""))) rawCls.name.uppercase() else "${gradeNum}$letter"
                                                                    rawCls.copy(name = cleanName, grade = gradeNum)
                                                                }
                                                            }
                                                        } else {
                                                            if (com.jadwale.core.model.SchoolConfig.isParallel) {
                                                                listOf(
                                                                    com.jadwale.core.model.ClassRoom("c_${gradeNum}a", "${gradeNum}A", gradeNum, 28, "Wali Kelas ${gradeNum}A"),
                                                                    com.jadwale.core.model.ClassRoom("c_${gradeNum}b", "${gradeNum}B", gradeNum, 28, "Wali Kelas ${gradeNum}B")
                                                                )
                                                            } else {
                                                                listOf(com.jadwale.core.model.ClassRoom("c_$gradeNum", "$gradeNum", gradeNum, 30, "Wali Kelas $gradeNum"))
                                                            }
                                                        }
                                                    }

                                                    rombelsInGrade.forEach { rombel ->
                                                        Surface(
                                                            color = Color(0xFFF8FAFC),
                                                            shape = RoundedCornerShape(10.dp),
                                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                                        ) {
                                                            Column(modifier = Modifier.padding(10.dp)) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        text = if (com.jadwale.core.model.SchoolConfig.isParallel) "Rombel Kelas ${rombel.name}" else "Kelas ${rombel.name}",
                                                                        fontSize = 12.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = Color(0xFF1E40AF)
                                                                    )
                                                                    Text(
                                                                        text = "Wali: ${rombel.homeroomTeacher ?: "Guru Kelas"}",
                                                                        fontSize = 11.sp,
                                                                        color = Color(0xFF64748B)
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.height(8.dp))

                                                                val slotsForRombel = allSchoolSlots.filter { slot ->
                                                                    slot.day == selectedDay && (
                                                                        slot.classId == rombel.id ||
                                                                        slot.className.equals(rombel.name, ignoreCase = true) ||
                                                                        slot.className.equals("Kelas ${rombel.name}", ignoreCase = true) ||
                                                                        slot.className.startsWith(rombel.name)
                                                                    )
                                                                }.sortedBy { it.timeSlot.startTime }

                                                                if (slotsForRombel.isEmpty()) {
                                                                    Text(
                                                                        text = "Belum ada slot pelajaran untuk Kelas ${rombel.name} di hari ${selectedDay.displayName}",
                                                                        fontSize = 11.sp,
                                                                        color = Color(0xFF94A3B8)
                                                                    )
                                                                } else {
                                                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                        items(slotsForRombel) { slot ->
                                                                            val style = if (slot.isRoutine) {
                                                                                when {
                                                                                    slot.routineActivity?.name?.contains("Upacara", ignoreCase = true) == true -> SubjectColorPalette.RoutineUpacara
                                                                                    slot.routineActivity?.name?.contains("Istirahat", ignoreCase = true) == true -> SubjectColorPalette.RoutineBreak
                                                                                    else -> SubjectColorPalette.RoutinePembiasaan
                                                                                }
                                                                            } else {
                                                                                SubjectColorPalette.getColorForSubject(slot.subject?.name, slot.subject?.code)
                                                                            }
                                                                            Box(modifier = Modifier.width(156.dp)) {
                                                                                ScheduleMiniMatrixCard(
                                                                                    slot = slot,
                                                                                    colorStyle = style,
                                                                                    rombelLabel = rombel.name,
                                                                                    onClick = { viewModel.selectSlotForDetail(slot) }
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (daySlots.isEmpty()) {
                                item {
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EventBusy,
                                                contentDescription = null,
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Tidak Ada Jadwal di Hari ${selectedDay.displayName}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Jadwal kosong atau belum diisi untuk hari ini.",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(daySlots) { slot ->
                                    val style = if (slot.isRoutine) {
                                        when {
                                            slot.routineActivity?.name?.contains("Upacara", ignoreCase = true) == true -> SubjectColorPalette.RoutineUpacara
                                            slot.routineActivity?.name?.contains("Istirahat", ignoreCase = true) == true -> SubjectColorPalette.RoutineBreak
                                            else -> SubjectColorPalette.RoutinePembiasaan
                                        }
                                    } else {
                                        SubjectColorPalette.getColorForSubject(slot.subject?.name, slot.subject?.code)
                                    }
                                    ScheduleItemCard(
                                        slot = slot,
                                        colorStyle = style,
                                        onClick = { viewModel.selectSlotForDetail(slot) }
                                    )
                                }
                            }

                            // 6. Action Button: Ekspor & Cetak Berkas
                            item {
                                OutlinedButton(
                                    onClick = { showExportSheet = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D68E4)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D68E4)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ekspor / Cetak Berkas Jadwal", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }

                    // Bottom Sheet Detail saat Cell di-klik
                    ScheduleDetailBottomSheet(
                        slot = state.selectedSlotDetail,
                        onDismiss = { viewModel.selectSlotForDetail(null) }
                    )

                    // Export & Print Bottom Sheet
                    if (showExportSheet) {
                        val activeClassName = "Kelas ${state.classes.getOrNull(state.selectedClassIndex)?.name ?: "3A"}"
                        ExportPrintBottomSheet(
                            className = activeClassName,
                            onDismiss = { showExportSheet = false }
                        )
                    }

                    // Notification Dialog
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
                                            Text("Semester Ganjil 2025/2026 SDN Pancasila 01 telah 100% teralokasi bebas bentrok.", fontSize = 11.sp, color = Color(0xFF166534))
                                        }
                                    }
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Standar Kurikulum Merdeka", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1D4ED8))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("Sesuai Kurikulum Merdeka Fase A, B, dan C (480 JP Mingguan).", fontSize = 11.sp, color = Color(0xFF1E3A8A))
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

                    // Profile Dialog
                    if (showProfileDialog) {
                        val (pName, pAcc, pBadge, pBadgeBg, pBadgeColor) = when (userRole) {
                            com.jadwale.core.model.UserRole.GURU -> arrayOf(
                                if (!userName.isNullOrBlank() && userName != "default") userName else "Bpk. Bambang Sutrisno, M.Pd",
                                "guru@sdnpancasila.sch.id (NIP 19820315 200801 1 008)",
                                "Guru Pengampu • Sertifikasi Pendidik",
                                Color(0xFFDBEAFE),
                                Color(0xFF1D4ED8)
                            )
                            com.jadwale.core.model.UserRole.SUPER_ADMIN -> arrayOf(
                                "Super Administrator",
                                "superadmin@jadwale.id",
                                "Super Admin • Dinas Pendidikan",
                                Color(0xFFEFF6FF),
                                Color(0xFF1D68E4)
                            )
                            com.jadwale.core.model.UserRole.UMUM -> arrayOf(
                                "Tamu / Orang Tua Siswa",
                                "Akses Tamu Publik Tanpa Akun",
                                "Wali Murid • Akses Tamu",
                                Color(0xFFCCFBF1),
                                Color(0xFF0F766E)
                            )
                            com.jadwale.core.model.UserRole.ADMIN_SEKOLAH -> arrayOf(
                                "Operator SDN Pancasila 01",
                                "operator@sdnpancasila01.sch.id",
                                "Admin Sekolah • Terverifikasi Dapodik",
                                Color(0xFFDCFCE7),
                                Color(0xFF15803D)
                            )
                        }

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
                                    Text(pName as String, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
                                    Text(pAcc as String, fontSize = 12.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(pBadgeBg as Color)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(pBadge as String, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = pBadgeColor as Color)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Untuk melihat konfigurasi dan menu lengkap, silakan buka tab Menu.", fontSize = 11.sp, color = Color(0xFF475569), textAlign = TextAlign.Center)
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showProfileDialog = false
                                        onNavigateToMenu()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                                ) { Text("Buka Menu Lengkap") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showProfileDialog = false }) { Text("Tutup") }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(
    slot: ScheduleSlot,
    colorStyle: com.jadwale.feature.schedule_view.components.SubjectColorStyle,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
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
            // Time Column
            Column(
                modifier = Modifier.width(95.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${slot.timeSlot.startTime} - ${slot.timeSlot.endTime}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                if (!slot.isRoutine && slot.timeSlot.jpNumber > 0) {
                    Text(
                        text = "JP ${slot.timeSlot.jpNumber}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Subject & Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeLabel = if (slot.isRoutine) {
                        slot.routineActivity?.name ?: "Rutin"
                    } else {
                        slot.subject?.name ?: "Pelajaran"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorStyle.backgroundColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorStyle.textColor
                        )
                    }

                    val classLabel = slot.className.ifBlank {
                        slot.classId.removePrefix("class_").uppercase()
                    }
                    if (classLabel.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Kelas $classLabel",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D68E4)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val titleText = if (slot.isRoutine) {
                    slot.routineActivity?.name ?: "Kegiatan Sekolah"
                } else {
                    slot.subject?.name ?: "Mata Pelajaran"
                }

                Text(
                    text = titleText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                val subText = if (slot.isRoutine) {
                    "Seluruh Siswa • ${if (slot.room.isNotBlank()) slot.room else "Area Sekolah"}"
                } else {
                    val teacherName = slot.teacher?.name ?: "Guru Mapel"
                    val classLabel = slot.className.ifBlank {
                        slot.classId.removePrefix("class_").uppercase()
                    }
                    "$teacherName • Kelas $classLabel"
                }

                Text(
                    text = subText,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ScheduleMiniMatrixCard(
    slot: ScheduleSlot,
    colorStyle: com.jadwale.feature.schedule_view.components.SubjectColorStyle,
    rombelLabel: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorStyle.backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorStyle.textColor.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val badgeText = when {
                    !rombelLabel.isNullOrBlank() -> if (rombelLabel.startsWith("Kelas", ignoreCase = true)) rombelLabel else "Kelas $rombelLabel"
                    slot.className.startsWith("Kelas", ignoreCase = true) -> slot.className
                    else -> "Kelas ${slot.className}"
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = colorStyle.textColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorStyle.textColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${slot.timeSlot.startTime} - ${slot.timeSlot.endTime}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorStyle.textColor.copy(alpha = 0.85f),
                    maxLines = 1,
                    softWrap = false
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            val title = if (slot.isRoutine) slot.routineActivity?.name ?: "Rutin" else slot.subject?.name ?: "-"
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorStyle.textColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            val subtitle = if (slot.isRoutine) {
                slot.room.ifBlank { "Area Sekolah" }
            } else {
                slot.teacher?.name?.split(",")?.firstOrNull()?.split(" ")?.take(2)?.joinToString(" ") ?: slot.teacher?.name ?: "-"
            }
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = colorStyle.textColor.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

