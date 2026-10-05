-- CreateTable
CREATE TABLE "Sekolah" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "npsn" TEXT NOT NULL,
    "nama_sekolah" TEXT NOT NULL,
    "alamat" TEXT,
    "deleted_at" DATETIME,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL
);

-- CreateTable
CREATE TABLE "User" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "nama" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "no_hp" TEXT,
    "password" TEXT NOT NULL,
    "is_active" BOOLEAN NOT NULL DEFAULT true,
    "is_admin" BOOLEAN NOT NULL DEFAULT false,
    "deleted_at" DATETIME,
    "last_login" DATETIME,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL,
    CONSTRAINT "User_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Guru" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "nama" TEXT NOT NULL,
    "nip" TEXT,
    "deleted_at" DATETIME,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL,
    CONSTRAINT "Guru_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "GuruAvailability" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_guru" INTEGER NOT NULL,
    "hari" INTEGER NOT NULL,
    "jam_mulai" DATETIME,
    "jam_selesai" DATETIME,
    CONSTRAINT "GuruAvailability_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "GuruAvailability_id_guru_fkey" FOREIGN KEY ("id_guru") REFERENCES "Guru" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Tingkatan" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "nama" TEXT NOT NULL,
    "deleted_at" DATETIME,
    CONSTRAINT "Tingkatan_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Kelas" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_tingkatan" INTEGER NOT NULL,
    "nama_kelas" TEXT NOT NULL,
    "kode_lengkap" TEXT NOT NULL,
    "id_wali_kelas" INTEGER,
    "deleted_at" DATETIME,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Kelas_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "Kelas_id_tingkatan_fkey" FOREIGN KEY ("id_tingkatan") REFERENCES "Tingkatan" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Mapel" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "nama" TEXT NOT NULL,
    "prioritas" BOOLEAN NOT NULL DEFAULT false,
    "color" TEXT NOT NULL DEFAULT '#E2E8F0',
    "deleted_at" DATETIME,
    CONSTRAINT "Mapel_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "MapelTingkatan" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_mapel" INTEGER NOT NULL,
    "id_tingkatan" INTEGER NOT NULL,
    "jp_per_minggu" INTEGER NOT NULL,
    CONSTRAINT "MapelTingkatan_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "MapelTingkatan_id_mapel_fkey" FOREIGN KEY ("id_mapel") REFERENCES "Mapel" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "MapelTingkatan_id_tingkatan_fkey" FOREIGN KEY ("id_tingkatan") REFERENCES "Tingkatan" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "WaliKelas" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_guru" INTEGER NOT NULL,
    "id_kelas" INTEGER NOT NULL,
    CONSTRAINT "WaliKelas_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "WaliKelas_id_guru_fkey" FOREIGN KEY ("id_guru") REFERENCES "Guru" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "WaliKelas_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Pengampu" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_guru" INTEGER NOT NULL,
    "id_mapel" INTEGER NOT NULL,
    "id_kelas" INTEGER NOT NULL,
    CONSTRAINT "Pengampu_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "Pengampu_id_guru_fkey" FOREIGN KEY ("id_guru") REFERENCES "Guru" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "Pengampu_id_mapel_fkey" FOREIGN KEY ("id_mapel") REFERENCES "Mapel" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "Pengampu_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "JpPerHari" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_kelas" INTEGER NOT NULL,
    "hari" INTEGER NOT NULL,
    "jp" INTEGER NOT NULL,
    CONSTRAINT "JpPerHari_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "JpPerHari_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "SchoolConfig" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "is_parallel" BOOLEAN NOT NULL DEFAULT false,
    "class_naming" TEXT NOT NULL DEFAULT 'alphabet',
    "school_days" INTEGER NOT NULL DEFAULT 5,
    "start_time" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "duration_per_jp" INTEGER NOT NULL DEFAULT 35,
    "has_routine" BOOLEAN NOT NULL DEFAULT false,
    "routine_duration" INTEGER NOT NULL DEFAULT 15,
    "break1_duration" INTEGER NOT NULL DEFAULT 15,
    "break2_duration" INTEGER NOT NULL DEFAULT 15,
    "break1_after_jp" INTEGER NOT NULL DEFAULT 3,
    "break2_after_jp" INTEGER NOT NULL DEFAULT 5,
    "has_monday_ceremony" BOOLEAN NOT NULL DEFAULT true,
    "ceremony_duration" INTEGER NOT NULL DEFAULT 35,
    "updated_at" DATETIME NOT NULL,
    CONSTRAINT "SchoolConfig_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "RoutineActivity" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "name" TEXT NOT NULL,
    "day_of_week" INTEGER,
    "time_before_jp" INTEGER DEFAULT 1,
    "duration" INTEGER NOT NULL,
    "is_active" BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT "RoutineActivity_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "ScheduleSlot" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_kelas" INTEGER NOT NULL,
    "hari" INTEGER NOT NULL,
    "jam_ke" INTEGER NOT NULL,
    "waktu_mulai" DATETIME NOT NULL,
    "waktu_selesai" DATETIME NOT NULL,
    "is_break" BOOLEAN NOT NULL DEFAULT false,
    "is_ceremony" BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT "ScheduleSlot_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "ScheduleSlot_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Jadwal" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "id_kelas" INTEGER NOT NULL,
    "hari" INTEGER NOT NULL,
    "jam_ke" INTEGER NOT NULL,
    "id_mapel" INTEGER NOT NULL,
    "id_guru" INTEGER NOT NULL,
    CONSTRAINT "Jadwal_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "Jadwal_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "Jadwal_id_mapel_fkey" FOREIGN KEY ("id_mapel") REFERENCES "Mapel" ("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "Jadwal_id_guru_fkey" FOREIGN KEY ("id_guru") REFERENCES "Guru" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "Template" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL,
    "thumbnail" TEXT,
    "css_styles" TEXT NOT NULL,
    "is_premium" BOOLEAN NOT NULL DEFAULT false,
    "created_by" INTEGER NOT NULL,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" DATETIME NOT NULL,
    CONSTRAINT "Template_created_by_fkey" FOREIGN KEY ("created_by") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "SharedLink" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "uuid" TEXT NOT NULL,
    "id_kelas" INTEGER,
    "permission" TEXT NOT NULL DEFAULT 'read',
    "created_by" INTEGER NOT NULL,
    "allowed_user_id" INTEGER,
    "expires_at" DATETIME,
    "view_count" INTEGER NOT NULL DEFAULT 0,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "SharedLink_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "SharedLink_id_kelas_fkey" FOREIGN KEY ("id_kelas") REFERENCES "Kelas" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "SharedLink_created_by_fkey" FOREIGN KEY ("created_by") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "SharedLink_allowed_user_id_fkey" FOREIGN KEY ("allowed_user_id") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "History" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "id_sekolah" INTEGER NOT NULL,
    "user_id" INTEGER NOT NULL,
    "aksi" TEXT NOT NULL,
    "deskripsi" TEXT,
    "ip_address" TEXT,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "History_id_sekolah_fkey" FOREIGN KEY ("id_sekolah") REFERENCES "Sekolah" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "History_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable
CREATE TABLE "AdminLog" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "admin_user_id" INTEGER NOT NULL,
    "action" TEXT NOT NULL,
    "target_type" TEXT,
    "target_id" INTEGER,
    "details" TEXT,
    "created_at" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "AdminLog_admin_user_id_fkey" FOREIGN KEY ("admin_user_id") REFERENCES "User" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateIndex
CREATE UNIQUE INDEX "Sekolah_npsn_key" ON "Sekolah"("npsn");

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- CreateIndex
CREATE INDEX "User_id_sekolah_idx" ON "User"("id_sekolah");

-- CreateIndex
CREATE INDEX "User_email_idx" ON "User"("email");

-- CreateIndex
CREATE UNIQUE INDEX "Guru_nip_key" ON "Guru"("nip");

-- CreateIndex
CREATE INDEX "Guru_id_sekolah_idx" ON "Guru"("id_sekolah");

-- CreateIndex
CREATE UNIQUE INDEX "GuruAvailability_id_sekolah_id_guru_hari_key" ON "GuruAvailability"("id_sekolah", "id_guru", "hari");

-- CreateIndex
CREATE UNIQUE INDEX "Tingkatan_id_sekolah_nama_key" ON "Tingkatan"("id_sekolah", "nama");

-- CreateIndex
CREATE UNIQUE INDEX "Kelas_id_sekolah_id_tingkatan_nama_kelas_key" ON "Kelas"("id_sekolah", "id_tingkatan", "nama_kelas");

-- CreateIndex
CREATE UNIQUE INDEX "Mapel_id_sekolah_nama_key" ON "Mapel"("id_sekolah", "nama");

-- CreateIndex
CREATE UNIQUE INDEX "MapelTingkatan_id_sekolah_id_mapel_id_tingkatan_key" ON "MapelTingkatan"("id_sekolah", "id_mapel", "id_tingkatan");

-- CreateIndex
CREATE UNIQUE INDEX "WaliKelas_id_sekolah_id_guru_key" ON "WaliKelas"("id_sekolah", "id_guru");

-- CreateIndex
CREATE UNIQUE INDEX "WaliKelas_id_sekolah_id_kelas_key" ON "WaliKelas"("id_sekolah", "id_kelas");

-- CreateIndex
CREATE UNIQUE INDEX "Pengampu_id_sekolah_id_guru_id_mapel_id_kelas_key" ON "Pengampu"("id_sekolah", "id_guru", "id_mapel", "id_kelas");

-- CreateIndex
CREATE UNIQUE INDEX "JpPerHari_id_sekolah_id_kelas_hari_key" ON "JpPerHari"("id_sekolah", "id_kelas", "hari");

-- CreateIndex
CREATE UNIQUE INDEX "SchoolConfig_id_sekolah_key" ON "SchoolConfig"("id_sekolah");

-- CreateIndex
CREATE UNIQUE INDEX "ScheduleSlot_id_sekolah_id_kelas_hari_jam_ke_key" ON "ScheduleSlot"("id_sekolah", "id_kelas", "hari", "jam_ke");

-- CreateIndex
CREATE INDEX "Jadwal_id_sekolah_id_kelas_idx" ON "Jadwal"("id_sekolah", "id_kelas");

-- CreateIndex
CREATE INDEX "Jadwal_id_guru_hari_jam_ke_idx" ON "Jadwal"("id_guru", "hari", "jam_ke");

-- CreateIndex
CREATE UNIQUE INDEX "Jadwal_id_sekolah_id_kelas_hari_jam_ke_key" ON "Jadwal"("id_sekolah", "id_kelas", "hari", "jam_ke");

-- CreateIndex
CREATE UNIQUE INDEX "SharedLink_uuid_key" ON "SharedLink"("uuid");

-- CreateIndex
CREATE INDEX "SharedLink_uuid_idx" ON "SharedLink"("uuid");

-- CreateIndex
CREATE INDEX "SharedLink_expires_at_idx" ON "SharedLink"("expires_at");

-- CreateIndex
CREATE INDEX "History_id_sekolah_user_id_idx" ON "History"("id_sekolah", "user_id");

-- CreateIndex
CREATE INDEX "AdminLog_admin_user_id_idx" ON "AdminLog"("admin_user_id");
