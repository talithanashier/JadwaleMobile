package com.jadwale.feature.dashboard

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
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
import com.jadwale.core.session.AuthManager
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

/**
 * Layar Masuk 3-Peran Jadwale
 * Sesuai mockup: UI JADWALE/Layar Masuk 3-Peran - Jadwale.png
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: UserRole, accountName: String) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedRole by remember { mutableStateOf(UserRole.ADMIN_SEKOLAH) }
    var emailOrNip by remember { mutableStateOf("admin@sdnpercobaan.sch.id") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isDark = isSystemInDarkTheme()
    val logoRes = if (isDark) R.drawable.logo_jadwale_light else R.drawable.logo_jadwale_dark

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = "Logo Jadwale",
                            modifier = Modifier.height(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Jadwale",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Informasi",
                            tint = Color(0xFF64748B)
                        )
                    }
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
            // Header Title with Centered Brand Logo
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = "Logo Jadwale",
                    modifier = Modifier.height(44.dp).padding(bottom = 8.dp)
                )
                Text(
                    text = "Masuk ke Jadwale",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih peran Anda untuk melanjutkan",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            // Role 1: Admin Sekolah (Operator)
            LoginRoleCard(
                icon = Icons.Default.CorporateFare,
                title = "Admin Sekolah (Operator)",
                subtitle = "Kelola data kurikulum sekolah (Akun domain @*.sch.id)",
                isSelected = selectedRole == UserRole.ADMIN_SEKOLAH,
                onClick = {
                    selectedRole = UserRole.ADMIN_SEKOLAH
                    emailOrNip = "admin_final@sdnpancasila.sch.id"
                    password = "KatasandiRahasia123!"
                }
            )

            // Role 2: Guru Pengampu
            LoginRoleCard(
                icon = Icons.Default.School,
                title = "Guru Pengampu",
                subtitle = "Lihat jadwal mengajar mingguan (Akun @guru.sd.belajar.id)",
                isSelected = selectedRole == UserRole.GURU,
                onClick = {
                    selectedRole = UserRole.GURU
                    emailOrNip = "guru@guru.sd.belajar.id"
                    password = "KatasandiRahasia123!"
                }
            )

            // Role 3: Super Admin Sistem
            LoginRoleCard(
                icon = Icons.Default.Security,
                title = "Super Admin Sistem",
                subtitle = "Verifikasi sekolah, audit log & pemeliharaan server",
                isSelected = selectedRole == UserRole.SUPER_ADMIN,
                onClick = {
                    selectedRole = UserRole.SUPER_ADMIN
                    emailOrNip = "superadmin@jadwale.id"
                    password = "KatasandiRahasia123!"
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email / NIP Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Email / NIP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                OutlinedTextField(
                    value = emailOrNip,
                    onValueChange = { emailOrNip = it },
                    placeholder = { Text("Contoh: admin@sekolah.sch.id", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF1D68E4),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Password Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Kata Sandi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Masukkan kata sandi", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF1D68E4),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // School Status Verification Badge
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SDN Percobaan / SDN Pancasila terdeteksi aktif",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error Message Box
            if (errorMessage != null) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
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

            // Login Button
            Button(
                onClick = {
                    if (emailOrNip.isBlank()) {
                        Toast.makeText(context, "Silakan isi email atau NIP", Toast.LENGTH_SHORT).show()
                    } else if (password.isBlank()) {
                        Toast.makeText(context, "Silakan isi kata sandi", Toast.LENGTH_SHORT).show()
                    } else if (selectedRole == UserRole.ADMIN_SEKOLAH && emailOrNip.contains("@") && !emailOrNip.trim().endsWith(".sch.id", ignoreCase = true)) {
                        val msg = "Akun Admin Sekolah harus menggunakan email resmi berakhiran .sch.id"
                        errorMessage = msg
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else if (selectedRole == UserRole.GURU && emailOrNip.contains("@") && !emailOrNip.contains("belajar.id", ignoreCase = true)) {
                        val msg = "Akun Guru harus menggunakan email resmi belajar.id (contoh: @guru.sd.belajar.id)"
                        errorMessage = msg
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = AuthManager.login(emailOrNip, password)
                            isLoading = false
                            result.onSuccess { session ->
                                Toast.makeText(context, "✓ Berhasil masuk sebagai ${session.name} (${session.role.name})", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(session.role, session.name)
                            }.onFailure { err ->
                                val msg = err.localizedMessage ?: "Gagal terhubung ke API server Jadwale"
                                errorMessage = msg
                                Toast.makeText(context, "Gagal Masuk: $msg", Toast.LENGTH_LONG).show()
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
                        text = "Menghubungkan ke Server...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuk Sekarang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Forgot Password
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = {
                    Toast.makeText(context, "Silakan hubungi administrator sekolah Anda untuk mereset sandi.", Toast.LENGTH_LONG).show()
                }) {
                    Text(
                        text = "Lupa Kata Sandi?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D68E4)
                    )
                }
            }

            // Register Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum memiliki akun?",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Daftar Sekarang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D68E4)
                    )
                }
            }

            // Demo Accounts Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gunakan akun demo untuk mencoba fitur secara instan:",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DemoAccountButton(
                        label = "Isi Otomatis Admin SD",
                        bgColor = Color(0xFFDBEAFE),
                        textColor = Color(0xFF1D4ED8),
                        onClick = {
                            selectedRole = UserRole.ADMIN_SEKOLAH
                            emailOrNip = "admin_final@sdnpancasila.sch.id"
                            password = "KatasandiRahasia123!"
                            Toast.makeText(context, "Akun Admin Sekolah terisi", Toast.LENGTH_SHORT).show()
                        }
                    )

                    DemoAccountButton(
                        label = "Isi Otomatis Guru",
                        bgColor = Color.White,
                        textColor = Color(0xFF0F172A),
                        onClick = {
                            selectedRole = UserRole.GURU
                            emailOrNip = "guru@guru.sd.belajar.id"
                            password = "KatasandiRahasia123!"
                            Toast.makeText(context, "Akun Guru (@guru.sd.belajar.id) terisi", Toast.LENGTH_SHORT).show()
                        }
                    )

                    DemoAccountButton(
                        label = "Isi Otomatis Superadmin",
                        bgColor = Color.White,
                        textColor = Color(0xFF0F172A),
                        onClick = {
                            selectedRole = UserRole.SUPER_ADMIN
                            emailOrNip = "superadmin@jadwale.id"
                            password = "KatasandiRahasia123!"
                            Toast.makeText(context, "Akun Super Admin terisi", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }


    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D68E4)) },
            title = { Text("Tentang Masuk Jadwale", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Jadwale mendukung 3 peran pengguna berbasis RBAC:", fontSize = 12.sp, color = Color(0xFF334155))
                    Text("1. Admin Sekolah: Akses penuh generator AI, rombel, guru & mapel.", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text("2. Guru Pengampu: Jadwal mengajar pribadi dan presensi.", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text("3. Super Admin: Monitoring multi-sekolah dan kesehatan server.", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text("4. Tamu Publik: Melihat jadwal kelas tanpa perlu akun.", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) { Text("Mengerti") }
            }
        )
    }
}

@Composable
private fun LoginRoleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
        ),
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF1D68E4) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF1D68E4) else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) Color(0xFF1D68E4) else Color(0xFFCBD5E1),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoAccountButton(
    label: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
