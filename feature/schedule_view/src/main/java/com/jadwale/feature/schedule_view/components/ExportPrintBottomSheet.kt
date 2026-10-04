package com.jadwale.feature.schedule_view.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportPrintBottomSheet(
    className: String = "Kelas 3A",
    schoolName: String = "SDN Pancasila 01 Pagi",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormatIndex by remember { mutableIntStateOf(0) } // 0: Dinas, 1: Warna Ceria, 2: Ringkas Saku
    var includeRoutines by remember { mutableStateOf(true) }
    var includeHomeroomPhone by remember { mutableStateOf(true) }
    var showDownloadedDialog by remember { mutableStateOf(false) }

    val shareText = "Yth. Bapak/Ibu Wali Murid $className, berikut kami lampirkan Jadwal Pelajaran Resmi Semester Ganjil $schoolName. Jadwal dapat dilihat interaktif di: jadwale.id/share/sdn-pancasila-3a"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            tint = Color(0xFF1D68E4),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LAYANAN MANDIRI GURU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D68E4),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Distribusi & Cetak Berkas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$schoolName • $className",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color(0xFF64748B))
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Section 1: Pilih Format Dokumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Format Dokumen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "3 Format",
                    fontSize = 11.sp,
                    color = Color(0xFF1D68E4),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Format Option 1: Standar Resmi Dinas Kemendikbud
            FormatOptionCard(
                title = "Standar Resmi Dinas Kemendikbud",
                description = "Format tabel formal dengan Kop Dinas Pendidikan, NIP Kepala Sekolah, stempel basah elektronik, serta barcode validasi.",
                badges = listOf("Kop Dinas", "NIP & TTD", "Sah Arsip Sekolah"),
                icon = Icons.Default.VerifiedUser,
                isSelected = selectedFormatIndex == 0,
                onClick = { selectedFormatIndex = 0 }
            )

            // Format Option 2: Warna Ceria Pastel
            FormatOptionCard(
                title = "Warna Ceria Pastel",
                description = "Warna latar lembut pembeda mata pelajaran. Sangat ramah dibaca peserta didik dan menarik untuk mading kelas.",
                badges = listOf("Mading Kelas", "Blok Warna Mata Pelajaran"),
                icon = Icons.Default.Palette,
                isSelected = selectedFormatIndex == 1,
                onClick = { selectedFormatIndex = 1 }
            )

            // Format Option 3: Ringkas Saku Guru
            FormatOptionCard(
                title = "Ringkas Saku Guru",
                description = "Khusus agenda mingguan guru 1 lembar lipat 3. Ringkas dan praktis disimpan dalam binder atau saku map kerja.",
                badges = listOf("Agenda Pribadi", "Lipat Portabel"),
                icon = Icons.Default.MenuBook,
                isSelected = selectedFormatIndex == 2,
                onClick = { selectedFormatIndex = 2 }
            )

            // Section 2: Parameter Cetak
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Parameter Cetak", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    }

                    Column {
                        Text("Ukuran & Tata Letak Kertas", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("A4 Landscape (Mendatar) - Rekomendasi Utama", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("✨ Landscape menampilkan 5 hari kerja tanpa terpotong", fontSize = 10.sp, color = Color(0xFF15803D))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Switch 1: Istirahat & Upacara
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sertakan Jam Istirahat & Upacara", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Text("Menampilkan slot upacara hari Senin, senam, & istirahat", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Switch(checked = includeRoutines, onCheckedChange = { includeRoutines = it })
                    }

                    // Switch 2: Kontak Wali Kelas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sertakan Nomor Kontak Wali Kelas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Text("Nomor kontak wali kelas pada catatan kaki dokumen", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Switch(checked = includeHomeroomPhone, onCheckedChange = { includeHomeroomPhone = it })
                    }
                }
            }

            // Section 3: Kirim Cepat WhatsApp
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
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kirim Cepat WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Tautan Aktif", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }

                    Text(
                        text = "Pesan akan langsung tersalin ke kolom obrolan WhatsApp saat aplikasi terbuka:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    // Text Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("TEKS SIAP KIRIM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"$shareText\"",
                                fontSize = 11.sp,
                                color = Color(0xFF334155),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Jadwal WhatsApp", shareText))
                                        Toast.makeText(context, "Teks pesan WhatsApp berhasil disalin!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF1D68E4), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salin Teks Pesan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D68E4))
                                }
                            }
                        }
                    }

                    // Direct WhatsApp Share Button
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            try {
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Jadwal via WhatsApp"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Tidak dapat membuka aplikasi berbagi.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Langsung ke Grup WhatsApp Kelas", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            // Bottom Actions: Download PDF & Browser Print
            Button(
                onClick = {
                    showDownloadedDialog = true
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unduh Berkas PDF Siap Cetak (A4)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "Membuka pratinjau cetak sistem...", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF334155), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka Pratinjau Printer Browser (@media print)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            }

            // Disclaimer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dokumen resmi terenkripsi sesuai panduan Kurikulum Merdeka", fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
        }
    }

    if (showDownloadedDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadedDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(32.dp)) },
            title = { Text("Berkas PDF Berhasil Dibuat!") },
            text = { Text("Jadwal resmi $className ($schoolName) format A4 Landscape telah selesai diproses dan siap dicetak atau dibagikan ke wali murid.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDownloadedDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D68E4))
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}

@Composable
private fun FormatOptionCard(
    title: String,
    description: String,
    badges: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) Color(0xFF1D68E4) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFF1D68E4) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = description, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1D68E4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                badges.forEach { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = badge, fontSize = 9.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
