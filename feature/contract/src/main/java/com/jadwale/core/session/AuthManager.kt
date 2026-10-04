package com.jadwale.core.session

import com.jadwale.core.model.SchoolConfig
import com.jadwale.core.model.UserRole
import com.jadwale.core.network.ApiClient
import com.jadwale.core.network.LoginRequest
import org.json.JSONObject

/**
 * Manajer otentikasi login ke backend Jadwale online (https://jadwale.salstudioz.web.id/api).
 * Menangani pemanggilan endpoint, parsing token JWT, dan pembaruan session store.
 */
object AuthManager {

    suspend fun login(email: String, pass: String): Result<UserSession> = runCatching {
        val cleanEmail = email.trim()
        val resp = ApiClient.api.login(LoginRequest(cleanEmail, pass))
        
        if (resp.isSuccessful && resp.body() != null) {
            val body = resp.body()!!
            val user = body.user
            val role = when {
                user.email.contains("superadmin", ignoreCase = true) ||
                (user.role != null && (user.role.equals("SUPER_ADMIN", ignoreCase = true) || user.role.equals("SUPERADMIN", ignoreCase = true))) -> UserRole.SUPER_ADMIN
                user.role != null && (user.role.equals("GURU", ignoreCase = true) || user.role.equals("TENAGA_PENDIDIK", ignoreCase = true)) -> UserRole.GURU
                user.role != null && (user.role.equals("USER_BIASA", ignoreCase = true) || user.role.equals("UMUM", ignoreCase = true)) -> UserRole.UMUM
                user.isAdmin == true || (user.role != null && (user.role.equals("ADMIN_SEKOLAH", ignoreCase = true) || user.role.equals("ADMIN", ignoreCase = true))) -> UserRole.ADMIN_SEKOLAH
                else -> UserRole.ADMIN_SEKOLAH
            }
            val session = UserSession(
                token = body.accessToken,
                userId = user.id,
                name = user.nama,
                email = user.email,
                role = role,
                backendRole = user.role ?: "",
                schoolId = user.idSekolah,
                teacherId = user.idGuru,
                schoolName = user.sekolah?.namaSekolah,
                npsn = user.sekolah?.npsn
            )
            SessionStore.save(session)
            user.sekolah?.namaSekolah?.let { SchoolConfig.schoolName = it }
            user.sekolah?.npsn?.let { SchoolConfig.npsn = it }
            session
        } else {
            val rawErr = resp.errorBody()?.string().orEmpty()
            val parsedMsg = try {
                val jsonObj = JSONObject(rawErr)
                jsonObj.optString("message", rawErr.ifBlank { resp.message() })
            } catch (e: Exception) {
                rawErr.ifBlank { resp.message() }
            }
            throw Exception(parsedMsg)
        }
    }

    suspend fun getSchoolList(): Result<List<com.jadwale.core.network.SekolahDto>> = runCatching {
        val resp = ApiClient.api.getSekolahList()
        if (resp.isSuccessful && resp.body() != null) {
            resp.body()!!
        } else {
            val rawErr = resp.errorBody()?.string().orEmpty()
            val parsedMsg = try {
                val jsonObj = JSONObject(rawErr)
                jsonObj.optString("message", rawErr.ifBlank { resp.message() })
            } catch (e: Exception) {
                rawErr.ifBlank { resp.message() }
            }
            throw Exception(parsedMsg)
        }
    }

    suspend fun registerSchool(
        nama: String,
        email: String,
        pass: String,
        namaSekolah: String,
        npsn: String?
    ): Result<String> = runCatching {
        val payload = mutableMapOf<String, Any?>(
            "nama" to nama.trim(),
            "email" to email.trim(),
            "password" to pass,
            "nama_sekolah" to namaSekolah.trim()
        )
        if (!npsn.isNullOrBlank()) {
            payload["npsn"] = npsn.trim()
        }
        val resp = ApiClient.api.signupSchool(payload)
        if (resp.isSuccessful) {
            resp.body()?.message ?: "Pendaftaran sekolah berhasil. Menunggu verifikasi Superadmin."
        } else {
            val rawErr = resp.errorBody()?.string().orEmpty()
            val parsedMsg = try {
                val jsonObj = JSONObject(rawErr)
                jsonObj.optString("message", rawErr.ifBlank { resp.message() })
            } catch (e: Exception) {
                rawErr.ifBlank { resp.message() }
            }
            throw Exception(parsedMsg)
        }
    }

    suspend fun registerTeacher(
        nama: String,
        email: String,
        pass: String,
        idSekolah: Int,
        nip: String?
    ): Result<String> = runCatching {
        val payload = mutableMapOf<String, Any?>(
            "nama" to nama.trim(),
            "email" to email.trim(),
            "password" to pass,
            "id_sekolah" to idSekolah
        )
        if (!nip.isNullOrBlank()) {
            payload["nip"] = nip.trim()
        }
        val resp = ApiClient.api.signupTeacher(payload)
        if (resp.isSuccessful) {
            resp.body()?.message ?: "Pendaftaran guru berhasil. Menunggu persetujuan Admin Sekolah."
        } else {
            val rawErr = resp.errorBody()?.string().orEmpty()
            val parsedMsg = try {
                val jsonObj = JSONObject(rawErr)
                jsonObj.optString("message", rawErr.ifBlank { resp.message() })
            } catch (e: Exception) {
                rawErr.ifBlank { resp.message() }
            }
            throw Exception(parsedMsg)
        }
    }

    suspend fun registerPublic(
        nama: String,
        email: String,
        pass: String
    ): Result<UserSession> = runCatching {
        val payload = mapOf(
            "nama" to nama.trim(),
            "email" to email.trim(),
            "password" to pass
        )
        val resp = ApiClient.api.signupPublic(payload)
        if (resp.isSuccessful && resp.body() != null) {
            val body = resp.body()!!
            val user = body.user
            val session = UserSession(
                token = body.accessToken,
                userId = user.id,
                name = user.nama,
                email = user.email,
                role = UserRole.UMUM,
                backendRole = user.role ?: "USER_BIASA",
                schoolId = user.idSekolah,
                teacherId = user.idGuru,
                schoolName = user.sekolah?.namaSekolah,
                npsn = user.sekolah?.npsn
            )
            SessionStore.save(session)
            session
        } else {
            val rawErr = resp.errorBody()?.string().orEmpty()
            val parsedMsg = try {
                val jsonObj = JSONObject(rawErr)
                jsonObj.optString("message", rawErr.ifBlank { resp.message() })
            } catch (e: Exception) {
                rawErr.ifBlank { resp.message() }
            }
            throw Exception(parsedMsg)
        }
    }
}
