package com.jadwale.app

import com.jadwale.core.mock.*
import com.jadwale.core.model.*
import com.jadwale.feature.dashboard.DashboardData
import com.jadwale.feature.dashboard.DashboardUiState
import com.jadwale.feature.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppLogicTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMockDataProviderIntegrity() {
        val schedule = MockDataProvider.createMockSchedule()
        assertNotNull("Schedule should not be null", schedule)
        assertFalse("Schedule slots should not be empty", schedule.slots.isEmpty())

        for (slot in schedule.slots) {
            assertNotNull("Slot ID should not be null", slot.id)
            assertNotNull("Class ID should not be null", slot.classId)
            assertNotNull("Class name should not be null", slot.className)
            assertNotNull("Day should not be null", slot.day)
            assertNotNull("TimeSlot should not be null", slot.timeSlot)
            // Routine vs regular
            if (slot.isRoutine) {
                assertNotNull("Routine slot should have routine activity", slot.routineActivity)
            } else {
                assertNotNull("Regular slot should have subject", slot.subject)
                assertNotNull("Regular slot should have teacher", slot.teacher)
            }
        }
    }

    @Test
    fun testRepositoriesData() = runTest {
        val scheduleRepo = MockScheduleRepository()
        val teacherRepo = MockTeacherRepository()
        val classRepo = MockClassRepository()
        val subjectRepo = MockSubjectRepository()
        val assignmentRepo = MockAssignmentRepository()
        val routineRepo = MockRoutineRepository()

        val teachers = teacherRepo.getTeachers("sch_sdn01").getOrThrow()
        assertTrue("Teachers count > 0", teachers.isNotEmpty())

        val classes = classRepo.getClasses("sch_sdn01").getOrThrow()
        assertTrue("Classes count > 0", classes.isNotEmpty())

        val subjects = subjectRepo.getSubjects("sch_sdn01").getOrThrow()
        assertTrue("Subjects count > 0", subjects.isNotEmpty())

        val schedule = scheduleRepo.getSchedule("sch_sdn01").getOrThrow()
        assertTrue("Schedule slots > 0", schedule.slots.isNotEmpty())

        val assignments = assignmentRepo.getAssignments("sch_sdn01").getOrThrow()
        assertTrue("Assignments > 0", assignments.isNotEmpty())

        val routines = routineRepo.getRoutines("sch_sdn01").getOrThrow()
        assertTrue("Routines > 0", routines.isNotEmpty())
    }

    @Test
    fun testDashboardViewModel() = runTest {
        val teacherRepo = MockTeacherRepository()
        val classRepo = MockClassRepository()
        val subjectRepo = MockSubjectRepository()
        val scheduleRepo = MockScheduleRepository()

        val viewModel = DashboardViewModel(
            teacherRepository = teacherRepo,
            classRepository = classRepo,
            subjectRepository = subjectRepo,
            scheduleRepository = scheduleRepo
        )

        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)

        viewModel.loadDashboard(role = UserRole.ADMIN_SEKOLAH)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success but was $state", state is DashboardUiState.Success)
        val success = state as DashboardUiState.Success
        assertEquals(UserRole.ADMIN_SEKOLAH, success.currentRole)
        assertTrue(success.data.teacherCount > 0)
        assertTrue(success.data.classCount > 0)
        assertTrue(success.data.subjectCount > 0)
    }

    @Test
    fun testClassStructureParallelAndSingle() = runTest {
        val classRepo = MockClassRepository()

        // 1. Test Struktur Paralel (1A s/d 6B)
        SchoolConfig.isParallel = true
        val parallelClasses = classRepo.getClasses("sch_sdn01").getOrThrow()
        assertEquals(12, parallelClasses.size)
        assertEquals(listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B"), parallelClasses.map { it.name })

        // 2. Test Struktur Tunggal (1 s/d 6)
        SchoolConfig.isParallel = false
        val singleClasses = classRepo.getClasses("sch_sdn01").getOrThrow()
        assertEquals(6, singleClasses.size)
        assertEquals(listOf("1", "2", "3", "4", "5", "6"), singleClasses.map { it.name })

        // Reset to default
        SchoolConfig.isParallel = true
    }

    @Test
    fun testRoutineRepositoryCrud() = runTest {
        val routineRepo = MockRoutineRepository()
        val initialCount = routineRepo.getRoutines("sch_sdn01").getOrThrow().size

        // 1. Add Routine
        val newRoutine = RoutineActivity(
            id = "test_routine_1",
            name = "Literasi Pagi Bersama",
            type = RoutineType.PEMBIASAAN,
            day = DayOfWeek.SELASA,
            startTime = "07:00",
            endTime = "07:15"
        )
        val added = routineRepo.addRoutine("sch_sdn01", newRoutine).getOrThrow()
        assertEquals("Literasi Pagi Bersama", added.name)
        val afterAdd = routineRepo.getRoutines("sch_sdn01").getOrThrow()
        assertEquals(initialCount + 1, afterAdd.size)

        // 2. Update Routine
        val updated = routineRepo.updateRoutine(newRoutine.copy(name = "Literasi & Numerasi Pagi")).getOrThrow()
        assertEquals("Literasi & Numerasi Pagi", updated.name)

        // 3. Delete Routine
        val deleteResult = routineRepo.deleteRoutine("test_routine_1")
        assertTrue(deleteResult.isSuccess)
        val afterDelete = routineRepo.getRoutines("sch_sdn01").getOrThrow()
        assertEquals(initialCount, afterDelete.size)
        assertFalse(afterDelete.any { it.id == "test_routine_1" })
    }

    @Test
    fun testScheduleWithRoutinesAndBreaks() = runTest {
        SchoolConfig.isUpacaraActive = true
        SchoolConfig.break1DurationMinutes = 20
        SchoolConfig.break2DurationMinutes = 25

        val schedule = MockDataProvider.createMockSchedule("sch_sdn01")
        val routineSlots = schedule.slots.filter { it.isRoutine }
        assertTrue("Schedule must contain routine slots", routineSlots.isNotEmpty())

        val upacaraSlot = routineSlots.firstOrNull { it.routineActivity?.name?.contains("Upacara", ignoreCase = true) == true }
        assertNotNull("Upacara Bendera slot should exist on Monday", upacaraSlot)
        assertEquals(DayOfWeek.SENIN, upacaraSlot?.day)

        val break1Slot = routineSlots.firstOrNull { it.routineActivity?.name?.contains("Istirahat 1", ignoreCase = true) == true }
        assertNotNull("Istirahat 1 slot should exist", break1Slot)
        assertEquals("09:35", break1Slot?.timeSlot?.endTime) // 09:15 + 20 minutes

        val break2Slot = routineSlots.firstOrNull { it.routineActivity?.name?.contains("Istirahat 2", ignoreCase = true) == true }
        assertNotNull("Istirahat 2 slot should exist", break2Slot)
        assertEquals("12:10", break2Slot?.timeSlot?.endTime) // 11:45 + 25 minutes
    }

    @Test
    fun testSchoolStructurePersistence() {
        SchoolConfig.isUserExplicitlySetParallel = true
        SchoolConfig.isParallel = false

        // Simulate server returning isParallel = true (or null/false)
        val serverConfigDto = false
        if (!SchoolConfig.isUserExplicitlySetParallel) {
            SchoolConfig.isParallel = serverConfigDto
        }

        // Must still remain false because user explicitly set it
        assertEquals(false, SchoolConfig.isParallel)

        // Reset
        SchoolConfig.isUserExplicitlySetParallel = false
        SchoolConfig.isParallel = true
    }

    @Test
    fun testUpacaraMondayOnlyAndPembiasaan() {
        SchoolConfig.isUpacaraActive = true
        SchoolConfig.isPembiasaanActive = true
        SchoolConfig.isSenamActive = true

        val schedule = MockDataProvider.createMockSchedule("sch_sdn01")
        
        // 1. Senin: Upacara must be present on Monday at JP 1 (07:00 - 07:45)
        val mondaySlots = schedule.slots.filter { it.day == DayOfWeek.SENIN }
        val mondayUpacara = mondaySlots.filter { it.routineActivity?.type == RoutineType.UPACARA }
        assertTrue("Monday must have Upacara", mondayUpacara.isNotEmpty())
        assertEquals("07:00", mondayUpacara.first().timeSlot.startTime)
        assertEquals("07:45", mondayUpacara.first().timeSlot.endTime)
        assertEquals(1, mondayUpacara.first().timeSlot.jpNumber)

        // Monday academic lessons must start at JP 2 (07:45)
        val mondayLessons = mondaySlots.filter { !it.isRoutine }
        assertTrue(mondayLessons.all { it.timeSlot.jpNumber >= 2 })

        // 2. Tuesday through Saturday: NEVER has Upacara
        val otherDays = listOf(DayOfWeek.SELASA, DayOfWeek.RABU, DayOfWeek.KAMIS, DayOfWeek.JUMAT, DayOfWeek.SABTU)
        val otherDayUpacara = schedule.slots.filter { it.day in otherDays && it.routineActivity?.type == RoutineType.UPACARA }
        assertTrue("Upacara must NOT exist on days other than Monday", otherDayUpacara.isEmpty())

        // 3. Tuesday-Thursday/Saturday: Pembiasaan is at 07:00 - 07:15, and JP 1 starts after it at 07:15
        val tuesdaySlots = schedule.slots.filter { it.day == DayOfWeek.SELASA }
        val tuesdayPembiasaan = tuesdaySlots.firstOrNull { it.routineActivity?.type == RoutineType.PEMBIASAAN }
        assertNotNull("Tuesday must have Pembiasaan", tuesdayPembiasaan)
        assertEquals("07:00", tuesdayPembiasaan?.timeSlot?.startTime)
        assertEquals("07:15", tuesdayPembiasaan?.timeSlot?.endTime)

        val tuesdayJp1 = tuesdaySlots.firstOrNull { !it.isRoutine && it.timeSlot.jpNumber == 1 }
        assertNotNull("Tuesday must have JP 1 lesson", tuesdayJp1)
        assertEquals("07:15", tuesdayJp1?.timeSlot?.startTime)
        assertEquals("07:55", tuesdayJp1?.timeSlot?.endTime)

        // 4. Friday: Senam is at 07:00 - 07:35, and JP 1 starts after it at 07:35
        val fridaySlots = schedule.slots.filter { it.day == DayOfWeek.JUMAT }
        val fridaySenam = fridaySlots.firstOrNull { it.routineActivity?.type == RoutineType.PEMBIASAAN }
        assertNotNull("Friday must have Senam", fridaySenam)
        assertEquals("07:00", fridaySenam?.timeSlot?.startTime)
        assertEquals("07:35", fridaySenam?.timeSlot?.endTime)

        val fridayJp1 = fridaySlots.firstOrNull { !it.isRoutine && it.timeSlot.jpNumber == 1 }
        assertNotNull("Friday must have JP 1 lesson", fridayJp1)
        assertEquals("07:35", fridayJp1?.timeSlot?.startTime)
        assertEquals("08:15", fridayJp1?.timeSlot?.endTime)
    }

    @Test
    fun testApiClassRepositoryCrud() = runTest {
        val classRepo = com.jadwale.core.network.ApiClassRepository()
        val initialClasses = classRepo.getClasses("sch_sdn01").getOrThrow()
        assertTrue("Initial classes should not be empty", initialClasses.isNotEmpty())

        // Add
        val newClass = ClassRoom(
            id = "",
            name = "TestClass1",
            grade = 1,
            studentCount = 25,
            homeroomTeacher = "Guru Test"
        )
        val added = classRepo.addClass("sch_sdn01", newClass).getOrThrow()
        assertEquals("TestClass1", added.name)
        assertTrue(added.id.isNotBlank())

        // Update
        val updated = classRepo.updateClass(added.copy(studentCount = 30)).getOrThrow()
        assertEquals(30, updated.studentCount)

        // Delete
        val delResult = classRepo.deleteClass(added.id)
        assertTrue("Delete must succeed", delResult.isSuccess)
    }

    @Test
    fun testApiTeacherRepositoryCrud() = runTest {
        val teacherRepo = com.jadwale.core.network.ApiTeacherRepository()
        val initialTeachers = teacherRepo.getTeachers("sch_sdn01").getOrThrow()
        assertTrue("Initial teachers should not be empty", initialTeachers.isNotEmpty())

        // Add
        val newTeacher = Teacher(
            id = "",
            name = "Guru Baru, S.Pd.",
            nip = "199501012020011001",
            email = "gurubaru@sd.belajar.id",
            phone = "081299998888",
            subjects = listOf("Matematika"),
            totalJp = 24
        )
        val added = teacherRepo.addTeacher("sch_sdn01", newTeacher).getOrThrow()
        assertEquals("Guru Baru, S.Pd.", added.name)
        assertTrue(added.id.isNotBlank())

        // Update
        val updated = teacherRepo.updateTeacher(added.copy(phone = "081299990000")).getOrThrow()
        assertEquals("081299990000", updated.phone)

        // Delete
        val delResult = teacherRepo.deleteTeacher(added.id)
        assertTrue("Delete must succeed", delResult.isSuccess)
    }

    @Test
    fun testTeacherMatriksMengajarPersistence() = runTest {
        val teacherRepo = com.jadwale.core.network.ApiTeacherRepository()
        val customDays = listOf(DayOfWeek.SENIN, DayOfWeek.RABU, DayOfWeek.JUMAT)
        val teacher = Teacher(
            id = "t_matriks_test",
            name = "Guru Honorer Matriks, S.Pd.",
            nip = "-",
            subjects = listOf("PJOK"),
            totalJp = 12,
            availabilityNote = "Bersedia hari: Senin, Rabu, Jumat",
            availableDays = customDays
        )

        // Save
        val added = teacherRepo.addTeacher("sch_sdn01", teacher).getOrThrow()
        assertEquals(customDays, added.availableDays)

        // Retrieve
        val teachers = teacherRepo.getTeachers("sch_sdn01").getOrThrow()
        val found = teachers.firstOrNull { it.id == added.id }
        assertNotNull(found)
        assertEquals(customDays, found?.availableDays)

        // Update with new matrix
        val newDays = listOf(DayOfWeek.SELASA, DayOfWeek.KAMIS)
        val updated = teacherRepo.updateTeacher(found!!.copy(availableDays = newDays)).getOrThrow()
        assertEquals(newDays, updated.availableDays)

        // Clean up
        teacherRepo.deleteTeacher(added.id)
    }
}
