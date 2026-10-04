package com.jadwale.feature.dashboard

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
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

/**
 * 1. Role Switcher Dialog ("Ganti / Demo")
 */
@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole = UserRole.ADMIN_SEKOLAH,
    onDismiss: () -> Unit,
    onRoleSelected: (UserRole) -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf(currentRole) }

    val roles = listOf(
        UserRole.ADMIN_SEKOLAH to ("Admin Sekolah (Operator)" to "Akses penuh penyusunan jadwal, 12 rombel, 24 guru"),
        UserRole.GURU to ("Guru Pengampu (Bpk. Bambang Sutrisno, M.Pd)" to "Wali Kelas 3A, jadwal ajar 18 JP/minggu & presensi"),
        UserRole.SUPER_ADMIN to ("Super Admin Sistem" to "Konsol multi-sekolah, audit log, & mesin CSP")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SyncAlt, contentDescription = null, tint = Color(0xFF1D68E4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Beralih Peran Demo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Pilih perspektif pengguna untuk menguji simulasi alur kerja aplikasi Jadwale:",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(4.dp))
                roles.forEach { (role, info) ->
                    val (title, desc) = info
                    val isSelected = selectedRole == role
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = role },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedRole = role },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1D68E4))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onRoleSelected(selectedRole)
                    Toast.makeText(context, "✓ Berhasil beralih peran", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
            ) {
                Text("Terapkan Peran")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * 2. School Profile & Configuration Modal
 * Matches: UI JADWALE/Profil Sekolah & Konfigurasi - Jadwale.png
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolProfileBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var schoolName by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.schoolName) }
    var npsn by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.npsn) }
    var address by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.address) }
    var principalName by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.headmaster) }
    var principalNip by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.headmasterNip) }
    var activeYear by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.academicYear) }
    var semester by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.semester) }
    var isParallel by remember { mutableStateOf(com.jadwale.core.model.SchoolConfig.isParallel) }
    var daysCount by remember { mutableStateOf(if (com.jadwale.core.model.SchoolConfig.daysCount == 6) "6 Hari" else "5 Hari") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Profil & Konfigurasi Sekolah", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Identitas Sekolah Dasar & Pengaturan Tahun Ajaran", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Info Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Standar Dokumen Resmi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text(
                            "Pengaturan ini menjadi acuan kop surat pada cetak jadwal PDF dan parameter mesin jadwal otomatis.",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // Section 1: Identitas Satuan Pendidikan
            Text("🏛️ Identitas Satuan Pendidikan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val isDark = isSystemInDarkTheme()
                    val logoRes = if (isDark) R.drawable.logo_jadwale_light else R.drawable.logo_jadwale_dark
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = "Logo Jadwale",
                            modifier = Modifier.size(46.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Logo Resmi Sekolah (Format PNG / JPG)", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Fitur unggah logo aktif (Kamera / Galeri)", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ganti Logo Sekolah", fontSize = 11.sp)
                    }
                }
            }

            OutlinedTextField(
                value = schoolName,
                onValueChange = { schoolName = it },
                label = { Text("Nama Satuan Pendidikan (SDN)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = npsn,
                onValueChange = { npsn = it },
                label = { Text("Nomor Pokok Sekolah Nasional (NPSN)") },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Terverifikasi", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Lengkap Sekolah") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Section 2: Data Kepala Sekolah
            Text("👤 Data Kepala Sekolah", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            OutlinedTextField(
                value = principalName,
                onValueChange = { principalName = it },
                label = { Text("Nama Lengkap & Gelar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = principalNip,
                onValueChange = { principalNip = it },
                label = { Text("NIP (Nomor Induk Pegawai)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Section 3: Parameter Kalender & Jadwal
            Text("⚙️ Parameter Jadwal & Kalender", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            // Struktur Kelas & Paralel Selector
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Struktur Kelas Sekolah (Paralel / Tunggal):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isParallel) Color(0xFF1D68E4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isParallel) Color(0xFF1D68E4) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isParallel = true
                                    com.jadwale.core.model.SchoolConfig.isParallel = true
                                    com.jadwale.core.model.SchoolConfig.isUserExplicitlySetParallel = true
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sekolah Paralel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isParallel) Color.White else Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "12 Rombel (1A s/d 6B)",
                                    fontSize = 10.sp,
                                    color = if (isParallel) Color(0xFFDBEAFE) else Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isParallel) Color(0xFF1D68E4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (!isParallel) Color(0xFF1D68E4) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    isParallel = false
                                    com.jadwale.core.model.SchoolConfig.isParallel = false
                                    com.jadwale.core.model.SchoolConfig.isUserExplicitlySetParallel = true
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sekolah Tunggal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (!isParallel) Color.White else Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "6 Kelas (1 s/d 6)",
                                    fontSize = 10.sp,
                                    color = if (!isParallel) Color(0xFFDBEAFE) else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isParallel)
                            "✓ Struktur Paralel: rombel dibuat 1a 1b, 2a 2b, 3a 3b, 4a 4b, 5a 5b, 6a 6b (total 12 rombel aktif)."
                        else
                            "✓ Struktur Tunggal: kelas cukup 1 2 3 4 5 6 (total 6 kelas aktif).",
                        fontSize = 11.sp,
                        color = Color(0xFF1D68E4),
                        fontWeight = FontWeight.Medium
                    )

                    val activePreviewClasses = remember(isParallel) {
                        if (isParallel) listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B")
                        else listOf("1", "2", "3", "4", "5", "6")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activePreviewClasses.forEach { cName ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isParallel) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isParallel) Color(0xFFBFDBFE) else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = "Kelas $cName",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isParallel) Color(0xFF1D68E4) else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { semester = "Ganjil" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (semester == "Ganjil") Color(0xFF1D68E4) else Color(0xFFF1F5F9),
                        contentColor = if (semester == "Ganjil") Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Semester Ganjil", fontSize = 12.sp)
                }

                Button(
                    onClick = { semester = "Genap" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (semester == "Genap") Color(0xFF1D68E4) else Color(0xFFF1F5F9),
                        contentColor = if (semester == "Genap") Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Semester Genap", fontSize = 12.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { daysCount = "5 Hari" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (daysCount == "5 Hari") Color(0xFF1D68E4) else Color(0xFFF1F5F9),
                        contentColor = if (daysCount == "5 Hari") Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("5 Hari (Senin-Jumat)", fontSize = 12.sp)
                }

                Button(
                    onClick = { daysCount = "6 Hari" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (daysCount == "6 Hari") Color(0xFF1D68E4) else Color(0xFFF1F5F9),
                        contentColor = if (daysCount == "6 Hari") Color.White else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("6 Hari (Senin-Sabtu)", fontSize = 12.sp)
                }
            }

            OutlinedTextField(
                value = activeYear,
                onValueChange = { activeYear = it },
                label = { Text("Tahun Ajaran Aktif") },
                supportingText = { Text("Contoh: 2025/2026 atau 2026/2027", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    com.jadwale.core.model.SchoolConfig.isParallel = isParallel
                    com.jadwale.core.model.SchoolConfig.isUserExplicitlySetParallel = true
                    com.jadwale.core.model.SchoolConfig.daysCount = if (daysCount.startsWith("6")) 6 else 5
                    com.jadwale.core.model.SchoolConfig.schoolName = schoolName
                    com.jadwale.core.model.SchoolConfig.npsn = npsn
                    com.jadwale.core.model.SchoolConfig.address = address
                    com.jadwale.core.model.SchoolConfig.headmaster = principalName
                    com.jadwale.core.model.SchoolConfig.headmasterNip = principalNip
                    com.jadwale.core.model.SchoolConfig.academicYear = activeYear
                    com.jadwale.core.model.SchoolConfig.semester = semester
                    Toast.makeText(
                        context,
                        "✓ Konfigurasi sekolah (${if (isParallel) "12 Rombel Paralel (1a-6b)" else "6 Kelas Tunggal (1-6)"}, $daysCount, $activeYear) berhasil disimpan!",
                        Toast.LENGTH_LONG
                    ).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Profil & Konfigurasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 3. Super Admin Console Modal
 * Matches: UI JADWALE/Konsol Super Admin & Sistem - Jadwale.png
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminConsoleBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isMaintenanceMode by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("RBAC LEVEL 3 • SUPER ADMIN • Sistem Normal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Konsol Super Admin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Monitoring Multi-Sekolah, Antrean CSP, & Audit Log", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Real-time Status 2x2 Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Sekolah Binaan", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("5 Unit", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("Wilayah Supervisi Aktif", fontSize = 10.sp, color = Color(0xFF1D68E4))
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Mesin CSP", fontSize = 11.sp, color = Color(0xFF15803D))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("142 Kali", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        Text("Bebas Bentrok 100%", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Redis & BullMQ", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Normal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        Text("0 antrean • 14ms", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("PDF Worker", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Aktif", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                        Text("Puppeteer Standby", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
            }

            // System Maintenance Controls
            Text("⚙️ Kontrol Sistem & Pemeliharaan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mode Pemeliharaan (Maintenance)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Nonaktif (Sistem dapat diakses publik)", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = isMaintenanceMode,
                            onCheckedChange = {
                                isMaintenanceMode = it
                                Toast.makeText(
                                    context,
                                    if (it) "Mode pemeliharaan diaktifkan" else "Mode pemeliharaan dinonaktifkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "✓ Cache Redis berhasil dibersihkan (Flush Global)", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Flush Cache Global", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "✓ Sinkronisasi data induk 5 sekolah binaan wilayah berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkronisasi Dapodik Multi-Sekolah", fontSize = 12.sp)
                    }
                }
            }

            // Audit Log Feed
            Text("📋 Log Audit Aktivitas Terkini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Generator CSP Selesai", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                            Text("Operator SDN Pancasila men-generate jadwal (3.2 detik)", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text("10:42", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Unduh Dokumen PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                            Text("Guru Bpk. Bambang Sutrisno mengunduh jadwal Kelas 3A", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text("09:15", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                }
            }

            Button(
                onClick = {
                    Toast.makeText(context, "✓ Berkas rekap log_audit_sistem.csv berhasil diunduh!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unduh Rekap Log Sistem (.CSV)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 4. Help Center Modal
 * Matches: UI JADWALE/Pusat Bantuan Guru SD - Jadwale.png
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var expandedFaq by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Pusat Bantuan Guru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("Panduan Praktis SD Tanpa Ribet", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Official WA Support Box
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Masih Mengalami Kendala?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF14532D))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tim fasilitator Jadwale siap memandu pengisian data jadwal Anda lewat obrolan WhatsApp.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281234567890?text=Halo%20Tim%20Jadwale,%20saya%20butuh%20bantuan%20penyusunan%20jadwal"))
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Membuka WhatsApp Bantuan: +62 812-3456-7890", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hubungi Bantuan WhatsApp Resmi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Guides
            Text("📚 Panduan Langkah Demi Langkah", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            val guides = listOf(
                "Cara Mengatur Guru Honorer Agar Tidak Bentrok" to "Cukup centang hari kehadiran guru di menu Manajemen Guru. Mesin otomatis mengosongkan jam selain hari yang dipilih.",
                "Mengunci Jam Upacara Bendera Senin" to "Aktifkan kegiatan rutin Upacara pada slot pertama Senin. Jam ini otomatis terkunci untuk semua kelas 1-6.",
                "Membagikan Jadwal ke WhatsApp Paguyuban" to "Gunakan tombol Bagikan WhatsApp pada tampilan Jadwal untuk mengirim format teks rapi langsung ke grup.",
                "Mengubah Jadwal Bila Ada Guru yang Pindah Hari" to "Ubah hari ketersediaan pada profil guru, lalu jalankan kembali Generator Otomatis."
            )

            guides.forEach { (title, content) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(content, fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 16.sp)
                    }
                }
            }

            // FAQ
            Text("❓ Pertanyaan Sering Diajukan (FAQ)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            val faqs = listOf(
                "Apakah data saya bisa hilang bila aplikasi tertutup?" to "Tidak. Semua data tersimpan secara otomatis dan aman.",
                "Apakah bisa dipakai untuk sekolah masuk 6 hari kerja?" to "Bisa. Anda dapat mengatur 5 atau 6 hari kerja pada menu Profil & Konfigurasi Sekolah.",
                "Bagaimana jika dalam satu jam ada 2 guru mengajar?" to "Aplikasi mendukung penugasan Guru Pendamping (Team Teaching) pada menu Alokasi JP.",
                "Bisa langsung dicetak ke kertas Folio (F4) dan A4?" to "Bisa. Tersedia pilihan ukuran cetak A4 dan Folio F4 pada menu Ekspor PDF."
            )

            faqs.forEachIndexed { index, (q, a) ->
                val isExpanded = expandedFaq == index
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFaq = if (isExpanded) null else index },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(q, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF0F172A), modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(a, fontSize = 11.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }

            // Download PDF Manual
            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "✓ Mengunduh Buku Panduan Jadwale SD (PDF)...", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Buku Panduan Cetak (PDF)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 5. Notification Bottom Sheet ("Notifikasi Sistem")
 * Menampilkan daftar notifikasi sistem, status jadwal, dan validasi Kemdikbudristek
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToScheduleView: () -> Unit = {}
) {
    val context = LocalContext.current
    var notificationsRead by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF1D68E4),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Notifikasi Sistem",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            if (!notificationsRead) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDBEAFE))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "3 Baru",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Pemberitahuan berkas, jadwal & validasi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Notification 1: Jadwal Siap
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
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
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jadwal Pelajaran Siap Digunakan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF14532D)
                            )
                        }
                        Text("Baru saja", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Penyusunan jadwal otomatis Semester Ganjil 2025/2026 SDN Pancasila 01 telah selesai. 100% jam pelajaran (480 JP) teralokasi bebas bentrok ruang dan guru.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onNavigateToScheduleView()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Buka Tampilan Jadwal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Notification 2: Standar Kemdikbudristek
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
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF1D68E4),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Validasi Kurikulum Merdeka",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E40AF)
                            )
                        }
                        Text("1 jam lalu", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Distribusi alokasi beban kurikulum Fase A (Kelas 1-2), Fase B (Kelas 3-4), dan Fase C (Kelas 5-6) telah diverifikasi sesuai Permendikbudristek No. 12 Tahun 2024.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 16.sp
                    )
                }
            }

            // Notification 3: Guru Honorer Disinkronkan
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
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
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ketersediaan Guru Terkunci",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Text("Kemarin", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jadwal kehadiran Guru Honorer Bpk. Ahmad Fauzi, S.Ag (Senin & Kamis) telah berhasil dikunci pada sistem penyusunan jadwal.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Tandai Semua Sudah Dibaca
            OutlinedButton(
                onClick = {
                    notificationsRead = true
                    Toast.makeText(context, "✓ Semua notifikasi telah ditandai dibaca", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tandai Semua Sudah Dibaca", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text("Tutup", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Dialog Ubah Profil Pengguna (Semua Role: Superadmin, Admin Sekolah, Guru, Umum)
 */
@Composable
fun EditUserProfileDialog(
    currentRole: UserRole,
    onDismiss: () -> Unit,
    onProfileUpdated: (name: String, email: String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(com.jadwale.core.model.UserProfileConfig.getNameForRole(currentRole)) }
    var email by remember { mutableStateOf(com.jadwale.core.model.UserProfileConfig.getEmailForRole(currentRole)) }
    var phone by remember { mutableStateOf(com.jadwale.core.model.UserProfileConfig.phone) }
    var nip by remember { mutableStateOf(com.jadwale.core.model.UserProfileConfig.nip) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Color(0xFF1D68E4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ubah Data Profil Akun", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Perbarui identitas profil akun Anda:",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Akun") },
                    supportingText = {
                        Text(
                            when (currentRole) {
                                UserRole.ADMIN_SEKOLAH -> "Domain resmi sekolah: @*.sch.id"
                                UserRole.GURU -> "Domain resmi guru: @guru.sd.belajar.id"
                                else -> "Format email aktif"
                            },
                            fontSize = 10.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor WhatsApp / HP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (currentRole == UserRole.GURU || currentRole == UserRole.ADMIN_SEKOLAH) {
                    OutlinedTextField(
                        value = nip,
                        onValueChange = { nip = it },
                        label = { Text("NIP (Opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Text("Keamanan & Kata Sandi (Opsional):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF334155))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Kata Sandi Baru") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Kata Sandi") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank()) {
                        Toast.makeText(context, "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Toast.makeText(context, "Konfirmasi kata sandi tidak cocok!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    com.jadwale.core.model.UserProfileConfig.name = name
                    com.jadwale.core.model.UserProfileConfig.email = email
                    com.jadwale.core.model.UserProfileConfig.phone = phone
                    com.jadwale.core.model.UserProfileConfig.nip = nip
                    onProfileUpdated(name, email)
                    Toast.makeText(context, "✓ Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * 6. User Profile Bottom Sheet
 * Menampilkan info akun pengguna, profil, role switcher, dan link ke konfigurasi sekolah
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileBottomSheet(
    currentRole: UserRole = UserRole.ADMIN_SEKOLAH,
    userName: String? = null,
    userAccount: String? = null,
    schoolName: String = "SDN Pancasila 01",
    onDismiss: () -> Unit,
    onOpenSchoolProfile: () -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    onOpenHelpCenter: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var currentName by remember {
        mutableStateOf(
            if (!userName.isNullOrBlank() && userName != "default") userName
            else com.jadwale.core.model.UserProfileConfig.getNameForRole(currentRole)
        )
    }
    var currentAccount by remember {
        mutableStateOf(
            if (!userAccount.isNullOrBlank() && userAccount != "default") userAccount
            else com.jadwale.core.model.UserProfileConfig.getEmailForRole(currentRole)
        )
    }

    val resolvedBadge = when (currentRole) {
        UserRole.ADMIN_SEKOLAH -> "Admin Sekolah • Terverifikasi Dapodik"
        UserRole.GURU -> "Guru Pengampu • Terverifikasi"
        UserRole.SUPER_ADMIN -> "RBAC Level 3 • Super Admin Wilayah"
        UserRole.UMUM -> "Wali Murid / Tamu Resmi"
    }

    val badgeColor = when (currentRole) {
        UserRole.ADMIN_SEKOLAH -> Color(0xFF15803D)
        UserRole.GURU -> Color(0xFF1D4ED8)
        UserRole.SUPER_ADMIN -> Color(0xFF7E22CE)
        UserRole.UMUM -> Color(0xFF0F766E)
    }

    val badgeBg = when (currentRole) {
        UserRole.ADMIN_SEKOLAH -> Color(0xFFDCFCE7)
        UserRole.GURU -> Color(0xFFDBEAFE)
        UserRole.SUPER_ADMIN -> Color(0xFFF3E8FF)
        UserRole.UMUM -> Color(0xFFCCFBF1)
    }

    if (showEditProfileDialog) {
        EditUserProfileDialog(
            currentRole = currentRole,
            onDismiss = { showEditProfileDialog = false },
            onProfileUpdated = { newName, newEmail ->
                currentName = newName
                currentAccount = newEmail
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Profil Pengguna & Akun", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // User Identity Card - Terpusat Presisi di Tengah
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D68E4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = currentName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentAccount,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = resolvedBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            // Tombol Ubah Profil untuk semua role
            Button(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ubah Data Profil", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Logout Button
            OutlinedButton(
                onClick = {
                    onLogout()
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari Akun", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 7. App Settings Bottom Sheet ("Pengaturan Aplikasi")
 * Menampilkan kontrol notifikasi jadwal, preferensi cetak PDF, format waktu, dan cache data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var notifBellEnabled by remember { mutableStateOf(true) }
    var notifScheduleChangeEnabled by remember { mutableStateOf(true) }
    var notifHomeroomNoteEnabled by remember { mutableStateOf(true) }
    var offlineCacheEnabled by remember { mutableStateOf(true) }
    var is24HourFormat by remember { mutableStateOf(true) }
    var isLargeFont by remember { mutableStateOf(false) }
    var paperSize by remember { mutableStateOf("A4") }
    var showQrVerification by remember { mutableStateOf(true) }
    var showHeadmasterSignature by remember { mutableStateOf(true) }
    var isCacheCleared by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color(0xFF1D68E4),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pengaturan Aplikasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Preferensi notifikasi, tampilan, & dokumen cetak",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            // Section 1: Notifikasi & Bel Jadwal
            Text("🔔 Notifikasi & Pengingat Jadwal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pengingat Bel & Jam Pelajaran", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Peringatan 5 menit sebelum pergantian jam/istirahat", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = notifBellEnabled,
                            onCheckedChange = {
                                notifBellEnabled = it
                                Toast.makeText(context, if (it) "Pengingat bel diaktifkan" else "Pengingat bel dinonaktifkan", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Perubahan & Pertukaran Jadwal", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Pemberitahuan saat ada guru piket atau tukar sesi", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = notifScheduleChangeEnabled,
                            onCheckedChange = {
                                notifScheduleChangeEnabled = it
                                Toast.makeText(context, if (it) "Notifikasi pertukaran diaktifkan" else "Notifikasi pertukaran dinonaktifkan", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Catatan Wali Kelas & Seragam", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Pengingat seragam dan perlengkapan esok hari (18.00)", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = notifHomeroomNoteEnabled,
                            onCheckedChange = {
                                notifHomeroomNoteEnabled = it
                                Toast.makeText(context, if (it) "Pengingat catatan wali kelas aktif" else "Pengingat dinonaktifkan", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Section 2: Tampilan & Format Waktu
            Text("🕒 Format Waktu & Tampilan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Format Jam Pelajaran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (is24HourFormat) Color(0xFF1D68E4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (is24HourFormat) Color(0xFF1D68E4) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    is24HourFormat = true
                                    Toast.makeText(context, "Format 24 Jam (07.00 - 12.45) aktif", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = "24 Jam (07.00)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (is24HourFormat) Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!is24HourFormat) Color(0xFF1D68E4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (!is24HourFormat) Color(0xFF1D68E4) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    is24HourFormat = false
                                    Toast.makeText(context, "Format 12 Jam (07:00 AM) aktif", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = "12 Jam (07:00 AM)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!is24HourFormat) Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ukuran Teks Kartu Jadwal", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Tampilkan tulisan lebih besar agar nyaman dibaca", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = isLargeFont,
                            onCheckedChange = {
                                isLargeFont = it
                                Toast.makeText(context, if (it) "Teks jadwal diperbesar" else "Teks ukuran standar", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Section 3: Preferensi Ekspor Cetak PDF
            Text("📄 Preferensi Dokumen Cetak (PDF)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ukuran Kertas Bawaan Cetak:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (paperSize == "A4") Color(0xFF15803D) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (paperSize == "A4") Color(0xFF15803D) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    paperSize = "A4"
                                    Toast.makeText(context, "Kertas cetak diatur ke A4 (210 x 297 mm)", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = "A4 (Standar)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (paperSize == "A4") Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (paperSize == "F4") Color(0xFF15803D) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (paperSize == "F4") Color(0xFF15803D) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    paperSize = "F4"
                                    Toast.makeText(context, "Kertas cetak diatur ke F4/Folio (215 x 330 mm)", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = "F4 / Folio Sekolah",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (paperSize == "F4") Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("QR Code Verifikasi Kemdikbud", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Sematkan barcode validitas di pojok dokumen PDF", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = showQrVerification,
                            onCheckedChange = { showQrVerification = it }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kolom Tanda Tangan Kepala Sekolah", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Sertakan titi mangsa dan kolom pengesahan resmi", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = showHeadmasterSignature,
                            onCheckedChange = { showHeadmasterSignature = it }
                        )
                    }
                }
            }

            // Section 4: Data & Cache Lokal
            Text("💾 Penyimpanan & Cache Data", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Akses Jadwal Offline", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Buka jadwal tanpa koneksi internet (hemat kuota)", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = offlineCacheEnabled,
                            onCheckedChange = {
                                offlineCacheEnabled = it
                                Toast.makeText(context, if (it) "Mode offline aktif" else "Mode offline nonaktif", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    OutlinedButton(
                        onClick = {
                            isCacheCleared = true
                            Toast.makeText(context, "✓ Cache lokal 4.2 MB berhasil dibersihkan!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isCacheCleared) "Cache Bersih (0 B)" else "Bersihkan Cache Aplikasi (4.2 MB)", fontSize = 12.sp)
                    }
                }
            }

            // Section 5: Informasi Aplikasi
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jadwale Mobile v1.4.0 (Build 2026.10)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E40AF))
                    }
                    Text("Penyusun Jadwal Otomatis Algoritma CSP Bebas Bentrok", fontSize = 11.sp, color = Color(0xFF3B82F6))
                    Text("Standar Kurikulum Merdeka • Kemdikbudristek RI", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Simpan & Tutup Pengaturan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 15. Dialog Pemilihan Sekolah Khusus Guru (Saat Login atau Ganti Sekolah Ajar)
 */
data class RegisteredSchoolOption(
    val name: String,
    val npsn: String,
    val address: String,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSchoolSelectionDialog(
    initialSchoolName: String = com.jadwale.core.model.SchoolConfig.schoolName,
    canDismiss: Boolean = true,
    onDismiss: () -> Unit = {},
    onSchoolSelected: (schoolName: String, npsn: String) -> Unit
) {
    val context = LocalContext.current
    val schoolOptions = remember {
        listOf(
            RegisteredSchoolOption("SDN Pancasila 01", "20104502", "Jl. Pendidikan No. 45, Jakarta Pusat", "12 Rombel (Paralel)"),
            RegisteredSchoolOption("SDN Percobaan 01", "20104501", "Jl. Merdeka Barat No. 12, Jakarta Pusat", "18 Rombel (Paralel)"),
            RegisteredSchoolOption("SDN Menteng 02", "20104503", "Jl. Cikini Raya No. 8, Jakarta Pusat", "6 Rombel (Tunggal)"),
            RegisteredSchoolOption("SDN Cibubur 03", "20104504", "Jl. Radar Auri No. 19, Jakarta Timur", "12 Rombel (Paralel)"),
            RegisteredSchoolOption("SDN Rawamangun 12", "20104505", "Jl. Pemuda No. 7, Jakarta Timur", "12 Rombel (Paralel)"),
            RegisteredSchoolOption("SDN Cempaka Putih 01", "20104506", "Jl. Percetakan Negara No. 15, Jakarta Pusat", "6 Rombel (Tunggal)")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedSchool by remember {
        mutableStateOf(
            schoolOptions.find { it.name.equals(initialSchoolName, ignoreCase = true) } ?: schoolOptions.first()
        )
    }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) schoolOptions
        else schoolOptions.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.npsn.contains(searchQuery, ignoreCase = true) ||
            it.address.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (canDismiss) onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
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
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pilih Sekolah Tempat Mengajar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Silakan tentukan pangkalan data sekolah",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sebagai guru terdaftar, Anda dapat memilih sekolah binaan untuk melihat jadwal mengajar mingguan dan jadwal kelas rombel.",
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama sekolah / NPSN...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = Color(0xFF1D68E4),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList.size) { index ->
                        val school = filteredList[index]
                        val isSelected = school.name == selectedSchool.name

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSchool = school },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSchool = school },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF1D68E4),
                                        unselectedColor = Color(0xFF94A3B8)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = school.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color(0xFF1D68E4) else Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9)
                                        ) {
                                            Text(
                                                text = school.type,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) Color(0xFF1E40AF) else Color(0xFF64748B),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "NPSN: ${school.npsn} • ${school.address}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    com.jadwale.core.model.SchoolConfig.schoolName = selectedSchool.name
                    com.jadwale.core.model.SchoolConfig.npsn = selectedSchool.npsn
                    com.jadwale.core.model.SchoolConfig.address = selectedSchool.address
                    Toast.makeText(context, "✓ Berhasil terhubung ke ${selectedSchool.name}", Toast.LENGTH_SHORT).show()
                    onSchoolSelected(selectedSchool.name, selectedSchool.npsn)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pilih Sekolah Ini", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            if (canDismiss) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Batal", fontSize = 13.sp, color = Color(0xFF64748B))
                }
            }
        }
    )
}


