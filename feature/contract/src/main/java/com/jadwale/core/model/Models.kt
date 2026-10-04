package com.jadwale.core.model

/**
 * Kontrak Model Entitas Aplikasi Jadwale Mobile
 * Sesuai spesifikasi teknis arsitektur multi-module
 */

enum class DayOfWeek(val displayName: String, val index: Int) {
    SENIN("Senin", 1),
    SELASA("Selasa", 2),
    RABU("Rabu", 3),
    KAMIS("Kamis", 4),
    JUMAT("Jumat", 5),
    SABTU("Sabtu", 6)
}

enum class RoutineType(val displayName: String) {
    UPACARA("Upacara Bendera"),
    PEMBIASAAN("Pembiasaan Pagi"),
    ISTIRAHAT("Istirahat")
}

data class TimeSlot(
    val id: String,
    val jpNumber: Int, // 1, 2, 3, dst. 0 untuk upacara/pembiasaan
    val startTime: String, // "07:00"
    val endTime: String,   // "07:45"
    val isRoutine: Boolean = false,
    val routineType: RoutineType? = null
)

enum class TeacherType(val displayName: String) {
    MAPEL("Guru Mata Pelajaran"),
    WALI_KELAS("Guru Wali Kelas"),
    KEDUANYA("Guru Mapel & Wali Kelas")
}

enum class TeacherStatus(val displayName: String) {
    PNS("PNS (Pegawai Negeri Sipil)"),
    PPPK("PPPK (P3K)"),
    HONORER("Guru Honorer / Tetap Yayasan")
}

data class Teacher(
    val id: String,
    val name: String,
    val nip: String,
    val email: String = "",
    val phone: String = "",
    val subjects: List<String> = emptyList(), // ID atau nama mapel
    val totalJp: Int = 24,
    val teacherType: TeacherType = TeacherType.KEDUANYA,
    val teacherStatus: TeacherStatus = TeacherStatus.PNS,
    val availabilityNote: String = "Senin - Jumat Bersedia Penuh",
    val availableDays: List<DayOfWeek> = listOf(DayOfWeek.SENIN, DayOfWeek.SELASA, DayOfWeek.RABU, DayOfWeek.KAMIS, DayOfWeek.JUMAT),
    val isVerified: Boolean = true,
    val schoolId: String = "default_school"
)

data class ClassRoom(
    val id: String,
    val name: String, // "1A", "1B", ... "6B"
    val grade: Int,   // 1 s/d 6
    val studentCount: Int = 28,
    val homeroomTeacher: String = "", // Nama wali kelas
    val weeklyJp: Int = 30,
    val curriculumPhase: String = "Fase A"
)

data class Subject(
    val id: String,
    val code: String, // "MTK", "IPA", dll
    val name: String, // "Matematika"
    val jpPerWeek: Int = 4,
    val colorCategory: String = "Matematika", // Untuk styling warna
    val isPriority: Boolean = false,
    val defaultTeacher: String = ""
)

data class RoutineActivity(
    val id: String,
    val name: String,
    val type: RoutineType,
    val day: DayOfWeek,
    val startTime: String,
    val endTime: String
)

data class Assignment(
    val id: String,
    val teacherId: String,
    val teacherName: String,
    val subjectId: String,
    val subjectName: String,
    val classId: String,
    val className: String,
    val totalJp: Int
)

data class ScheduleSlot(
    val id: String,
    val classId: String,
    val className: String,
    val day: DayOfWeek,
    val timeSlot: TimeSlot,
    val subject: Subject? = null,
    val teacher: Teacher? = null,
    val routineActivity: RoutineActivity? = null,
    val room: String = "Ruang Kelas"
) {
    val isRoutine: Boolean get() = routineActivity != null || timeSlot.isRoutine
}

data class Schedule(
    val id: String,
    val schoolId: String,
    val academicYear: String = "2026/2027",
    val semester: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val slots: List<ScheduleSlot> = emptyList()
)

data class GenerateProgress(
    val progress: Float, // 0.0f s/d 1.0f
    val message: String,
    val result: Schedule? = null
)

enum class UserRole {
    SUPER_ADMIN,
    ADMIN_SEKOLAH,
    GURU,
    UMUM
}

object SchoolConfig {
    var isParallel: Boolean = true // true: 12 Rombel (1A s/d 6B), false: 6 Kelas Tunggal (1 s/d 6)
    var isUserExplicitlySetParallel: Boolean = false
    var daysCount: Int = 5 // 5: Senin-Jumat, 6: Senin-Sabtu
    var schoolName: String = "SDN Pancasila 01"
    var npsn: String = "20108922"
    var logoUrl: String = ""
    var address: String = "Jl. Merdeka No. 45, Gambir, Jakarta Pusat"
    var headmaster: String = "Drs. H. Subagyo, M.M"
    var headmasterNip: String = "19680512 199303 1 004"
    var academicYear: String = "2025/2026"
    var semester: String = "Ganjil"
    var isVerified: Boolean = true

    // Parameter kegiatan rutin & istirahat
    var hasMondayCeremony: Boolean = true
    var hasRoutine: Boolean = true
    var hasSenam: Boolean = true
    var break1Duration: Int = 15
    var break1AfterJp: Int = 3
    var break2Duration: Int = 15
    var break2AfterJp: Int = 5

    var isUpacaraActive: Boolean
        get() = hasMondayCeremony
        set(value) { hasMondayCeremony = value }
    var isPembiasaanActive: Boolean
        get() = hasRoutine
        set(value) { hasRoutine = value }
    var isSenamActive: Boolean
        get() = hasSenam
        set(value) { hasSenam = value }
    var break1DurationMinutes: Int
        get() = break1Duration
        set(value) { break1Duration = value }
    var break2DurationMinutes: Int
        get() = break2Duration
        set(value) { break2Duration = value }
}

data class SystemModuleItem(
    val id: String,
    val name: String,
    val description: String,
    var isEnabled: Boolean = true,
    val category: String = "Fitur Sistem"
)

object UserProfileConfig {
    var name: String = ""
    var email: String = ""
    var phone: String = "081234567890"
    var nip: String = "19820315 200801 1 008"

    fun getNameForRole(role: UserRole): String {
        if (name.isNotBlank()) return name
        return when (role) {
            UserRole.ADMIN_SEKOLAH -> "Operator ${SchoolConfig.schoolName}"
            UserRole.GURU -> "Bpk. Bambang Sutrisno, M.Pd"
            UserRole.SUPER_ADMIN -> "Pengawas & Super Admin Wilayah"
            UserRole.UMUM -> "Tamu Publik / Orang Tua Siswa"
        }
    }

    fun getEmailForRole(role: UserRole): String {
        if (email.isNotBlank()) return email
        return when (role) {
            UserRole.ADMIN_SEKOLAH -> "operator@sdnpancasila01.sch.id"
            UserRole.GURU -> "bambang@guru.sd.belajar.id"
            UserRole.SUPER_ADMIN -> "superadmin@dinas.diknas.go.id"
            UserRole.UMUM -> "tamu@jadwale.id"
        }
    }
}

