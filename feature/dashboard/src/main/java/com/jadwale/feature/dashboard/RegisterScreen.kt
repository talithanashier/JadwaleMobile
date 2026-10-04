package com.jadwale.feature.dashboard

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadwale.core.model.UserRole
import com.jadwale.core.network.SekolahDto
import com.jadwale.core.session.AuthManager
import kotlinx.coroutines.launch

enum class RegisterRole(val label: String, val subtitle: String) {
    GURU("Guru Pengampu", "Akun @guru.sd.belajar.id"),
    ADMIN_SEKOLAH("Admin Sekolah", "Akun domain @*.sch.id")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (role: UserRole, accountName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedRole by remember { mutableStateOf(RegisterRole.GURU) }

    // Form fields - Guru
    var namaGuru by remember { mutableStateOf("") }
    var nipGuru by remember { mutableStateOf("") }
    var emailGuru by remember { mutableStateOf("") }
    var passwordGuru by remember { mutableStateOf("") }
    var passwordGuruVisible by remember { mutableStateOf(false) }
    var schoolList by remember { mutableStateOf<List<SekolahDto>>(emptyList()) }
    var selectedSekolah by remember { mutableStateOf<SekolahDto?>(null) }
    var schoolDropdownExpanded by remember { mutableStateOf(false) }
    var isLoadingSchools by remember { mutableStateOf(false) }

    // Form fields - Admin Sekolah
    var namaAdmin by remember { mutableStateOf("") }
    var namaSekolah by remember { mutableStateOf("") }
    var npsnSekolah by remember { mutableStateOf("") }
    var emailSekolah by remember { mutableStateOf("") }
    var passwordSekolah by remember { mutableStateOf("") }
    var passwordSekolahVisible by remember { mutableStateOf(false) }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successDialogMessage by remember { mutableStateOf<String?>(null) }

    // Fetch school list on launch
    LaunchedEffect(Unit) {
        isLoadingSchools = true
        coroutineScope.launch {
            AuthManager.getSchoolList().onSuccess { list ->
                schoolList = list
                if (list.isNotEmpty() && selectedSekolah == null) {
                    selectedSekolah = list.first()
                }
            }.onFailure {
                schoolList = emptyList()
            }
            isLoadingSchools = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Login",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Pendaftaran Akun",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Column {
                Text(
                    text = "Daftar Akun Baru",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih kategori pendaftaran sesuai peran Anda",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Role Selector Tabs: GURU (Utama) & ADMIN SEKOLAH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RegisterRoleTab(
                    title = "Guru Pengampu",
                    subtitle = "Akun @guru.sd.belajar.id",
                    icon = Icons.Default.School,
                    isSelected = selectedRole == RegisterRole.GURU,
                    onClick = {
                        selectedRole = RegisterRole.GURU
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                RegisterRoleTab(
                    title = "Admin Sekolah",
                    subtitle = "Akun domain @*.sch.id",
                    icon = Icons.Default.CorporateFare,
                    isSelected = selectedRole == RegisterRole.ADMIN_SEKOLAH,
                    onClick = {
                        selectedRole = RegisterRole.ADMIN_SEKOLAH
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Alur & Verifikasi Info Box
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedRole == RegisterRole.GURU) Color(0xFFF0FDF4) else Color(0xFFEFF6FF)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedRole == RegisterRole.GURU) Color(0xFFBBF7D0) else Color(0xFFBFDBFE)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedRole == RegisterRole.GURU) Icons.Default.VerifiedUser else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (selectedRole == RegisterRole.GURU) Color(0xFF15803D) else Color(0xFF1D4ED8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedRole == RegisterRole.GURU) "Alur Pendaftaran & Verifikasi Guru" else "Alur Pendaftaran Sekolah",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == RegisterRole.GURU) Color(0xFF166534) else Color(0xFF1E40AF)
                        )
                    }

                    Text(
                        text = if (selectedRole == RegisterRole.GURU) {
                            "1. Sekolah Anda harus sudah terdaftar dan diverifikasi oleh Super Admin.\n" +
                            "2. Masukkan identitas Anda menggunakan email resmi @guru.sd.belajar.id.\n" +
                            "3. Admin Sekolah (@*.sch.id) akan memverifikasi apakah Anda benar guru dari sekolah tersebut sebelum akses dibuka."
                        } else {
                            "1. Sekolah mendaftarkan identitas sekolah menggunakan email resmi @*.sch.id.\n" +
                            "2. Super Admin memverifikasi keabsahan data sekolah.\n" +
                            "3. Setelah disetujui, nama sekolah muncul pada opsi pilihan guru untuk mendaftar."
                        },
                        fontSize = 12.sp,
                        color = if (selectedRole == RegisterRole.GURU) Color(0xFF15803D) else Color(0xFF1E40AF),
                        lineHeight = 17.sp
                    )
                }
            }

            // Form Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (selectedRole == RegisterRole.GURU) "Form Registrasi Guru Pengampu" else "Form Registrasi Sekolah Baru",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    if (selectedRole == RegisterRole.GURU) {
                        // 1. Pilih Sekolah Asal Mengajar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Pilih Sekolah Tempat Bertugas *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )

                            if (isLoadingSchools) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF1D68E4)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Memuat daftar sekolah terverifikasi...", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                            } else if (schoolList.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Belum ada sekolah terverifikasi di server. Hubungi Admin Sekolah Anda untuk mendaftarkan sekolah terlebih dahulu.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            } else {
                                ExposedDropdownMenuBox(
                                    expanded = schoolDropdownExpanded,
                                    onExpandedChange = { schoolDropdownExpanded = !schoolDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedSekolah?.namaSekolah ?: "Pilih Sekolah",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = schoolDropdownExpanded)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = Color(0xFF1D68E4),
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = schoolDropdownExpanded,
                                        onDismissRequest = { schoolDropdownExpanded = false }
                                    ) {
                                        schoolList.forEach { item ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = item.namaSekolah ?: "Sekolah",
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp
                                                        )
                                                        if (!item.npsn.isNullOrBlank()) {
                                                            Text(
                                                                text = "NPSN: ${item.npsn}",
                                                                fontSize = 11.sp,
                                                                color = Color(0xFF64748B)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedSekolah = item
                                                    schoolDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Nama Lengkap Guru
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nama Lengkap Guru *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = namaGuru,
                                onValueChange = { namaGuru = it },
                                placeholder = { Text("Contoh: Siti Rahmawati, S.Pd", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 3. NIP / NUPTK
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("NIP / NUPTK (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = nipGuru,
                                onValueChange = { nipGuru = it },
                                placeholder = { Text("Contoh: 198503152010011005", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 4. Email Belajar.id
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Email Akun Belajar.id Guru *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = emailGuru,
                                onValueChange = { emailGuru = it },
                                placeholder = { Text("Contoh: nama@guru.sd.belajar.id", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Wajib menggunakan akun resmi @guru.sd.belajar.id atau domain belajar.id",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D)
                            )
                        }

                        // 5. Kata Sandi
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Kata Sandi *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = passwordGuru,
                                onValueChange = { passwordGuru = it },
                                placeholder = { Text("Minimal 6 karakter", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordGuruVisible = !passwordGuruVisible }) {
                                        Icon(
                                            imageVector = if (passwordGuruVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordGuruVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    } else {
                        // Form Registrasi Admin Sekolah
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nama Administrator / Operator *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = namaAdmin,
                                onValueChange = { namaAdmin = it },
                                placeholder = { Text("Contoh: Bpk. Bambang Sutrisno, M.Pd", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nama Sekolah *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = namaSekolah,
                                onValueChange = { namaSekolah = it },
                                placeholder = { Text("Contoh: SDN Pancasila 01 Jakarta", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("NPSN Sekolah (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = npsnSekolah,
                                onValueChange = { npsnSekolah = it },
                                placeholder = { Text("Contoh: 20104567", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Email Resmi Sekolah (*.sch.id) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = emailSekolah,
                                onValueChange = { emailSekolah = it },
                                placeholder = { Text("Contoh: admin@sdnpancasila.sch.id", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Wajib menggunakan domain resmi sekolah berakhiran .sch.id",
                                fontSize = 11.sp,
                                color = Color(0xFF1E40AF)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Kata Sandi *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            OutlinedTextField(
                                value = passwordSekolah,
                                onValueChange = { passwordSekolah = it },
                                placeholder = { Text("Minimal 6 karakter", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordSekolahVisible = !passwordSekolahVisible }) {
                                        Icon(
                                            imageVector = if (passwordSekolahVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordSekolahVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF1D68E4),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Error Message Banner
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    fontSize = 12.sp,
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedRole == RegisterRole.GURU) {
                                // Validasi Guru
                                if (selectedSekolah == null) {
                                    errorMessage = "Silakan pilih sekolah tempat Anda mengajar."
                                    return@Button
                                }
                                if (namaGuru.isBlank()) {
                                    errorMessage = "Silakan isi nama lengkap Anda."
                                    return@Button
                                }
                                val cleanEmail = emailGuru.trim()
                                if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                    errorMessage = "Silakan masukkan alamat email yang valid."
                                    return@Button
                                }
                                if (!cleanEmail.contains("belajar.id", ignoreCase = true)) {
                                    errorMessage = "Akun Guru wajib menggunakan email resmi @guru.sd.belajar.id atau domain belajar.id."
                                    return@Button
                                }
                                if (passwordGuru.length < 6) {
                                    errorMessage = "Kata sandi minimal 6 karakter."
                                    return@Button
                                }

                                val schoolId = selectedSekolah!!.id ?: 1
                                val schoolName = selectedSekolah!!.namaSekolah ?: "Sekolah Terpilih"
                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val res = AuthManager.registerTeacher(
                                        nama = namaGuru,
                                        email = cleanEmail,
                                        pass = passwordGuru,
                                        idSekolah = schoolId,
                                        nip = nipGuru.ifBlank { null }
                                    )
                                    isLoading = false
                                    res.onSuccess { msg ->
                                        com.jadwale.core.session.TeacherVerificationStore.addPendingTeacher(
                                            name = namaGuru,
                                            email = cleanEmail,
                                            nip = nipGuru.ifBlank { null },
                                            schoolId = schoolId,
                                            schoolName = schoolName
                                        )
                                        successDialogMessage = "Pendaftaran Berhasil!\n\n$msg\n\nAdmin Sekolah ($schoolName) akan memverifikasi apakah Anda benar merupakan guru dari sekolah tersebut sebelum akun aktif."
                                    }.onFailure { err ->
                                        errorMessage = err.localizedMessage ?: "Pendaftaran guru gagal."
                                    }
                                }

                            } else {
                                // Validasi Admin Sekolah
                                if (namaAdmin.isBlank()) {
                                    errorMessage = "Silakan isi nama administrator sekolah."
                                    return@Button
                                }
                                if (namaSekolah.isBlank()) {
                                    errorMessage = "Silakan isi nama sekolah Anda."
                                    return@Button
                                }
                                val cleanEmail = emailSekolah.trim()
                                if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                    errorMessage = "Silakan masukkan alamat email yang valid."
                                    return@Button
                                }
                                if (!cleanEmail.endsWith(".sch.id", ignoreCase = true)) {
                                    errorMessage = "Akun Sekolah wajib menggunakan email resmi berakhiran .sch.id (contoh: admin@sdnpancasila.sch.id)."
                                    return@Button
                                }
                                if (passwordSekolah.length < 6) {
                                    errorMessage = "Kata sandi minimal 6 karakter."
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val res = AuthManager.registerSchool(
                                        nama = namaAdmin,
                                        email = cleanEmail,
                                        pass = passwordSekolah,
                                        namaSekolah = namaSekolah,
                                        npsn = npsnSekolah.ifBlank { null }
                                    )
                                    isLoading = false
                                    res.onSuccess { msg ->
                                        com.jadwale.core.session.SchoolVerificationStore.addPendingSchool(
                                            namaAdmin = namaAdmin,
                                            emailSekolah = cleanEmail,
                                            namaSekolah = namaSekolah,
                                            npsn = npsnSekolah.ifBlank { null }
                                        )
                                        successDialogMessage = "Pendaftaran Sekolah Berhasil!\n\n$msg\n\nData sekolah sedang menunggu verifikasi dari Super Admin. Setelah disetujui, guru dapat memilih sekolah ini saat mendaftar."
                                    }.onFailure { err ->
                                        errorMessage = err.localizedMessage ?: "Pendaftaran sekolah gagal."
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Memproses Pendaftaran...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedRole == RegisterRole.GURU) "Daftar Sebagai Guru" else "Daftarkan Sekolah Baru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Already have account? Back to Login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah memiliki akun?",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Masuk di Sini",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D68E4)
                    )
                }
            }
        }
    }

    // Success Dialog
    if (successDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                successDialogMessage = null
                onNavigateToLogin()
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Pendaftaran Berhasil!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = successDialogMessage ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        successDialogMessage = null
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ke Halaman Masuk")
                }
            }
        )
    }
}

@Composable
private fun RegisterRoleTab(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
    val contentColor = if (isSelected) Color(0xFF1D68E4) else Color(0xFF475569)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
