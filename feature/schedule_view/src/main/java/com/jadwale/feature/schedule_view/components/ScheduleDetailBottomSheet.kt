package com.jadwale.feature.schedule_view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadwale.core.model.ScheduleSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailBottomSheet(
    slot: ScheduleSlot?,
    onDismiss: () -> Unit
) {
    if (slot == null) return

    val isRoutine = slot.isRoutine
    val title = if (isRoutine) {
        slot.routineActivity?.name ?: "Kegiatan Rutin"
    } else {
        slot.subject?.name ?: "Mata Pelajaran"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Detail Jadwal Pelajaran",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Detail Rows
                DetailItem(
                    icon = Icons.Default.Class,
                    label = "Kelas & Hari",
                    value = "Kelas ${slot.className} • Hari ${slot.day.displayName}"
                )

                DetailItem(
                    icon = Icons.Default.AccessTime,
                    label = "Jam Pelajaran",
                    value = "${slot.timeSlot.startTime} - ${slot.timeSlot.endTime} (JP ke-${slot.timeSlot.jpNumber})"
                )

                if (!isRoutine) {
                    DetailItem(
                        icon = Icons.Default.Person,
                        label = "Guru Pengampu",
                        value = slot.teacher?.name ?: "Belum ditentukan"
                    )

                    if (!slot.teacher?.nip.isNullOrBlank()) {
                        DetailItem(
                            icon = Icons.Default.Badge,
                            label = "NIP Guru",
                            value = slot.teacher?.nip ?: "-"
                        )
                    }

                    DetailItem(
                        icon = Icons.Default.MenuBook,
                        label = "Kode Mata Pelajaran",
                        value = "${slot.subject?.code} (${slot.subject?.jpPerWeek} JP/Minggu)"
                    )
                }

                DetailItem(
                    icon = Icons.Default.MeetingRoom,
                    label = "Lokasi / Ruangan",
                    value = slot.room
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
