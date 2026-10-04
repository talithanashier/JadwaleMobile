package com.jadwale.core.session

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jadwale.core.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sesi pengguna yang sedang login ke Jadwale API.
 * Diisi dari respons POST /api/auth/login.
 */
data class UserSession(
    val token: String,
    val userId: Int,
    val name: String,
    val email: String,
    val role: UserRole,
    val backendRole: String,
    val schoolId: Int? = null,
    val teacherId: Int? = null,
    val schoolName: String? = null,
    val npsn: String? = null
)

/**
 * Penyimpanan sesi (token JWT + profil) yang dipakai bersama oleh seluruh modul.
 * Disimpan di SharedPreferences agar pengguna tetap login setelah aplikasi ditutup.
 */
object SessionStore {
    private const val PREFS_NAME = "jadwale_session"
    private const val KEY_SESSION = "session_json"

    private val gson = Gson()
    private var prefs: SharedPreferences? = null

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    val current: UserSession? get() = _session.value
    val token: String? get() = _session.value?.token

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = p
        _session.value = p.getString(KEY_SESSION, null)?.let { json ->
            runCatching { gson.fromJson(json, UserSession::class.java) }.getOrNull()
                ?.takeIf { !it.token.isNullOrBlank() }
        }
    }

    fun save(session: UserSession) {
        _session.value = session
        prefs?.edit()?.putString(KEY_SESSION, gson.toJson(session))?.apply()
    }

    fun updateSchool(name: String?, npsn: String?) {
        val s = _session.value ?: return
        save(s.copy(schoolName = name ?: s.schoolName, npsn = npsn ?: s.npsn))
    }

    fun clear() {
        _session.value = null
        prefs?.edit()?.remove(KEY_SESSION)?.apply()
    }
}
