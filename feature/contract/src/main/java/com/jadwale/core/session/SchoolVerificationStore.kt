package com.jadwale.core.session

import com.jadwale.core.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PendingSchoolItem(
    val id: String,
    val name: String,
    val npsn: String,
    val email: String,
    val headmaster: String,
    val address: String,
    val registrationDate: String = "Baru Saja Mendaftar",
    var status: String = "Menunggu Verifikasi"
)

object SchoolVerificationStore {
    private val _pendingSchools = MutableStateFlow<List<PendingSchoolItem>>(emptyList())
    val pendingSchools: StateFlow<List<PendingSchoolItem>> = _pendingSchools.asStateFlow()

    fun addPendingSchool(
        namaAdmin: String,
        emailSekolah: String,
        namaSekolah: String,
        npsn: String?
    ) {
        val cleanNpsn = if (!npsn.isNullOrBlank()) npsn else Math.floor(10000000 + Math.random() * 90000000).toLong().toString().take(8)
        val newItem = PendingSchoolItem(
            id = "sch_reg_${System.currentTimeMillis()}",
            name = namaSekolah,
            npsn = cleanNpsn,
            email = emailSekolah,
            headmaster = namaAdmin,
            address = "Wilayah Binaan Dinas Pendidikan",
            registrationDate = "Baru Mendaftar via Registrasi Website",
            status = "Menunggu Verifikasi"
        )
        _pendingSchools.update { current ->
            listOf(newItem) + current.filter { it.email != emailSekolah && it.name != namaSekolah }
        }
    }

    suspend fun loadFromBackend() {
        runCatching {
            val resp = ApiClient.api.getPendingSchools()
            if (resp.isSuccessful) {
                val serverUsers = resp.body() ?: emptyList()
                if (serverUsers.isNotEmpty()) {
                    val serverItems = serverUsers.map { u ->
                        PendingSchoolItem(
                            id = u.id.toString(),
                            name = u.sekolah?.namaSekolah ?: "Sekolah Baru",
                            npsn = u.sekolah?.npsn ?: "-",
                            email = u.email,
                            headmaster = u.nama,
                            address = u.sekolah?.alamat ?: "Wilayah Supervisi",
                            registrationDate = "Terdaftar di Server",
                            status = if (u.isVerified == true) "✓ Terverifikasi Disetujui" else "Menunggu Verifikasi"
                        )
                    }
                    _pendingSchools.update { current ->
                        val localOnly = current.filter { c -> serverItems.none { it.email == c.email } }
                        localOnly + serverItems
                    }
                }
            }
        }
    }

    suspend fun approveSchool(id: String) {
        _pendingSchools.update { list ->
            list.map {
                if (it.id == id) it.copy(status = "✓ Terverifikasi Disetujui") else it
            }
        }
        val intId = id.toIntOrNull()
        if (intId != null) {
            runCatching { ApiClient.api.verifySchool(intId) }
        }
    }

    fun rejectSchool(id: String) {
        _pendingSchools.update { list ->
            list.map {
                if (it.id == id) it.copy(status = "✕ Ditolak Superadmin") else it
            }
        }
    }
}
