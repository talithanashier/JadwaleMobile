package com.jadwale.core.mock

import com.jadwale.core.model.*
import com.jadwale.core.repository.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Implementasi Mock Repository untuk Pengujian Standalone & Demo Penilaian
 * Dapat di-switch langsung saat MODE_MOCK = true
 */
class MockScheduleRepository : ScheduleRepository {
    private var currentSchedule: Schedule = MockDataProvider.createMockSchedule()

    override suspend fun generateSchedule(schoolId: String): Flow<GenerateProgress> {
        return kotlinx.coroutines.flow.flow {
            MockDataProvider.simulateGenerateFlow(schoolId).collect { progress ->
                if (progress.result != null) {
                    currentSchedule = progress.result
                }
                emit(progress)
            }
        }
    }

    override suspend fun getSchedule(schoolId: String): Result<Schedule> {
        return Result.success(currentSchedule)
    }

    override suspend fun getScheduleByClass(schoolId: String, classId: String): Result<Schedule> {
        val filteredSlots = currentSchedule.slots.filter { it.classId == classId }
        return Result.success(currentSchedule.copy(slots = filteredSlots))
    }

    override suspend fun updateSlot(slotId: String, slot: ScheduleSlot): Result<ScheduleSlot> {
        val updatedSlots = currentSchedule.slots.map {
            if (it.id == slotId) slot else it
        }
        currentSchedule = currentSchedule.copy(slots = updatedSlots)
        return Result.success(slot)
    }

    override suspend fun saveSchedule(schedule: Schedule): Result<Schedule> {
        currentSchedule = schedule
        return Result.success(schedule)
    }
}

class MockTeacherRepository : TeacherRepository {
    private val teachers = MockDataProvider.teacherList.toMutableList()

    override suspend fun getTeachers(schoolId: String): Result<List<Teacher>> {
        return Result.success(teachers.toList())
    }

    override suspend fun addTeacher(schoolId: String, teacher: Teacher): Result<Teacher> {
        val newTeacher = if (teacher.id.isBlank()) teacher.copy(id = UUID.randomUUID().toString()) else teacher
        teachers.add(newTeacher)
        return Result.success(newTeacher)
    }

    override suspend fun updateTeacher(teacher: Teacher): Result<Teacher> {
        val index = teachers.indexOfFirst { it.id == teacher.id }
        if (index != -1) {
            teachers[index] = teacher
            return Result.success(teacher)
        }
        return Result.failure(Exception("Guru tidak ditemukan"))
    }

    override suspend fun deleteTeacher(teacherId: String): Result<Unit> {
        teachers.removeAll { it.id == teacherId }
        return Result.success(Unit)
    }
}

class MockClassRepository : ClassRepository {
    private var lastIsParallel: Boolean? = null
    private val classes = mutableListOf<ClassRoom>()

    private fun ensureClasses() {
        if (lastIsParallel != SchoolConfig.isParallel) {
            lastIsParallel = SchoolConfig.isParallel
            classes.clear()
            classes.addAll(MockDataProvider.classList)
        }
    }

    override suspend fun getClasses(schoolId: String): Result<List<ClassRoom>> {
        ensureClasses()
        return Result.success(classes.toList())
    }

    override suspend fun addClass(schoolId: String, classRoom: ClassRoom): Result<ClassRoom> {
        ensureClasses()
        val newClass = if (classRoom.id.isBlank()) classRoom.copy(id = UUID.randomUUID().toString()) else classRoom
        classes.add(newClass)
        return Result.success(newClass)
    }

    override suspend fun updateClass(classRoom: ClassRoom): Result<ClassRoom> {
        ensureClasses()
        val index = classes.indexOfFirst { it.id == classRoom.id }
        if (index != -1) {
            classes[index] = classRoom
            return Result.success(classRoom)
        }
        return Result.failure(Exception("Kelas tidak ditemukan"))
    }

    override suspend fun deleteClass(classId: String): Result<Unit> {
        ensureClasses()
        classes.removeAll { it.id == classId }
        return Result.success(Unit)
    }
}

class MockSubjectRepository : SubjectRepository {
    private val subjects = MockDataProvider.subjectList.toMutableList()

    override suspend fun getSubjects(schoolId: String): Result<List<Subject>> {
        return Result.success(subjects.toList())
    }

    override suspend fun addSubject(schoolId: String, subject: Subject): Result<Subject> {
        val newSubject = if (subject.id.isBlank()) subject.copy(id = UUID.randomUUID().toString()) else subject
        subjects.add(newSubject)
        return Result.success(newSubject)
    }

    override suspend fun updateSubject(subject: Subject): Result<Subject> {
        val index = subjects.indexOfFirst { it.id == subject.id }
        if (index != -1) {
            subjects[index] = subject
            return Result.success(subject)
        }
        return Result.failure(Exception("Mata pelajaran tidak ditemukan"))
    }

    override suspend fun deleteSubject(subjectId: String): Result<Unit> {
        subjects.removeAll { it.id == subjectId }
        return Result.success(Unit)
    }
}

class MockAssignmentRepository : AssignmentRepository {
    private val assignments = mutableListOf(
        Assignment("a1", "t1", "Budi Santoso, S.Pd.", "s1", "Matematika", "c1", "1A", 4),
        Assignment("a2", "t2", "Siti Rahma, S.Pd.", "s2", "IPA", "c1", "1A", 4),
        Assignment("a3", "t3", "Ahmad Hidayat, M.Pd.", "s4", "Bahasa Indonesia", "c1", "1A", 5),
        Assignment("a4", "t4", "Dewi Lestari, S.Pd.", "s5", "Bahasa Inggris", "c2", "1B", 3),
        Assignment("a5", "t5", "Rina Kusuma, S.Pd.", "s3", "IPS", "c2", "1B", 3)
    )

    override suspend fun getAssignments(schoolId: String): Result<List<Assignment>> {
        return Result.success(assignments.toList())
    }

    override suspend fun addAssignment(schoolId: String, assignment: Assignment): Result<Assignment> {
        val newAssignment = if (assignment.id.isBlank()) assignment.copy(id = UUID.randomUUID().toString()) else assignment
        assignments.add(newAssignment)
        return Result.success(newAssignment)
    }

    override suspend fun updateAssignment(assignment: Assignment): Result<Assignment> {
        val index = assignments.indexOfFirst { it.id == assignment.id }
        if (index != -1) {
            assignments[index] = assignment
            return Result.success(assignment)
        }
        return Result.failure(Exception("Penetapan mengajar tidak ditemukan"))
    }

    override suspend fun deleteAssignment(assignmentId: String): Result<Unit> {
        assignments.removeAll { it.id == assignmentId }
        return Result.success(Unit)
    }
}

class MockRoutineRepository : RoutineRepository {
    private val routines = MockDataProvider.routineActivities.toMutableList()

    override suspend fun getRoutines(schoolId: String): Result<List<RoutineActivity>> {
        return Result.success(routines.toList())
    }

    override suspend fun addRoutine(schoolId: String, routine: RoutineActivity): Result<RoutineActivity> {
        val newRoutine = if (routine.id.isBlank()) routine.copy(id = UUID.randomUUID().toString()) else routine
        routines.add(newRoutine)
        return Result.success(newRoutine)
    }

    override suspend fun updateRoutine(routine: RoutineActivity): Result<RoutineActivity> {
        val index = routines.indexOfFirst { it.id == routine.id }
        if (index != -1) {
            routines[index] = routine
            return Result.success(routine)
        }
        return Result.failure(Exception("Kegiatan rutin tidak ditemukan"))
    }

    override suspend fun deleteRoutine(routineId: String): Result<Unit> {
        routines.removeAll { it.id == routineId }
        return Result.success(Unit)
    }
}
