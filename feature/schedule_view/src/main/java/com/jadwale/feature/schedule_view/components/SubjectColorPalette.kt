package com.jadwale.feature.schedule_view.components

import androidx.compose.ui.graphics.Color

/**
 * Palet Warna Resmi Mata Pelajaran Jadwale Mobile
 * Berdasarkan spesifikasi visual Material 3
 */
data class SubjectColorStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color
)

object SubjectColorPalette {

    // Palet Warna Pastel Harmonis
    val BlueMath = SubjectColorStyle(
        backgroundColor = Color(0xFFE0F2FE), // Sky 100
        textColor = Color(0xFF0369A1),       // Sky 700
        borderColor = Color(0xFF7DD3FC)       // Sky 300
    )

    val GreenIPA = SubjectColorStyle(
        backgroundColor = Color(0xFFDCFCE7), // Emerald 100
        textColor = Color(0xFF15803D),       // Emerald 700
        borderColor = Color(0xFF86EFAC)       // Emerald 300
    )

    val OrangeIPS = SubjectColorStyle(
        backgroundColor = Color(0xFFFFEDD5), // Orange 100
        textColor = Color(0xFFC2410C),       // Orange 700
        borderColor = Color(0xFFFDBA74)       // Orange 300
    )

    val RedBIndo = SubjectColorStyle(
        backgroundColor = Color(0xFFFEE2E2), // Rose 100
        textColor = Color(0xFFBE123C),       // Rose 700
        borderColor = Color(0xFFFDA4AF)       // Rose 300
    )

    val PurpleBIng = SubjectColorStyle(
        backgroundColor = Color(0xFFF3E8FF), // Purple 100
        textColor = Color(0xFF7E22CE),       // Purple 700
        borderColor = Color(0xFFD8B4FE)       // Purple 300
    )

    val YellowPPKn = SubjectColorStyle(
        backgroundColor = Color(0xFFFEF9C3), // Amber 100
        textColor = Color(0xFFA16207),       // Amber 700
        borderColor = Color(0xFFFDE047)       // Amber 300
    )

    val TealAgama = SubjectColorStyle(
        backgroundColor = Color(0xFFCCFBF1), // Teal 100
        textColor = Color(0xFF0F766E),       // Teal 700
        borderColor = Color(0xFF5EEAD4)       // Teal 300
    )

    val PinkPJOK = SubjectColorStyle(
        backgroundColor = Color(0xFFFCE7F3), // Pink 100
        textColor = Color(0xFFBE185D),       // Pink 700
        borderColor = Color(0xFFF472B6)       // Pink 300
    )

    val BrownSeni = SubjectColorStyle(
        backgroundColor = Color(0xFFF5EBE6), // Warm Brown tint
        textColor = Color(0xFF78350F),       // Brown 700
        borderColor = Color(0xFFD7CCC8)       // Brown 300
    )

    val GrayMulok = SubjectColorStyle(
        backgroundColor = Color(0xFFF1F5F9), // Slate 100
        textColor = Color(0xFF475569),       // Slate 700
        borderColor = Color(0xFFCBD5E1)       // Slate 300
    )

    // Rutinitas (Upacara, Istirahat, Pembiasaan)
    val RoutineUpacara = SubjectColorStyle(
        backgroundColor = Color(0xFFE2E8F0), // Slate 200
        textColor = Color(0xFF1E293B),       // Slate 800
        borderColor = Color(0xFF94A3B8)       // Slate 400
    )

    val RoutineBreak = SubjectColorStyle(
        backgroundColor = Color(0xFFF8FAFC), // Slate 50
        textColor = Color(0xFF64748B),       // Slate 500
        borderColor = Color(0xFFE2E8F0)       // Slate 200
    )

    val RoutinePembiasaan = SubjectColorStyle(
        backgroundColor = Color(0xFFE0E7FF), // Indigo 100
        textColor = Color(0xFF4338CA),       // Indigo 700
        borderColor = Color(0xFFA5B4FC)       // Indigo 300
    )

    fun getColorForSubject(subjectName: String?, code: String?): SubjectColorStyle {
        val s = (subjectName ?: code ?: "").lowercase()
        return when {
            s.contains("mat") || s.contains("mtk") -> BlueMath
            s.contains("ipa") || s.contains("alam") -> GreenIPA
            s.contains("ips") || s.contains("sosial") -> OrangeIPS
            s.contains("indonesia") || s.contains("bind") -> RedBIndo
            s.contains("inggris") || s.contains("bing") -> PurpleBIng
            s.contains("ppkn") || s.contains("pancasila") -> YellowPPKn
            s.contains("agama") || s.contains("pai") -> TealAgama
            s.contains("pjok") || s.contains("olahraga") -> PinkPJOK
            s.contains("seni") || s.contains("sbdp") -> BrownSeni
            s.contains("mulok") || s.contains("jawa") || s.contains("sunda") -> GrayMulok
            else -> GrayMulok
        }
    }
}
