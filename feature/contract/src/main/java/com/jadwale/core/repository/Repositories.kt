package com.jadwale.core.repository

import com.jadwale.core.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository Kontrak untuk Modul Jadwale
 * ViewModel HANYA menginjeksi interface ini (Dependency Inversion Principle)
 */

interface ScheduleRepository {
    suspend fun generateSchedule(schoolId: String): Flow<GenerateProgress>
    suspend fun getSchedule(schoolId: String): Result<Schedule>
    suspend fun getScheduleByClass(schoolId: String, classId: String): Result<Schedule>
    suspend fun updateSlot(slotId: String, slot: ScheduleSlot): Result<ScheduleSlot>
    suspend fun saveSchedule(schedule: Schedule): Result<Schedule>
}

interface TeacherRepository {
    suspend fun getTeachers(schoolId: String): Result<List<Teacher>>
    suspend fun addTeacher(schoolId: String, teacher: Teacher): Result<Teacher>
    suspend fun updateTeacher(teacher: Teacher): Result<Teacher>
    suspend fun deleteTeacher(teacherId: String): Result<Unit>
}

interface ClassRepository {
    suspend fun getClasses(schoolId: String): Result<List<ClassRoom>>
    suspend fun addClass(schoolId: String, classRoom: ClassRoom): Result<ClassRoom>
    suspend fun updateClass(classRoom: ClassRoom): Result<ClassRoom>
    suspend fun deleteClass(classId: String): Result<Unit>
}

interface SubjectRepository {
    suspend fun getSubjects(schoolId: String): Result<List<Subject>>
    suspend fun addSubject(schoolId: String, subject: Subject): Result<Subject>
    suspend fun updateSubject(subject: Subject): Result<Subject>
    suspend fun deleteSubject(subjectId: String): Result<Unit>
}

interface AssignmentRepository {
    suspend fun getAssignments(schoolId: String): Result<List<Assignment>>
    suspend fun addAssignment(schoolId: String, assignment: Assignment): Result<Assignment>
    suspend fun updateAssignment(assignment: Assignment): Result<Assignment>
    suspend fun deleteAssignment(assignmentId: String): Result<Unit>
}

interface RoutineRepository {
    suspend fun getRoutines(schoolId: String): Result<List<RoutineActivity>>
    suspend fun addRoutine(schoolId: String, routine: RoutineActivity): Result<RoutineActivity>
    suspend fun updateRoutine(routine: RoutineActivity): Result<RoutineActivity>
    suspend fun deleteRoutine(routineId: String): Result<Unit>
}
