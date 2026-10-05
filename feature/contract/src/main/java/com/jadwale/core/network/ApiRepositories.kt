package com.jadwale.core.network

import com.jadwale.core.mock.MockDataProvider
import com.jadwale.core.model.*
import com.jadwale.core.repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiTeacherRepository(
    private val api: JadwaleApiService = ApiClient.api
) : TeacherRepository {
    companion object {
        val sharedLocalTeachers = MockDataProvider.teacherList.toMutableList()
    }

    override suspend fun getTeachers(schoolId: String): Result<List<Teacher>> = runCatching {
        try {
            val resp = api.getGurus()
            if (resp.isSuccessful) {
                val list = resp.body() ?: emptyList()
                if (list.isNotEmpty()) {
                    val remoteTeachers = list.map { dto ->
                        val existing = sharedLocalTeachers.firstOrNull { it.id == (dto.id ?: 0).toString() || it.name.equals(dto.nama, ignoreCase = true) }
                        val daysFromDto = dto.guruAvailabilities?.mapNotNull { av ->
                            DayOfWeek.values().firstOrNull { it.index == av.hari }
                        }
                        val finalDays = if (!daysFromDto.isNullOrEmpty()) daysFromDto else existing?.availableDays ?: listOf(DayOfWeek.SENIN, DayOfWeek.SELASA, DayOfWeek.RABU, DayOfWeek.KAMIS, DayOfWeek.JUMAT)
                        Teacher(
                            id = (dto.id ?: 0).toString(),
                            name = dto.nama,
                            nip = dto.nip ?: existing?.nip ?: "-",
                            email = dto.users?.firstOrNull()?.email ?: existing?.email ?: "",
                            phone = existing?.phone ?: "-",
                            subjects = existing?.subjects ?: emptyList(),
                            totalJp = existing?.totalJp ?: 24,
                            teacherType = existing?.teacherType ?: TeacherType.KEDUANYA,
                            teacherStatus = existing?.teacherStatus ?: TeacherStatus.PNS,
                            availabilityNote = existing?.availabilityNote ?: "Senin - Jumat Bersedia Penuh",
                            availableDays = finalDays
                        )
                    }
                    remoteTeachers.forEach { remote ->
                        val idx = sharedLocalTeachers.indexOfFirst { it.id == remote.id || it.name.equals(remote.name, ignoreCase = true) }
                        if (idx >= 0) {
                            sharedLocalTeachers[idx] = remote
                        } else {
                            sharedLocalTeachers.add(remote)
                        }
                    }
                }
            }
        } catch (_: Exception) { }
        sharedLocalTeachers.toList()
    }

    override suspend fun addTeacher(schoolId: String, teacher: Teacher): Result<Teacher> = runCatching {
        val newId = if (teacher.id.isBlank() || teacher.id.startsWith("t") || teacher.id.startsWith("teacher_")) {
            val nextNum = (sharedLocalTeachers.mapNotNull { it.id.filter { ch -> ch.isDigit() }.toIntOrNull() }.maxOrNull() ?: 0) + 1
            "t$nextNum"
        } else teacher.id
        val newTeacher = teacher.copy(id = newId)
        sharedLocalTeachers.add(newTeacher)

        try {
            val resp = api.createGuru(CreateGuruRequest(nama = teacher.name, nip = teacher.nip.takeIf { it != "-" }))
            if (resp.isSuccessful) {
                resp.body()?.id?.let { remoteId ->
                    val idx = sharedLocalTeachers.indexOfFirst { it.id == newId }
                    if (idx >= 0) {
                        sharedLocalTeachers[idx] = newTeacher.copy(id = remoteId.toString())
                    }
                    try {
                        api.setGuruAvailability(
                            remoteId,
                            SetAvailabilityRequest(newTeacher.availableDays.map { GuruAvailabilityItem(it.index) })
                        )
                    } catch (_: Exception) { }
                }
            }
        } catch (_: Exception) { }
        newTeacher
    }

    override suspend fun updateTeacher(teacher: Teacher): Result<Teacher> = runCatching {
        val idx = sharedLocalTeachers.indexOfFirst { it.id == teacher.id || it.name.equals(teacher.name, ignoreCase = true) }
        if (idx >= 0) {
            sharedLocalTeachers[idx] = teacher
        } else {
            sharedLocalTeachers.add(teacher)
        }
        val intId = teacher.id.toIntOrNull()
        if (intId != null) {
            try {
                api.updateGuru(intId, CreateGuruRequest(nama = teacher.name, nip = teacher.nip.takeIf { it != "-" }))
                api.setGuruAvailability(
                    intId,
                    SetAvailabilityRequest(teacher.availableDays.map { GuruAvailabilityItem(it.index) })
                )
            } catch (_: Exception) { }
        }
        teacher
    }

    override suspend fun deleteTeacher(teacherId: String): Result<Unit> = runCatching {
        sharedLocalTeachers.removeAll { it.id == teacherId || it.name.equals(teacherId, ignoreCase = true) }
        val intId = teacherId.toIntOrNull()
        if (intId != null) {
            try {
                api.deleteGuru(intId)
            } catch (_: Exception) { }
        }
        Unit
    }
}

class ApiClassRepository(
    private val api: JadwaleApiService = ApiClient.api
) : ClassRepository {
    companion object {
        private var lastIsParallel: Boolean? = null
        val sharedLocalClasses = mutableListOf<ClassRoom>()

        fun ensureClasses() {
            if (lastIsParallel != SchoolConfig.isParallel || sharedLocalClasses.isEmpty()) {
                lastIsParallel = SchoolConfig.isParallel
                sharedLocalClasses.clear()
                sharedLocalClasses.addAll(MockDataProvider.classList)
            }
        }
    }

    override suspend fun getClasses(schoolId: String): Result<List<ClassRoom>> = runCatching {
        ensureClasses()
        try {
            val resp = api.getClasses()
            if (resp.isSuccessful) {
                val list = resp.body() ?: emptyList()
                if (list.isNotEmpty()) {
                    val distinctTingkatanIds = list.mapNotNull { it.idTingkatan }.distinct().sorted()
                    val tingkatanIndexMap = distinctTingkatanIds.mapIndexed { idx, id -> id to (idx + 1) }.toMap()

                    val withGrades = list.mapIndexed { index, dto ->
                        val g = dto.namaKelas.filter { it.isDigit() }.toIntOrNull()
                            ?: dto.tingkatan?.nama?.filter { it.isDigit() }?.toIntOrNull()
                            ?: dto.kodeLengkap?.filter { it.isDigit() }?.toIntOrNull()
                            ?: (dto.idTingkatan?.let { tingkatanIndexMap[it] })
                            ?: ((index / 2) + 1).coerceIn(1, 6)
                        dto to g
                    }

                    val remoteClasses = withGrades.groupBy({ it.second }, { it.first }).flatMap { (gradeNum, dtos) ->
                        if (!SchoolConfig.isParallel) {
                            val dto = dtos.first()
                            listOf(
                                ClassRoom(
                                    id = (dto.id ?: gradeNum).toString(),
                                    name = "$gradeNum",
                                    grade = gradeNum,
                                    studentCount = 28,
                                    homeroomTeacher = dto.waliKelasList?.firstOrNull()?.guru?.nama ?: "-",
                                    weeklyJp = 30,
                                    curriculumPhase = if (gradeNum <= 2) "Fase A" else if (gradeNum <= 4) "Fase B" else "Fase C"
                                )
                            )
                        } else {
                            val listForGrade = if (dtos.size == 1) {
                                listOf(dtos[0] to "A", dtos[0] to "B")
                            } else {
                                dtos.take(2).mapIndexed { idx, dto ->
                                    val letter = when {
                                        dto.namaKelas.contains("B", ignoreCase = true) -> "B"
                                        dto.namaKelas.contains("A", ignoreCase = true) -> "A"
                                        idx % 2 == 1 -> "B"
                                        else -> "A"
                                    }
                                    dto to letter
                                }
                            }
                            listForGrade.map { (dto, letter) ->
                                val normalizedName = if (dto.namaKelas.matches(Regex("""(?i)^\d+[A-Z]$"""))) {
                                    dto.namaKelas.uppercase()
                                } else if (dto.namaKelas.matches(Regex("""(?i)^kelas\s*\d+[A-Z]$"""))) {
                                    dto.namaKelas.replace(Regex("""(?i)kelas\s*"""), "").uppercase()
                                } else {
                                    "${gradeNum}$letter"
                                }

                                ClassRoom(
                                    id = (dto.id ?: 0).toString(),
                                    name = normalizedName,
                                    grade = gradeNum,
                                    studentCount = 28,
                                    homeroomTeacher = dto.waliKelasList?.firstOrNull()?.guru?.nama ?: "-",
                                    weeklyJp = 30,
                                    curriculumPhase = if (gradeNum <= 2) "Fase A" else if (gradeNum <= 4) "Fase B" else "Fase C"
                                )
                            }
                        }
                    }
                    if (remoteClasses.isNotEmpty()) {
                        sharedLocalClasses.clear()
                        sharedLocalClasses.addAll(remoteClasses)
                    }
                }
            }
        } catch (_: Exception) { }
        sharedLocalClasses.sortedWith(compareBy({ it.grade }, { it.name }))
    }

    override suspend fun addClass(schoolId: String, classRoom: ClassRoom): Result<ClassRoom> = runCatching {
        ensureClasses()
        val newId = if (classRoom.id.isBlank() || classRoom.id.startsWith("c") || classRoom.id.startsWith("class_")) {
            val nextNum = (sharedLocalClasses.mapNotNull { it.id.filter { ch -> ch.isDigit() }.toIntOrNull() }.maxOrNull() ?: 0) + 1
            "c$nextNum"
        } else classRoom.id
        val newClass = classRoom.copy(id = newId)
        sharedLocalClasses.add(newClass)

        try {
            val resp = api.createClass(CreateKelasRequest(namaKelas = classRoom.name, idTingkatan = classRoom.grade, kodeLengkap = classRoom.name))
            if (resp.isSuccessful) {
                resp.body()?.id?.let { remoteId ->
                    val idx = sharedLocalClasses.indexOfFirst { it.id == newId }
                    if (idx >= 0) {
                        sharedLocalClasses[idx] = newClass.copy(id = remoteId.toString())
                    }
                }
            }
        } catch (_: Exception) { }
        newClass
    }

    override suspend fun updateClass(classRoom: ClassRoom): Result<ClassRoom> = runCatching {
        ensureClasses()
        val idx = sharedLocalClasses.indexOfFirst { it.id == classRoom.id || it.name.equals(classRoom.name, ignoreCase = true) }
        if (idx >= 0) {
            sharedLocalClasses[idx] = classRoom
        } else {
            sharedLocalClasses.add(classRoom)
        }
        val intId = classRoom.id.toIntOrNull()
        if (intId != null) {
            try {
                api.updateClass(intId, CreateKelasRequest(namaKelas = classRoom.name, idTingkatan = classRoom.grade, kodeLengkap = classRoom.name))
            } catch (_: Exception) { }
        }
        classRoom
    }

    override suspend fun deleteClass(classId: String): Result<Unit> = runCatching {
        ensureClasses()
        sharedLocalClasses.removeAll { it.id == classId || it.name.equals(classId, ignoreCase = true) }
        val intId = classId.toIntOrNull()
        if (intId != null) {
            try {
                api.deleteClass(intId)
            } catch (_: Exception) { }
        }
        Unit
    }
}

class ApiSubjectRepository(
    private val api: JadwaleApiService = ApiClient.api
) : SubjectRepository {

    override suspend fun getSubjects(schoolId: String): Result<List<Subject>> = runCatching {
        val resp = api.getSubjects()
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal memuat mata pelajaran: $err")
        }
        val list = resp.body() ?: emptyList()
        list.map { dto ->
            Subject(
                id = (dto.id ?: 0).toString(),
                code = dto.nama.take(4).uppercase(),
                name = dto.nama,
                jpPerWeek = dto.mapelTingkatans?.firstOrNull()?.jpPerMinggu ?: 2,
                colorCategory = dto.nama,
                isPriority = dto.prioritas,
                defaultTeacher = "-"
            )
        }
    }

    override suspend fun addSubject(schoolId: String, subject: Subject): Result<Subject> = runCatching {
        val resp = api.createSubject(
            CreateMapelRequest(
                nama = subject.name,
                prioritas = subject.isPriority,
                color = "#1D68E4"
            )
        )
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal menambah mata pelajaran: $err")
        }
        val dto = resp.body() ?: throw Exception("Respons server kosong")
        subject.copy(id = (dto.id ?: 0).toString())
    }

    override suspend fun updateSubject(subject: Subject): Result<Subject> = runCatching {
        val id = subject.id.toIntOrNull() ?: throw Exception("ID mapel tidak valid: ${subject.id}")
        val resp = api.updateSubject(
            id,
            CreateMapelRequest(
                nama = subject.name,
                prioritas = subject.isPriority,
                color = "#1D68E4"
            )
        )
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal memperbarui mapel: $err")
        }
        subject
    }

    override suspend fun deleteSubject(subjectId: String): Result<Unit> = runCatching {
        val id = subjectId.toIntOrNull() ?: throw Exception("ID mapel tidak valid: $subjectId")
        val resp = api.deleteSubject(id)
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal menghapus mapel: $err")
        }
        Unit
    }
}

class ApiScheduleRepository(
    private val api: JadwaleApiService = ApiClient.api
) : ScheduleRepository {

    override suspend fun generateSchedule(schoolId: String): Flow<GenerateProgress> = flow {
        emit(GenerateProgress(0.15f, "Menghubungi server Jadwale AI..."))
        delay(300)
        emit(GenerateProgress(0.35f, "Mengirim permintaan penjadwalan CSP..."))
        val resp = api.generateJadwal()
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal generate jadwal di server: $err")
        }
        emit(GenerateProgress(0.65f, "Algoritma CSP backend sedang menyusun slot tanpa bentrok..."))
        delay(1200)
        emit(GenerateProgress(0.85f, "Mengunduh hasil jadwal terbaru dari database..."))
        val schedResult = getSchedule(schoolId)
        val schedule = schedResult.getOrThrow()
        emit(GenerateProgress(1.0f, "Jadwal berhasil digenerate dari server!", result = schedule))
    }

    override suspend fun getSchedule(schoolId: String): Result<Schedule> = runCatching {
        try {
            val resp = api.getJadwal()
            if (resp.isSuccessful && resp.body() != null && resp.body()?.jadwal?.isNotEmpty() == true) {
                val body = resp.body()!!
                
                // Sync config ke SchoolConfig singleton
                body.config?.let { cfg ->
                    if (!SchoolConfig.isUserExplicitlySetParallel) {
                        SchoolConfig.isParallel = cfg.isParallel ?: false
                    }
                    SchoolConfig.daysCount = cfg.schoolDays ?: 5
                }

                val baseSlots = body.jadwal.map { item ->
                    val dayEnum = when (item.hari) {
                        1 -> DayOfWeek.SENIN
                        2 -> DayOfWeek.SELASA
                        3 -> DayOfWeek.RABU
                        4 -> DayOfWeek.KAMIS
                        5 -> DayOfWeek.JUMAT
                        else -> DayOfWeek.SABTU
                    }
                    val daySlots = MockDataProvider.getTimeSlotsForDay(dayEnum)
                    val matchedTs = daySlots.firstOrNull { !it.isRoutine && it.jpNumber == item.jamKe }
                    val timeSlot = matchedTs ?: TimeSlot(
                        id = "ts_${item.jamKe}_${dayEnum.name}",
                        jpNumber = item.jamKe,
                        startTime = String.format("%02d:00", 7 + (item.jamKe - 1) * 45 / 60),
                        endTime = String.format("%02d:%02d", 7 + item.jamKe * 45 / 60, (item.jamKe * 45) % 60)
                    )
                    val sub = item.mapel?.let { m ->
                        Subject(
                            id = m.id.toString(),
                            code = m.nama.take(4).uppercase(),
                            name = m.nama,
                            jpPerWeek = 2,
                            colorCategory = m.nama,
                            isPriority = m.prioritas
                        )
                    }
                    val tch = item.guru?.let { g ->
                        Teacher(
                            id = g.id.toString(),
                            name = g.nama,
                            nip = g.nip ?: "-",
                            subjects = listOfNotNull(item.mapel?.nama)
                        )
                    }
                    ScheduleSlot(
                        id = item.id.toString(),
                        classId = item.idKelas.toString(),
                        className = item.kelas?.namaKelas ?: "Kelas ${item.idKelas}",
                        day = dayEnum,
                        timeSlot = timeSlot,
                        subject = sub,
                        teacher = tch,
                        room = "Ruang ${item.kelas?.namaKelas ?: ""}"
                    )
                }

                // Generate / Inject Routine Activities and Break Slots
                val distinctClasses = if (baseSlots.isNotEmpty()) {
                    baseSlots.map { it.classId to it.className }.distinctBy { it.first }
                } else {
                    MockDataProvider.classList.map { it.id to it.name }
                }

                val activeDays = if (SchoolConfig.daysCount == 6) {
                    listOf(DayOfWeek.SENIN, DayOfWeek.SELASA, DayOfWeek.RABU, DayOfWeek.KAMIS, DayOfWeek.JUMAT, DayOfWeek.SABTU)
                } else {
                    listOf(DayOfWeek.SENIN, DayOfWeek.SELASA, DayOfWeek.RABU, DayOfWeek.KAMIS, DayOfWeek.JUMAT)
                }

                val routineSlots = mutableListOf<ScheduleSlot>()
                for ((cId, cName) in distinctClasses) {
                    for (day in activeDays) {
                        val dayRoutines = MockDataProvider.getTimeSlotsForDay(day).filter { it.isRoutine }
                        for (ts in dayRoutines) {
                            val routineAct = when (ts.routineType) {
                                RoutineType.UPACARA -> {
                                    if (!SchoolConfig.isUpacaraActive && day == DayOfWeek.SENIN) null
                                    else RoutineActivity("r_upacara_${cId}", "Upacara Bendera", RoutineType.UPACARA, DayOfWeek.SENIN, "07:00", "07:45")
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
                                    RoutineActivity("r_${ts.id}_${cId}", breakTitle, RoutineType.ISTIRAHAT, day, ts.startTime, dynamicEndTime)
                                }
                                RoutineType.PEMBIASAAN -> {
                                    if (day == DayOfWeek.JUMAT) {
                                        if (SchoolConfig.isSenamActive) {
                                            RoutineActivity("r_${ts.id}_${cId}", "Senam Kebugaran Jasmani", RoutineType.PEMBIASAAN, day, ts.startTime, ts.endTime)
                                        } else null
                                    } else {
                                        if (SchoolConfig.isPembiasaanActive) {
                                            RoutineActivity("r_${ts.id}_${cId}", "Pembiasaan Pagi", RoutineType.PEMBIASAAN, day, ts.startTime, ts.endTime)
                                        } else null
                                    }
                                }
                                else -> null
                            }

                            if (routineAct != null) {
                                val finalTs = if (ts.routineType == RoutineType.ISTIRAHAT) {
                                    ts.copy(endTime = routineAct.endTime)
                                } else ts

                                routineSlots.add(
                                    ScheduleSlot(
                                        id = "routine_${ts.id}_${cId}_${day.name}",
                                        classId = cId,
                                        className = cName,
                                        day = day,
                                        timeSlot = finalTs,
                                        routineActivity = routineAct,
                                        room = if (ts.routineType == RoutineType.UPACARA || (ts.routineType == RoutineType.PEMBIASAAN && day == DayOfWeek.JUMAT)) "Lapangan Utama" else "Ruang Kelas $cName"
                                    )
                                )
                            }
                        }
                    }
                }

                // Inject custom routines from ApiRoutineRepository
                val customRoutines = ApiRoutineRepository.sharedLocalRoutines.filter { 
                    it.type != RoutineType.ISTIRAHAT && !it.name.contains("Upacara", ignoreCase = true) && !it.name.contains("Senam", ignoreCase = true) && !it.name.contains("Pembiasaan Pagi", ignoreCase = true)
                }
                for (cr in customRoutines) {
                    for ((cId, cName) in distinctClasses) {
                        routineSlots.add(
                            ScheduleSlot(
                                id = "custom_${cr.id}_${cId}_${cr.day.name}",
                                classId = cId,
                                className = cName,
                                day = cr.day,
                                timeSlot = TimeSlot("ts_custom_${cr.id}", 0, cr.startTime, cr.endTime, isRoutine = true, routineType = cr.type),
                                routineActivity = cr,
                                room = "Ruang Kelas $cName"
                            )
                        )
                    }
                }

                // Hindari mapel bentrok di Senin JP 1 jika Upacara aktif
                val filteredBaseSlots = if (SchoolConfig.isUpacaraActive) {
                    baseSlots.filter { !(it.day == DayOfWeek.SENIN && it.timeSlot.jpNumber == 1) }
                } else {
                    baseSlots
                }

                val allSlots = (filteredBaseSlots + routineSlots).sortedWith(
                    compareBy({ it.day.index }, { it.timeSlot.startTime }, { it.className })
                )

                Schedule(
                    id = body.currentPeriodeId?.toString() ?: "1",
                    schoolId = schoolId,
                    academicYear = SchoolConfig.academicYear,
                    semester = 1,
                    slots = allSlots
                )
            } else {
                MockDataProvider.createMockSchedule(schoolId)
            }
        } catch (_: Exception) {
            MockDataProvider.createMockSchedule(schoolId)
        }
    }

    override suspend fun getScheduleByClass(schoolId: String, classId: String): Result<Schedule> = runCatching {
        val cid = classId.toIntOrNull()
        val resp = api.getJadwal(idKelas = cid)
        if (!resp.isSuccessful) {
            val err = resp.errorBody()?.string() ?: resp.message()
            throw Exception("Gagal memuat jadwal kelas: $err")
        }
        val full = getSchedule(schoolId).getOrThrow()
        full.copy(slots = full.slots.filter { it.classId == classId })
    }

    override suspend fun updateSlot(slotId: String, slot: ScheduleSlot): Result<ScheduleSlot> {
        // Backend live saat ini mengelola slot via algoritma / generate
        return Result.success(slot)
    }

    override suspend fun saveSchedule(schedule: Schedule): Result<Schedule> {
        return Result.success(schedule)
    }
}

class ApiAssignmentRepository : AssignmentRepository {
    private val localAssignments = mutableListOf<Assignment>()

    override suspend fun getAssignments(schoolId: String): Result<List<Assignment>> = runCatching {
        localAssignments.toList()
    }

    override suspend fun addAssignment(schoolId: String, assignment: Assignment): Result<Assignment> = runCatching {
        localAssignments.add(assignment)
        assignment
    }

    override suspend fun updateAssignment(assignment: Assignment): Result<Assignment> = runCatching {
        val idx = localAssignments.indexOfFirst { it.id == assignment.id }
        if (idx >= 0) localAssignments[idx] = assignment
        assignment
    }

    override suspend fun deleteAssignment(assignmentId: String): Result<Unit> = runCatching {
        localAssignments.removeAll { it.id == assignmentId }
        Unit
    }
}

class ApiRoutineRepository(
    private val api: JadwaleApiService = ApiClient.api
) : RoutineRepository {
    companion object {
        val sharedLocalRoutines = com.jadwale.core.mock.MockDataProvider.routineActivities.toMutableList()
    }

    override suspend fun getRoutines(schoolId: String): Result<List<RoutineActivity>> = runCatching {
        try {
            val resp = api.getJadwal()
            if (resp.isSuccessful) {
                val dtoRoutines = resp.body()?.routines ?: emptyList()
                if (dtoRoutines.isNotEmpty()) {
                    dtoRoutines.forEach { r ->
                        val day = when (r.dayOfWeek) {
                            1 -> DayOfWeek.SENIN
                            2 -> DayOfWeek.SELASA
                            3 -> DayOfWeek.RABU
                            4 -> DayOfWeek.KAMIS
                            5 -> DayOfWeek.JUMAT
                            else -> DayOfWeek.SENIN
                        }
                        val rId = r.id.toString()
                        if (sharedLocalRoutines.none { it.id == rId }) {
                            sharedLocalRoutines.add(
                                RoutineActivity(
                                    id = rId,
                                    name = r.name,
                                    type = if (r.name.contains("Istirahat", ignoreCase = true)) RoutineType.ISTIRAHAT
                                           else if (r.name.contains("Upacara", ignoreCase = true)) RoutineType.UPACARA
                                           else RoutineType.PEMBIASAAN,
                                    day = day,
                                    startTime = "07:00",
                                    endTime = "07:15"
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) { }
        sharedLocalRoutines.toList()
    }

    override suspend fun addRoutine(schoolId: String, routine: RoutineActivity): Result<RoutineActivity> = runCatching {
        val newRoutine = if (routine.id.isBlank()) routine.copy(id = "routine_${System.currentTimeMillis()}") else routine
        sharedLocalRoutines.add(newRoutine)
        newRoutine
    }

    override suspend fun updateRoutine(routine: RoutineActivity): Result<RoutineActivity> = runCatching {
        val idx = sharedLocalRoutines.indexOfFirst { it.id == routine.id }
        if (idx >= 0) {
            sharedLocalRoutines[idx] = routine
        } else {
            sharedLocalRoutines.add(routine)
        }
        routine
    }

    override suspend fun deleteRoutine(routineId: String): Result<Unit> = runCatching {
        sharedLocalRoutines.removeAll { it.id == routineId }
        Unit
    }
}
