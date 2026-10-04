package com.jadwale.core.session

import com.jadwale.core.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PendingTeacherItem(
    val id: String,
    val name: String,
    val nip: String,
    val email: String,
    val schoolId: Int,
    val schoolName: String,
    val teacherType: String = "Guru Mapel / Wali Kelas",
    val statusGuru: String = "PPPK / Non-ASN",
    val requestedAt: String = "Baru Saja Mendaftar",
    var isApproved: Boolean = false
)

object TeacherVerificationStore {
    private val _pendingList = MutableStateFlow<List<PendingTeacherItem>>(emptyList())
    val pendingList: StateFlow<List<PendingTeacherItem>> = _pendingList.asStateFlow()

    fun addPendingTeacher(
        name: String,
        email: String,
        nip: String?,
        schoolId: Int,
        schoolName: String
    ) {
        val newItem = PendingTeacherItem(
            id = "reg_${System.currentTimeMillis()}",
            name = name,
            nip = nip ?: "-",
            email = email,
            schoolId = schoolId,
            schoolName = schoolName,
            teacherType = "Guru Pengampu Baru",
            statusGuru = if (nip.isNullOrBlank()) "Guru Honorer / P3K" else "PNS / ASN",
            requestedAt = "Baru Mendaftar via Registrasi",
            isApproved = false
        )
        _pendingList.update { current ->
            listOf(newItem) + current.filter { it.email != email }
        }
    }

    suspend fun loadFromBackend(schoolId: Int? = null) {
        runCatching {
            val resp = ApiClient.api.getPendingTeachers()
            if (resp.isSuccessful) {
                val serverUsers = resp.body() ?: emptyList()
                if (serverUsers.isNotEmpty()) {
                    val serverItems = serverUsers.map { u ->
                        PendingTeacherItem(
                            id = u.id.toString(),
                            name = u.nama,
                            nip = u.guru?.nip ?: "-",
                            email = u.email,
                            schoolId = u.idSekolah ?: schoolId ?: 1,
                            schoolName = u.sekolah?.namaSekolah ?: "Sekolah Terpilih",
                            teacherType = "Guru Pengampu",
                            statusGuru = if (u.guru?.nip.isNullOrBlank()) "Guru Honorer" else "PNS",
                            requestedAt = "Terdaftar di Server",
                            isApproved = u.isVerified == true
                        )
                    }
                    _pendingList.update { current ->
                        val localOnly = current.filter { c -> serverItems.none { it.email == c.email } }
                        localOnly + serverItems
                    }
                }
            }
        }
    }

    suspend fun approveTeacher(id: String) {
        _pendingList.update { list ->
            list.map {
                if (it.id == id) it.copy(isApproved = true) else it
            }
        }
        val intId = id.toIntOrNull()
        if (intId != null) {
            runCatching { ApiClient.api.verifyTeacher(intId) }
        }
    }

    fun rejectTeacher(id: String) {
        _pendingList.update { list ->
            list.filter { it.id != id }
        }
    }
}
