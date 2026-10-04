package com.jadwale.core.network

import com.google.gson.annotations.SerializedName

// ── Auth DTOs ──
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val nama: String,
    val email: String,
    val role: String?,
    @SerializedName("is_admin") val isAdmin: Boolean? = false,
    @SerializedName("is_verified") val isVerified: Boolean? = true,
    @SerializedName("is_active") val isActive: Boolean? = true,
    @SerializedName("id_sekolah") val idSekolah: Int? = null,
    @SerializedName("id_guru") val idGuru: Int? = null,
    val sekolah: SekolahDto? = null,
    val guru: GuruDto? = null
)

// ── Sekolah DTOs ──
data class SekolahDto(
    val id: Int? = null,
    val npsn: String? = null,
    @SerializedName("nama_sekolah") val namaSekolah: String? = null,
    val alamat: String? = null,
    val config: SchoolConfigDto? = null
)

data class SchoolConfigDto(
    val id: Int? = null,
    @SerializedName("id_sekolah") val idSekolah: Int? = null,
    @SerializedName("is_parallel") val isParallel: Boolean? = false,
    @SerializedName("class_naming") val classNaming: String? = "alphabet",
    @SerializedName("school_days") val schoolDays: Int? = 5,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("duration_per_jp") val durationPerJp: Int? = 35,
    @SerializedName("has_routine") val hasRoutine: Boolean? = false,
    @SerializedName("routine_duration") val routineDuration: Int? = 15,
    @SerializedName("break1_duration") val break1Duration: Int? = 15,
    @SerializedName("break2_duration") val break2Duration: Int? = 15,
    @SerializedName("break1_after_jp") val break1AfterJp: Int? = 3,
    @SerializedName("break2_after_jp") val break2AfterJp: Int? = 5,
    @SerializedName("has_monday_ceremony") val hasMondayCeremony: Boolean? = true,
    @SerializedName("ceremony_duration") val ceremonyDuration: Int? = 35
)

data class SchoolStatsDto(
    val guru: Int = 0,
    val kelas: Int = 0,
    val mapel: Int = 0,
    val jadwal: Int = 0
)

// ── Guru DTOs ──
data class GuruDto(
    val id: Int? = null,
    @SerializedName("id_sekolah") val idSekolah: Int? = null,
    val nama: String,
    val nip: String? = null,
    val guruAvailabilities: List<GuruAvailabilityDto>? = null,
    val users: List<UserDto>? = null
)

data class GuruAvailabilityDto(
    val id: Int? = null,
    val hari: Int,
    @SerializedName("jam_mulai") val jamMulai: String? = null,
    @SerializedName("jam_selesai") val jamSelesai: String? = null
)

data class CreateGuruRequest(
    val nama: String,
    val nip: String? = null
)

data class SetAvailabilityRequest(
    val availabilities: List<GuruAvailabilityItem>
)

data class GuruAvailabilityItem(
    val hari: Int
)

// ── Kelas DTOs ──
data class KelasDto(
    val id: Int? = null,
    @SerializedName("id_sekolah") val idSekolah: Int? = null,
    @SerializedName("id_tingkatan") val idTingkatan: Int? = null,
    @SerializedName("nama_kelas") val namaKelas: String,
    @SerializedName("kode_lengkap") val kodeLengkap: String? = null,
    val tingkatan: TingkatanDto? = null,
    val waliKelasList: List<WaliKelasDto>? = null
)

data class TingkatanDto(
    val id: Int,
    val nama: String
)

data class WaliKelasDto(
    val id: Int,
    @SerializedName("id_guru") val idGuru: Int,
    @SerializedName("id_kelas") val idKelas: Int,
    val guru: GuruDto? = null
)

data class CreateKelasRequest(
    @SerializedName("nama_kelas") val namaKelas: String,
    @SerializedName("id_tingkatan") val idTingkatan: Int? = null,
    @SerializedName("kode_lengkap") val kodeLengkap: String? = null,
    @SerializedName("id_guru_wali") val idGuruWali: Int? = null
)

// ── Mapel DTOs ──
data class MapelDto(
    val id: Int? = null,
    @SerializedName("id_sekolah") val idSekolah: Int? = null,
    val nama: String,
    val prioritas: Boolean = false,
    val color: String = "#E2E8F0",
    val mapelTingkatans: List<MapelTingkatanDto>? = null
)

data class MapelTingkatanDto(
    val id: Int? = null,
    @SerializedName("id_mapel") val idMapel: Int? = null,
    @SerializedName("id_tingkatan") val idTingkatan: Int? = null,
    @SerializedName("jp_per_minggu") val jpPerMinggu: Int = 2,
    val tingkatan: TingkatanDto? = null
)

data class CreateMapelRequest(
    val nama: String,
    val prioritas: Boolean = false,
    val color: String = "#1D68E4",
    @SerializedName("jp_per_tingkatan") val jpPerTingkatan: Map<String, Int>? = null
)

// ── Jadwal DTOs ──
data class JadwalResponseDto(
    val config: SchoolConfigDto? = null,
    val routines: List<RoutineDto>? = null,
    val jadwal: List<JadwalItemDto> = emptyList(),
    val bebanGuru: List<BebanGuruDto>? = null,
    val periodes: List<PeriodeJadwalDto>? = null,
    @SerializedName("current_periode_id") val currentPeriodeId: Int? = null
)

data class JadwalItemDto(
    val id: Int,
    @SerializedName("id_sekolah") val idSekolah: Int,
    @SerializedName("id_periode_jadwal") val idPeriodeJadwal: Int? = null,
    @SerializedName("id_kelas") val idKelas: Int,
    val hari: Int, // 1: Senin s/d 6: Sabtu
    @SerializedName("jam_ke") val jamKe: Int,
    @SerializedName("id_mapel") val idMapel: Int,
    @SerializedName("id_guru") val idGuru: Int,
    val kelas: KelasDto? = null,
    val mapel: MapelDto? = null,
    val guru: GuruDto? = null
)

data class RoutineDto(
    val id: Int,
    val name: String,
    @SerializedName("day_of_week") val dayOfWeek: Int? = null,
    @SerializedName("time_before_jp") val timeBeforeJp: Int? = 1,
    val duration: Int,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class BebanGuruDto(
    val id: Int,
    val nama: String,
    @SerializedName("total_jp") val totalJp: Int,
    @SerializedName("rincian_kelas") val rincianKelas: String? = null
)

data class PeriodeJadwalDto(
    val id: Int,
    val nama: String,
    @SerializedName("tahun_ajaran") val tahunAjaran: String? = null,
    val semester: String? = null,
    @SerializedName("is_active") val isActive: Boolean = false
)

data class MyScheduleResponseDto(
    val guru: GuruDto? = null,
    val config: SchoolConfigDto? = null,
    val periode: PeriodeJadwalDto? = null,
    val jadwals: List<JadwalItemDto> = emptyList(),
    @SerializedName("total_jp") val totalJp: Int = 0
)

data class SimpleMessageResponse(
    val message: String? = null,
    val status: String? = null
)
