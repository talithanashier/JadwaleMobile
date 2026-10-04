package com.jadwale.core.network

import com.jadwale.core.session.SessionStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

@JvmSuppressWildcards
interface JadwaleApiService {

    // ── AUTH ──
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup/school")
    suspend fun signupSchool(@Body data: Map<String, Any?>): Response<SimpleMessageResponse>

    @POST("auth/signup/teacher")
    suspend fun signupTeacher(@Body data: Map<String, Any?>): Response<SimpleMessageResponse>

    @POST("auth/signup/public")
    suspend fun signupPublic(@Body data: Map<String, Any?>): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getProfile(): Response<UserDto>

    @GET("auth/sekolah-list")
    suspend fun getSekolahList(): Response<List<SekolahDto>>

    // ── SEKOLAH & CONFIG ──
    @GET("sekolah")
    suspend fun getSekolah(): Response<SekolahDto>

    @GET("sekolah/stats")
    suspend fun getSekolahStats(): Response<SchoolStatsDto>

    @PATCH("sekolah")
    suspend fun updateSekolah(@Body data: Map<String, Any?>): Response<SekolahDto>

    @POST("sekolah/hydrate")
    suspend fun hydrateSekolah(@Body data: Map<String, Any?>): Response<SimpleMessageResponse>

    // ── GURU ──
    @GET("guru")
    suspend fun getGurus(): Response<List<GuruDto>>

    @GET("guru/{id}")
    suspend fun getGuru(@Path("id") id: Int): Response<GuruDto>

    @POST("guru")
    suspend fun createGuru(@Body request: CreateGuruRequest): Response<GuruDto>

    @PUT("guru/{id}")
    suspend fun updateGuru(@Path("id") id: Int, @Body request: CreateGuruRequest): Response<GuruDto>

    @DELETE("guru/{id}")
    suspend fun deleteGuru(@Path("id") id: Int): Response<SimpleMessageResponse>

    @GET("guru/pending/list")
    suspend fun getPendingTeachers(): Response<List<UserDto>>

    @POST("guru/{id}/verify")
    suspend fun verifyTeacher(@Path("id") id: Int): Response<UserDto>

    @POST("guru/{id}/availability")
    suspend fun setGuruAvailability(
        @Path("id") id: Int,
        @Body request: SetAvailabilityRequest
    ): Response<SimpleMessageResponse>

    // ── KELAS ──
    @GET("kelas")
    suspend fun getClasses(): Response<List<KelasDto>>

    @POST("kelas")
    suspend fun createClass(@Body request: CreateKelasRequest): Response<KelasDto>

    @PUT("kelas/{id}")
    suspend fun updateClass(@Path("id") id: Int, @Body request: CreateKelasRequest): Response<KelasDto>

    @DELETE("kelas/{id}")
    suspend fun deleteClass(@Path("id") id: Int): Response<SimpleMessageResponse>

    // ── MAPEL ──
    @GET("mapel")
    suspend fun getSubjects(): Response<List<MapelDto>>

    @POST("mapel")
    suspend fun createSubject(@Body request: CreateMapelRequest): Response<MapelDto>

    @PUT("mapel/{id}")
    suspend fun updateSubject(@Path("id") id: Int, @Body request: CreateMapelRequest): Response<MapelDto>

    @DELETE("mapel/{id}")
    suspend fun deleteSubject(@Path("id") id: Int): Response<SimpleMessageResponse>

    // ── JADWAL ──
    @GET("jadwal")
    suspend fun getJadwal(
        @Query("id_kelas") idKelas: Int? = null,
        @Query("id_periode") idPeriode: Int? = null
    ): Response<JadwalResponseDto>

    @GET("jadwal/my-schedule")
    suspend fun getMySchedule(): Response<MyScheduleResponseDto>

    @POST("jadwal/generate")
    suspend fun generateJadwal(): Response<SimpleMessageResponse>

    @GET("jadwal/periode")
    suspend fun getPeriodes(): Response<List<PeriodeJadwalDto>>

    @POST("jadwal/periode")
    suspend fun createPeriode(@Body data: Map<String, Any?>): Response<PeriodeJadwalDto>

    @POST("jadwal/periode/{id}/activate")
    suspend fun activatePeriode(@Path("id") id: Int): Response<PeriodeJadwalDto>

    // ── ADMIN / VERIFIKASI SEKOLAH ──
    @GET("admin/pending-schools")
    suspend fun getPendingSchools(): Response<List<UserDto>>

    @POST("admin/verify-school/{id}")
    suspend fun verifySchool(@Path("id") id: Int): Response<UserDto>
}

object ApiClient {
    const val BASE_URL = "https://jadwale.salstudioz.web.id/api/"

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        val token = SessionStore.token
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        builder.header("Accept", "application/json")
        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: JadwaleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JadwaleApiService::class.java)
    }
}
