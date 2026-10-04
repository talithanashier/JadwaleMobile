package com.jadwale.core.mock

import com.jadwale.core.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object MockDataProvider {

    val parallelClassList = listOf(
        ClassRoom("c1", "1A", 1, 28, "Siti Aminah, S.Pd.", 30, "Fase A • Tematik Terpadu"),
        ClassRoom("c2", "1B", 1, 28, "Dewi Lestari, S.Pd.", 30, "Fase A • Tematik Terpadu"),
        ClassRoom("c3", "2A", 2, 30, "Hendra Wijaya, S.Pd.", 32, "Fase A • Tematik Terpadu"),
        ClassRoom("c4", "2B", 2, 29, "Nurul Hidayah, S.Pd.", 32, "Fase A • Tematik Terpadu"),
        ClassRoom("c5", "3A", 3, 30, "Bambang Sutrisno, M.Pd.", 34, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c6", "3B", 3, 31, "Eko Prasetyo, S.Pd.", 34, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c7", "4A", 4, 32, "Hendra Gunawan, M.Pd.", 36, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c8", "4B", 4, 30, "Nurul Aini, S.Pd.", 36, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c9", "5A", 5, 32, "Fajar Nugraha, S.Pd.", 36, "Fase C • Kurikulum Merdeka"),
        ClassRoom("c10", "5B", 5, 31, "Maya Safitri, S.Pd.", 36, "Fase C • Kurikulum Merdeka"),
        ClassRoom("c11", "6A", 6, 30, "Bambang Wijaya, M.Pd.", 36, "Fase C • Kurikulum Merdeka"),
        ClassRoom("c12", "6B", 6, 29, "Tri Wahyuni, S.Pd.", 36, "Fase C • Kurikulum Merdeka")
    )

    val singleClassList = listOf(
        ClassRoom("c1", "1", 1, 28, "Siti Aminah, S.Pd.", 30, "Fase A • Tematik Terpadu"),
        ClassRoom("c3", "2", 2, 30, "Hendra Wijaya, S.Pd.", 32, "Fase A • Tematik Terpadu"),
        ClassRoom("c5", "3", 3, 30, "Bambang Sutrisno, M.Pd.", 34, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c7", "4", 4, 32, "Hendra Gunawan, M.Pd.", 36, "Fase B • Kurikulum Merdeka"),
        ClassRoom("c9", "5", 5, 32, "Fajar Nugraha, S.Pd.", 36, "Fase C • Kurikulum Merdeka"),
        ClassRoom("c11", "6", 6, 30, "Bambang Wijaya, M.Pd.", 36, "Fase C • Kurikulum Merdeka")
    )

    val classList: List<ClassRoom>
        get() = if (SchoolConfig.isParallel) parallelClassList else singleClassList

    val teacherList = listOf(
        Teacher("t1", "Bambang Sutrisno, M.Pd.", "198204152008011008", "bambang@sd.belajar.id", "081234567801", listOf("Matematika"), 24),
        Teacher("t2", "Siti Aminah, S.Pd.", "198811202012022004", "siti@sd.belajar.id", "081234567802", listOf("Bahasa Indonesia"), 22),
        Teacher("t3", "Ahmad Fauzi, S.Ag.", "198005202003121003", "ahmad@sd.belajar.id", "081234567803", listOf("Pendidikan Agama Islam"), 16),
        Teacher("t4", "Ratna Dewi, S.Pd.", "199305122019032015", "ratna@sd.belajar.id", "081234567804", listOf("SBdP & Kesenian"), 18),
        Teacher("t5", "Dewi Lestari, S.Pd.", "198511252009022004", "dewi@sd.belajar.id", "081234567805", listOf("Pendidikan Pancasila"), 20),
        Teacher("t6", "Kurniawan, S.Pd.", "198307082008011006", "kurniawan@sd.belajar.id", "081234567806", listOf("IPAS"), 20),
        Teacher("t7", "Haryono, S.Pd.", "197509142000031007", "haryono@sd.belajar.id", "081234567807", listOf("PJOK"), 24),
        Teacher("t8", "Nadia Utami, S.Pd.", "199102182015032008", "nadia@sd.belajar.id", "081234567808", listOf("Bahasa Inggris"), 18),
        Teacher("t9", "Hendra Wijaya, S.Pd.", "198712032010011009", "hendra@sd.belajar.id", "081234567809", listOf("Muatan Lokal"), 18),
        Teacher("t10", "Eko Prasetyo, S.Pd.", "199208222016022010", "eko@sd.belajar.id", "081234567810", listOf("Matematika"), 24),
        Teacher("t11", "Nurul Hidayah, S.Pd.", "197604101999031011", "nurul@sd.belajar.id", "081234567811", listOf("Bahasa Indonesia"), 22),
        Teacher("t12", "Tri Wahyuni, S.Pd.", "198406162007012012", "tri@sd.belajar.id", "081234567812", listOf("IPAS"), 20)
    )

    val subjectList = listOf(
        Subject("s1", "MTK", "Matematika", 5, "Matematika", isPriority = true, defaultTeacher = "Bpk. Bambang Sutrisno, M.Pd."),
        Subject("s2", "BIND", "Bahasa Indonesia", 6, "Bahasa Indonesia", isPriority = true, defaultTeacher = "Ibu Siti Aminah, S.Pd."),
        Subject("s3", "IPAS", "Ilmu Pengetahuan Alam & Sosial", 5, "IPA", isPriority = true, defaultTeacher = "Bpk. Kurniawan, S.Pd."),
        Subject("s4", "PAI", "Pendidikan Agama Islam", 3, "Agama", isPriority = false, defaultTeacher = "Bpk. Ahmad Fauzi, S.Ag. (Guru Honorer)"),
        Subject("s5", "PPKN", "Pendidikan Pancasila", 4, "PPKn", isPriority = false, defaultTeacher = "Ibu Dewi Lestari, S.Pd."),
        Subject("s6", "PJOK", "Pendidikan Jasmani & Olahraga", 3, "PJOK", isPriority = true, defaultTeacher = "Bpk. Haryono, S.Pd. (Slot Pagi Anti Bentrok)"),
        Subject("s7", "SBDP", "Seni Budaya & Prakarya", 3, "Seni", isPriority = false, defaultTeacher = "Ibu Ratna Dewi, S.Pd."),
        Subject("s8", "BING", "Bahasa Inggris", 2, "Bahasa Inggris", isPriority = false, defaultTeacher = "Ibu Nadia Utami, S.Pd."),
        Subject("s9", "MLK", "Muatan Lokal / Bhs Daerah", 2, "Mulok", isPriority = false, defaultTeacher = "Bpk. Hendra Wijaya, S.Pd.")
    )

    val routineActivities = listOf(
        RoutineActivity("r1", "Upacara Bendera", RoutineType.UPACARA, DayOfWeek.SENIN, "07:00", "07:45"),
        RoutineActivity("r2", "Pembiasaan Pagi", RoutineType.PEMBIASAAN, DayOfWeek.SELASA, "07:00", "07:15"),
        RoutineActivity("r3", "Pembiasaan Pagi", RoutineType.PEMBIASAAN, DayOfWeek.RABU, "07:00", "07:15"),
        RoutineActivity("r4", "Pembiasaan Pagi", RoutineType.PEMBIASAAN, DayOfWeek.KAMIS, "07:00", "07:15"),
        RoutineActivity("r5", "Senam Bersama", RoutineType.PEMBIASAAN, DayOfWeek.JUMAT, "07:00", "07:30"),
        RoutineActivity("r6", "Literasi Pagi", RoutineType.PEMBIASAAN, DayOfWeek.SABTU, "07:00", "07:15"),
        RoutineActivity("r7", "Istirahat Pagi", RoutineType.ISTIRAHAT, DayOfWeek.SENIN, "09:15", "09:30"),
        RoutineActivity("r8", "Istirahat Siang", RoutineType.ISTIRAHAT, DayOfWeek.SENIN, "11:45", "12:30")
    )

    val timeSlotsTemplate = listOf(
        TimeSlot("ts1", 1, "07:00", "07:45"),
        TimeSlot("ts2", 2, "07:45", "08:30"),
        TimeSlot("ts3", 3, "08:30", "09:15"),
        TimeSlot("ts_break1", 0, "09:15", "09:30", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
        TimeSlot("ts4", 4, "09:30", "10:15"),
        TimeSlot("ts5", 5, "10:15", "11:00"),
        TimeSlot("ts6", 6, "11:00", "11:45"),
        TimeSlot("ts_break2", 0, "11:45", "12:30", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
        TimeSlot("ts7", 7, "12:30", "13:15")
    )

    fun getTimeSlotsForDay(day: DayOfWeek): List<TimeSlot> {
        return when (day) {
            DayOfWeek.SENIN -> listOf(
                TimeSlot("ts_upacara", 1, "07:00", "07:45", isRoutine = true, routineType = RoutineType.UPACARA),
                TimeSlot("ts2_SENIN", 2, "07:45", "08:30"),
                TimeSlot("ts3_SENIN", 3, "08:30", "09:15"),
                TimeSlot("ts_break1_SENIN", 0, "09:15", "09:30", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
                TimeSlot("ts4_SENIN", 4, "09:30", "10:15"),
                TimeSlot("ts5_SENIN", 5, "10:15", "11:00"),
                TimeSlot("ts6_SENIN", 6, "11:00", "11:45"),
                TimeSlot("ts_break2_SENIN", 0, "11:45", "12:30", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
                TimeSlot("ts7_SENIN", 7, "12:30", "13:15")
            )
            DayOfWeek.JUMAT -> listOf(
                TimeSlot("ts_senam_JUMAT", 0, "07:00", "07:35", isRoutine = true, routineType = RoutineType.PEMBIASAAN),
                TimeSlot("ts1_JUMAT", 1, "07:35", "08:15"),
                TimeSlot("ts2_JUMAT", 2, "08:15", "08:55"),
                TimeSlot("ts3_JUMAT", 3, "08:55", "09:35"),
                TimeSlot("ts_break_JUMAT", 0, "09:35", "09:55", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
                TimeSlot("ts4_JUMAT", 4, "09:55", "10:35"),
                TimeSlot("ts5_JUMAT", 5, "10:35", "11:15")
            )
            else -> listOf(
                TimeSlot("ts_pembiasaan_${day.name}", 0, "07:00", "07:15", isRoutine = true, routineType = RoutineType.PEMBIASAAN),
                TimeSlot("ts1_${day.name}", 1, "07:15", "07:55"),
                TimeSlot("ts2_${day.name}", 2, "07:55", "08:35"),
                TimeSlot("ts3_${day.name}", 3, "08:35", "09:15"),
                TimeSlot("ts_break1_${day.name}", 0, "09:15", "09:30", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
                TimeSlot("ts4_${day.name}", 4, "09:30", "10:10"),
                TimeSlot("ts5_${day.name}", 5, "10:10", "10:50"),
                TimeSlot("ts_break2_${day.name}", 0, "10:50", "11:20", isRoutine = true, routineType = RoutineType.ISTIRAHAT),
                TimeSlot("ts6_${day.name}", 6, "11:20", "12:00"),
                TimeSlot("ts7_${day.name}", 7, "12:00", "12:40")
            )
        }
    }

    fun createMockSchedule(schoolId: String = "sch_sdn01"): Schedule {
        val days = if (SchoolConfig.daysCount == 6) {
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

        val slots = mutableListOf<ScheduleSlot>()
        var slotCounter = 1

        for (cls in classList) {
            val classOffset = classList.indexOf(cls)

            for (day in days) {
                val dayTemplate = getTimeSlotsForDay(day)
                for (ts in dayTemplate) {
                    val slotId = "slot_${cls.name}_${day.name}_${ts.id}_$slotCounter"
                    slotCounter++

                    // 1. Cek Apakah Slot Rutin
                    if (ts.isRoutine) {
                        val routineAct = when (ts.routineType) {
                            RoutineType.UPACARA -> {
                                if (!SchoolConfig.isUpacaraActive && day == DayOfWeek.SENIN) null
                                else RoutineActivity("rout_upacara", "Upacara Bendera", RoutineType.UPACARA, DayOfWeek.SENIN, "07:00", "07:45")
                            }
                            RoutineType.ISTIRAHAT -> {
                                val isBreak1 = ts.id.contains("break1") || ts.startTime == "09:15" || ts.startTime == "09:35"
                                val breakMinutes = if (isBreak1) SchoolConfig.break1DurationMinutes else SchoolConfig.break2DurationMinutes
                                val breakTitle = if (isBreak1) "Istirahat 1" else "Istirahat 2"
                                val timeParts = ts.startTime.split(":")
                                val startH = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
                                val startM = timeParts.getOrNull(1)?.toIntOrNull() ?: 15
                                val endTotalMin = startH * 60 + startM + breakMinutes
                                val dynamicEndTime = String.format("%02d:%02d", endTotalMin / 60, endTotalMin % 60)
                                RoutineActivity("rout_${ts.id}", breakTitle, RoutineType.ISTIRAHAT, day, ts.startTime, dynamicEndTime)
                            }
                            RoutineType.PEMBIASAAN -> {
                                if (day == DayOfWeek.JUMAT) {
                                    if (SchoolConfig.isSenamActive) {
                                        RoutineActivity("rout_${ts.id}", "Senam Kebugaran Jasmani", RoutineType.PEMBIASAAN, day, ts.startTime, ts.endTime)
                                    } else null
                                } else {
                                    if (SchoolConfig.isPembiasaanActive) {
                                        RoutineActivity("rout_${ts.id}", "Pembiasaan Pagi", RoutineType.PEMBIASAAN, day, ts.startTime, ts.endTime)
                                    } else null
                                }
                            }
                            else -> null
                        }

                        if (routineAct != null) {
                            val finalTs = if (ts.routineType == RoutineType.ISTIRAHAT) {
                                ts.copy(endTime = routineAct.endTime)
                            } else ts

                            slots.add(
                                ScheduleSlot(
                                    id = slotId,
                                    classId = cls.id,
                                    className = cls.name,
                                    day = day,
                                    timeSlot = finalTs,
                                    routineActivity = routineAct,
                                    room = if (ts.routineType == RoutineType.UPACARA || (ts.routineType == RoutineType.PEMBIASAAN && day == DayOfWeek.JUMAT)) "Lapangan Utama" else "Ruang Kelas ${cls.name}"
                                )
                            )
                        }
                        continue
                    }

                    // 2. Mapel Reguler (Distribusi tanpa bentrok)
                    val subjectIndex = (classOffset + day.index + ts.jpNumber) % subjectList.size
                    val assignedSubject = subjectList[subjectIndex]
                    val teacherIndex = (classOffset + ts.jpNumber) % teacherList.size
                    val assignedTeacher = teacherList[teacherIndex]

                    slots.add(
                        ScheduleSlot(
                            id = slotId,
                            classId = cls.id,
                            className = cls.name,
                            day = day,
                            timeSlot = ts,
                            subject = assignedSubject,
                            teacher = assignedTeacher,
                            room = "Ruang Kelas ${cls.name}"
                        )
                    )
                }
            }
        }

        return Schedule(
            id = "sched_2026_final",
            schoolId = schoolId,
            academicYear = "2026/2027",
            semester = 1,
            slots = slots
        )
    }

    fun simulateGenerateFlow(schoolId: String): Flow<GenerateProgress> = flow {
        emit(GenerateProgress(0.10f, "Memuat data guru dan kelas..."))
        delay(600)
        emit(GenerateProgress(0.30f, "Mengecek constraint bentrok guru..."))
        delay(700)
        emit(GenerateProgress(0.50f, "Mengecek constraint bentrok kelas..."))
        delay(700)
        emit(GenerateProgress(0.70f, "Menghindari slot kegiatan rutin..."))
        delay(700)
        emit(GenerateProgress(0.90f, "Menyusun jadwal final..."))
        delay(700)
        val finalSchedule = createMockSchedule(schoolId)
        emit(GenerateProgress(1.00f, "Jadwal berhasil dibuat!", result = finalSchedule))
    }
}
