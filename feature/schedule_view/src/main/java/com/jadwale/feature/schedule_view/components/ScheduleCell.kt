package com.jadwale.feature.schedule_view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadwale.core.model.RoutineType
import com.jadwale.core.model.ScheduleSlot

@Composable
fun ScheduleCell(
    slot: ScheduleSlot?,
    onClick: (ScheduleSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    if (slot == null) {
        Box(
            modifier = modifier
                .height(72.dp)
                .width(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF8FAFC))
                .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
        )
        return
    }

    val isRoutine = slot.isRoutine
    val routineType = slot.routineActivity?.type ?: slot.timeSlot.routineType

    val colorStyle = when {
        routineType == RoutineType.UPACARA -> SubjectColorPalette.RoutineUpacara
        routineType == RoutineType.ISTIRAHAT -> SubjectColorPalette.RoutineBreak
        routineType == RoutineType.PEMBIASAAN -> SubjectColorPalette.RoutinePembiasaan
        else -> SubjectColorPalette.getColorForSubject(slot.subject?.name, slot.subject?.code)
    }

    Box(
        modifier = modifier
            .height(72.dp)
            .width(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorStyle.backgroundColor)
            .border(1.dp, colorStyle.borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick(slot) }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRoutine) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val icon = when (routineType) {
                    RoutineType.UPACARA -> Icons.Default.Flag
                    RoutineType.ISTIRAHAT -> Icons.Default.FreeBreakfast
                    else -> Icons.Default.School
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorStyle.textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = slot.routineActivity?.name ?: "Istirahat",
                    color = colorStyle.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = slot.subject?.name ?: slot.subject?.code ?: "-",
                    color = colorStyle.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )
                Text(
                    text = slot.teacher?.name?.split(",")?.firstOrNull() ?: "-",
                    color = colorStyle.textColor.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
