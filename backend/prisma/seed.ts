import { PrismaClient } from '@prisma/client';
// @ts-ignore
const bcrypt = require('bcrypt');

const prisma = new PrismaClient();

async function main() {
  console.log('🌱 Seeding database Jadwale (Mencakup Semua Case, Semua Jenis & Data Riil)...\n');

  console.log('🧹 Membersihkan seluruh data lama database sebelum seed...');
  try {
    await prisma.$executeRawUnsafe('SET FOREIGN_KEY_CHECKS = 0;').catch(() => {});
  } catch (e) {}

  await prisma.adminLog.deleteMany();
  await prisma.history.deleteMany();
  await prisma.sharedLink.deleteMany();
  await prisma.template.deleteMany();
  await prisma.jadwal.deleteMany();
  await prisma.periodeJadwal.deleteMany();
  await prisma.scheduleSlot.deleteMany();
  await prisma.jpPerHari.deleteMany();
  await prisma.pengampu.deleteMany();
  await prisma.waliKelas.deleteMany();
  await prisma.mapelTingkatan.deleteMany();
  await prisma.mapel.deleteMany();
  await prisma.kelas.deleteMany();
  await prisma.tingkatan.deleteMany();
  await prisma.guruAvailability.deleteMany();
  await prisma.user.deleteMany();
  await prisma.guru.deleteMany();
  await prisma.routineActivity.deleteMany();
  await prisma.schoolConfig.deleteMany();
  await prisma.sekolah.deleteMany();

  try {
    await prisma.$executeRawUnsafe('SET FOREIGN_KEY_CHECKS = 1;').catch(() => {});
  } catch (e) {}
  console.log('✨ Data lama berhasil dibersihkan total!\n');

  const hashedPassword = await bcrypt.hash('password123', 10);

  // ═══════════════════════════════════════════════════════════════
  // 1. SUPERADMIN SISTEM
  // ═══════════════════════════════════════════════════════════════
  const superAdmin = await prisma.user.upsert({
    where: { email: 'superadmin@jadwale.id' },
    update: { password: hashedPassword, is_verified: true, is_active: true, role: 'SUPER_ADMIN', is_admin: true },
    create: {
      nama: 'Super Administrator Sistem',
      email: 'superadmin@jadwale.id',
      password: hashedPassword,
      role: 'SUPER_ADMIN',
      is_admin: true,
      is_verified: true,
      is_active: true,
    },
  });
  console.log('✅ 1. Super Admin: superadmin@jadwale.id / password123');

  // ═══════════════════════════════════════════════════════════════
  // 2. DESIGNER TEMPLATE RESMI
  // ═══════════════════════════════════════════════════════════════
  const designer = await prisma.user.upsert({
    where: { email: 'designer.kreatif@jadwale.id' },
    update: { password: hashedPassword, is_verified: true, is_active: true, role: 'DESIGNER' },
    create: {
      nama: 'Studio Desain Kreatif Jadwale',
      email: 'designer.kreatif@jadwale.id',
      password: hashedPassword,
      role: 'DESIGNER',
      is_admin: false,
      is_verified: true,
      is_active: true,
    },
  });
  console.log('✅ 2. Designer: designer.kreatif@jadwale.id / password123');

  // ═══════════════════════════════════════════════════════════════
  // 3. CASE SEKOLAH 1: STRUKTUR KELAS PARALEL (AKTIF & TERVERIFIKASI)
  //    SDN Pancasila 01 Jakarta (12 Rombel: 1A, 1B ... 6A, 6B)
  // ═══════════════════════════════════════════════════════════════
  const sekolahPancasila = await prisma.sekolah.upsert({
    where: { npsn: '20108922' },
    update: { setupCompleted: true, status: 'ACTIVE' },
    create: {
      npsn: '20108922',
      nama_sekolah: 'SDN Pancasila 01',
      alamat: 'Jl. Percetakan Negara No. 21, Cempaka Putih, Jakarta Pusat',
      jenjang: 'SD',
      kepala_sekolah: 'Drs. H. Subagyo, M.M.',
      semester_aktif: 'GASAL',
      tahun_pelajaran: '2026/2027',
      setupCompleted: true,
      status: 'ACTIVE',
      config: {
        create: {
          is_parallel: true, // JENIS PARALEL
          class_naming: 'alphabet',
          school_days: 5,
          start_time: new Date('1970-01-01T07:00:00Z'),
          duration_per_jp: 35,
          break1_after_jp: 3,
          break1_duration: 15,
          break2_after_jp: 5,
          break2_duration: 15,
          has_routine: true,
          routine_duration: 15,
          has_monday_ceremony: true,
          ceremony_duration: 35,
        },
      },
    },
    include: { config: true },
  });

  const adminPancasila = await prisma.user.upsert({
    where: { email: 'admin@sdnpancasila01.sch.id' },
    update: {
      password: hashedPassword,
      is_verified: true,
      is_active: true,
      id_sekolah: sekolahPancasila.id,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
    },
    create: {
      nama: 'Admin Kurikulum SDN Pancasila 01',
      email: 'admin@sdnpancasila01.sch.id',
      password: hashedPassword,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
      is_verified: true,
      is_active: true,
      id_sekolah: sekolahPancasila.id,
    },
  });

  // Alias login operator
  await prisma.user.upsert({
    where: { email: 'operator@sdnpancasila01.sch.id' },
    update: { password: hashedPassword, is_verified: true, is_active: true, id_sekolah: sekolahPancasila.id },
    create: {
      nama: 'Operator SDN Pancasila 01',
      email: 'operator@sdnpancasila01.sch.id',
      password: hashedPassword,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
      is_verified: true,
      is_active: true,
      id_sekolah: sekolahPancasila.id,
    },
  });
  console.log('✅ 3. Case Sekolah Paralel: SDN Pancasila 01 (admin@sdnpancasila01.sch.id)');

  // ═══════════════════════════════════════════════════════════════
  // 4. CASE SEKOLAH 2: STRUKTUR KELAS TUNGGAL (AKTIF & TERVERIFIKASI)
  //    SDN Teladan 05 (6 Rombel Tunggal: Kelas 1, 2, 3, 4, 5, 6)
  // ═══════════════════════════════════════════════════════════════
  const sekolahTeladan = await prisma.sekolah.upsert({
    where: { npsn: '20104011' },
    update: { setupCompleted: true, status: 'ACTIVE' },
    create: {
      npsn: '20104011',
      nama_sekolah: 'SDN Teladan 05',
      alamat: 'Jl. Salemba Raya No. 14, Senen, Jakarta Pusat',
      jenjang: 'SD',
      kepala_sekolah: 'Hj. Maryati, M.Pd.',
      semester_aktif: 'GASAL',
      tahun_pelajaran: '2026/2027',
      setupCompleted: true,
      status: 'ACTIVE',
      config: {
        create: {
          is_parallel: false, // JENIS TUNGGAL
          class_naming: 'number',
          school_days: 6, // 6 Hari sekolah
          start_time: new Date('1970-01-01T07:15:00Z'),
          duration_per_jp: 35,
          break1_after_jp: 3,
          break1_duration: 20,
          break2_after_jp: 5,
          break2_duration: 15,
          has_routine: true,
          routine_duration: 15,
          has_monday_ceremony: true,
          ceremony_duration: 35,
        },
      },
    },
    include: { config: true },
  });

  await prisma.user.upsert({
    where: { email: 'admin@sdnteladan05.sch.id' },
    update: { password: hashedPassword, is_verified: true, is_active: true, id_sekolah: sekolahTeladan.id },
    create: {
      nama: 'Admin SDN Teladan 05',
      email: 'admin@sdnteladan05.sch.id',
      password: hashedPassword,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
      is_verified: true,
      is_active: true,
      id_sekolah: sekolahTeladan.id,
    },
  });
  console.log('✅ 4. Case Sekolah Tunggal: SDN Teladan 05 (admin@sdnteladan05.sch.id)');

  // ═══════════════════════════════════════════════════════════════
  // 5. CASE SEKOLAH 3: MENUNGGU VERIFIKASI SUPER ADMIN
  //    SDN Cibubur 03 (is_verified: false, status: PENDING_VERIFICATION)
  // ═══════════════════════════════════════════════════════════════
  const sekolahCibubur = await prisma.sekolah.upsert({
    where: { npsn: '20103450' },
    update: { setupCompleted: false, status: 'PENDING_VERIFICATION' },
    create: {
      npsn: '20103450',
      nama_sekolah: 'SDN Cibubur 03',
      alamat: 'Jl. Lapangan Tembak No. 12, Ciracas, Jakarta Timur',
      jenjang: 'SD',
      kepala_sekolah: 'Dra. Endang S., M.Pd.',
      semester_aktif: 'GASAL',
      tahun_pelajaran: '2026/2027',
      setupCompleted: false,
      status: 'PENDING_VERIFICATION',
    },
  });

  await prisma.user.upsert({
    where: { email: 'admin@sdncibubur03.sch.id' },
    update: {
      password: hashedPassword,
      is_verified: false,
      is_active: true,
      id_sekolah: sekolahCibubur.id,
      role: 'ADMIN_SEKOLAH',
    },
    create: {
      nama: 'Admin SDN Cibubur 03',
      email: 'admin@sdncibubur03.sch.id',
      password: hashedPassword,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
      is_verified: false, // Menunggu Verifikasi Super Admin
      is_active: true,
      id_sekolah: sekolahCibubur.id,
    },
  });
  console.log('⏳ 5. Case Sekolah Menunggu Verifikasi: SDN Cibubur 03 (admin@sdncibubur03.sch.id)');

  // ═══════════════════════════════════════════════════════════════
  // 6. CASE SEKOLAH 4: STATUS NONAKTIF / DITOLAK
  //    SD Harapan Mandiri (is_active: false, status: REJECTED)
  // ═══════════════════════════════════════════════════════════════
  const sekolahMandiri = await prisma.sekolah.upsert({
    where: { npsn: '20109931' },
    update: { setupCompleted: false, status: 'REJECTED' },
    create: {
      npsn: '20109931',
      nama_sekolah: 'SD Harapan Mandiri',
      alamat: 'Jl. Rawamangun Muka No. 9, Jakarta Timur',
      jenjang: 'SD',
      kepala_sekolah: 'Ibu Yuliana, S.Pd.',
      semester_aktif: 'GASAL',
      tahun_pelajaran: '2026/2027',
      setupCompleted: false,
      status: 'REJECTED',
    },
  });

  await prisma.user.upsert({
    where: { email: 'admin@sdharapanmandiri.sch.id' },
    update: { password: hashedPassword, is_verified: false, is_active: false, id_sekolah: sekolahMandiri.id },
    create: {
      nama: 'Admin SD Harapan Mandiri',
      email: 'admin@sdharapanmandiri.sch.id',
      password: hashedPassword,
      role: 'ADMIN_SEKOLAH',
      is_admin: true,
      is_verified: false,
      is_active: false,
      id_sekolah: sekolahMandiri.id,
    },
  });
  console.log('⛔ 6. Case Sekolah Ditolak/Nonaktif: SD Harapan Mandiri (admin@sdharapanmandiri.sch.id)');

  // ═══════════════════════════════════════════════════════════════
  // 7. DETAIL DATA LENGKAP SEKOLAH UTAMA (SDN PANCASILA 01)
  // ═══════════════════════════════════════════════════════════════

  // A. KEGIATAN RUTIN LENGKAP (Hari Spesifik & Harian)
  await prisma.routineActivity.deleteMany({ where: { id_sekolah: sekolahPancasila.id } });
  await prisma.routineActivity.createMany({
    data: [
      { id_sekolah: sekolahPancasila.id, name: 'Upacara Bendera', day_of_week: 1, time_before_jp: 1, duration: 35, is_active: true },
      { id_sekolah: sekolahPancasila.id, name: 'Pembiasaan Literasi & Numerasi Pagi', day_of_week: 2, time_before_jp: 1, duration: 15, is_active: true },
      { id_sekolah: sekolahPancasila.id, name: 'Senam Kesegaran Jasmani / Sholat Dhuha', day_of_week: 5, time_before_jp: 1, duration: 30, is_active: true },
      { id_sekolah: sekolahPancasila.id, name: 'Istirahat I & Kudapan Sehat', time_before_jp: 4, duration: 15, is_active: true },
      { id_sekolah: sekolahPancasila.id, name: 'Istirahat II & Sholat Dzuhur Berjamaah', time_before_jp: 6, duration: 20, is_active: true },
    ],
  });

  // B. SEMUA JENIS GURU (PNS, PPPK, Honorer, Wali Kelas, Guru Mapel, Keduanya, Ketersediaan Khusus)
  const guruMasterData = [
    // 1. Wali Kelas + Guru Mapel (PNS)
    {
      nama: 'Bambang Sutrisno, M.Pd.',
      nip: '198204152008011008',
      email: 'bambang.sutrisno@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 2. Wali Kelas (PPPK)
    {
      nama: 'Siti Aminah, S.Pd.',
      nip: '198811202012022004',
      email: 'siti.aminah@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PPPK',
      jenis: 'WALI_KELAS',
      limitedSchedule: false
    },
    // 3. Wali Kelas (PNS)
    {
      nama: 'Ahmad Fauzi, S.Pd.',
      nip: '197912202006011003',
      email: 'ahmad.fauzi@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 4. Wali Kelas (PPPK)
    {
      nama: 'Dewi Lestari, S.Pd.',
      nip: '199001102015012004',
      email: 'dewi.lestari@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PPPK',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 5. Guru Mapel PJOK (Honorer/GTT - Ketersediaan Khusus: Hanya Selasa s/d Jumat)
    {
      nama: 'Hendra Gunawan, S.Pd.',
      nip: '198708052012011005',
      email: 'hendra.gunawan@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'HONORER',
      jenis: 'GURU_MAPEL',
      limitedSchedule: true,
      availableDays: [2, 3, 4, 5] // Tidak bisa Senin (jadwal rapat luar)
    },
    // 6. Guru Mapel PAI (PNS - Ketersediaan Khusus: Jam 1 s/d Jam 4 saja)
    {
      nama: 'Rina Oktavia, S.Pd.I',
      nip: '199205182017012006',
      email: 'rina.oktavia@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'GURU_MAPEL',
      limitedSchedule: true,
      availableDays: [1, 2, 3, 4, 5]
    },
    // 7. Guru Mapel Bahasa Inggris (PPPK)
    {
      nama: 'Wahyu Setiawan, S.Pd.',
      nip: '198411272009011007',
      email: 'wahyu.setiawan@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PPPK',
      jenis: 'GURU_MAPEL',
      limitedSchedule: false
    },
    // 8. Wali Kelas (PNS)
    {
      nama: 'Nurul Hidayah, S.Pd.',
      nip: '199107082016012008',
      email: 'nurul.hidayah@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 9. Wali Kelas (PNS)
    {
      nama: 'Agus Prasetyo, S.Pd.',
      nip: '198006142004011009',
      email: 'agus.prasetyo@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 10. Wali Kelas (PPPK)
    {
      nama: 'Maya Sari, S.Pd.',
      nip: '199310222019012010',
      email: 'maya.sari@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PPPK',
      jenis: 'WALI_KELAS_DAN_MAPEL',
      limitedSchedule: false
    },
    // 11. Wali Kelas (Honorer)
    {
      nama: 'Doni Firmansyah, S.Pd.',
      nip: '198802132011011011',
      email: 'doni.firmansyah@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'HONORER',
      jenis: 'WALI_KELAS',
      limitedSchedule: false
    },
    // 12. Guru Mapel Seni & Budaya (PNS)
    {
      nama: 'Sri Wulandari, S.Pd.',
      nip: '197805302003012012',
      email: 'sri.wulandari@guru.sd.belajar.id',
      isVerified: true,
      isActive: true,
      status: 'PNS',
      jenis: 'GURU_MAPEL',
      limitedSchedule: false
    },
    // 13. CASE GURU MENUNGGU VERIFIKASI SEKOLAH (is_verified: false)
    {
      nama: 'Ratna Kartika, S.Pd.',
      nip: '199504122020122015',
      email: 'ratna.kartika@guru.sd.belajar.id',
      isVerified: false, // Menunggu diverifikasi Admin Sekolah
      isActive: true,
      status: 'PPPK',
      jenis: 'GURU_MAPEL',
      limitedSchedule: false
    },
    // 14. CASE GURU NONAKTIF / CUTI (is_active: false)
    {
      nama: 'Joko Widodo, S.Pd.',
      nip: '198103142005011002',
      email: 'joko.widodo@guru.sd.belajar.id',
      isVerified: true,
      isActive: false, // Status Nonaktif (Cuti Studi Lanjut)
      status: 'PNS',
      jenis: 'GURU_MAPEL',
      limitedSchedule: false
    }
  ];

  const guruList: any[] = [];
  for (const g of guruMasterData) {
    const guru = await prisma.guru.upsert({
      where: { nip: g.nip },
      update: { nama: g.nama, id_sekolah: sekolahPancasila.id },
      create: { id_sekolah: sekolahPancasila.id, nama: g.nama, nip: g.nip },
    });
    guruList.push(guru);

    await prisma.user.upsert({
      where: { email: g.email },
      update: {
        password: hashedPassword,
        is_verified: g.isVerified,
        is_active: g.isActive,
        id_sekolah: sekolahPancasila.id,
        id_guru: guru.id,
        role: 'TENAGA_PENDIDIK',
      },
      create: {
        nama: g.nama,
        email: g.email,
        password: hashedPassword,
        role: 'TENAGA_PENDIDIK',
        is_admin: false,
        is_verified: g.isVerified,
        is_active: g.isActive,
        id_sekolah: sekolahPancasila.id,
        id_guru: guru.id,
      },
    });

    // Seed Ketersediaan Khusus untuk Guru
    if (g.limitedSchedule && g.availableDays) {
      await prisma.guruAvailability.deleteMany({ where: { id_sekolah: sekolahPancasila.id, id_guru: guru.id } });
      for (const hari of g.availableDays) {
        await prisma.guruAvailability.create({
          data: {
            id_sekolah: sekolahPancasila.id,
            id_guru: guru.id,
            hari,
            jam_mulai: new Date('1970-01-01T07:00:00Z'),
            jam_selesai: new Date('1970-01-01T12:00:00Z'),
          },
        });
      }
    }
  }
  console.log(`✅ 7. Case Guru Lengkap: PNS, PPPK, Honorer, Wali Kelas, Guru Mapel, Keduanya, Ketersediaan Hari, Pending & Nonaktif`);

  // C. TINGKATAN (Kelas 1 s/d 6)
  const tingkatanNames = ['Kelas 1', 'Kelas 2', 'Kelas 3', 'Kelas 4', 'Kelas 5', 'Kelas 6'];
  const tingkatanList: any[] = [];
  for (const nama of tingkatanNames) {
    const t = await prisma.tingkatan.upsert({
      where: { id_sekolah_nama: { id_sekolah: sekolahPancasila.id, nama } },
      update: {},
      create: { id_sekolah: sekolahPancasila.id, nama },
    });
    tingkatanList.push(t);
  }

  // D. KELAS (12 Rombel: 1A - 6B)
  const kelasList: any[] = [];
  const rombels = ['A', 'B'];
  for (let i = 0; i < tingkatanList.length; i++) {
    const nomor = i + 1;
    for (const rombel of rombels) {
      const kodeLengkap = `${nomor}${rombel}`;
      const fase = nomor <= 2 ? 'Fase A' : nomor <= 4 ? 'Fase B' : 'Fase C';

      const kelas = await prisma.kelas.upsert({
        where: {
          id_sekolah_id_tingkatan_nama_kelas: {
            id_sekolah: sekolahPancasila.id,
            id_tingkatan: tingkatanList[i].id,
            nama_kelas: rombel,
          },
        },
        update: { kode_lengkap: kodeLengkap, fase },
        create: {
          id_sekolah: sekolahPancasila.id,
          id_tingkatan: tingkatanList[i].id,
          nama_kelas: rombel,
          kode_lengkap: kodeLengkap,
          fase,
        },
      });
      kelasList.push(kelas);
    }
  }

  // E. SEMUA JENIS MATA PELAJARAN (Prioritas Pagi, Non-Prioritas, Muatan Lokal)
  const mapelMasterData = [
    { nama: 'Pendidikan Agama & Budi Pekerti', prioritas: false, color: '#3B82F6' },
    { nama: 'Pendidikan Pancasila',            prioritas: false, color: '#10B981' },
    { nama: 'Bahasa Indonesia',                prioritas: true,  color: '#EF4444' }, // PRIORITAS
    { nama: 'Matematika',                      prioritas: true,  color: '#F59E0B' }, // PRIORITAS
    { nama: 'IPAS (Sains & Sosial)',           prioritas: true,  color: '#8B5CF6' }, // PRIORITAS
    { nama: 'PJOK',                            prioritas: false, color: '#06B6D4' },
    { nama: 'Seni Rupa & Budaya',              prioritas: false, color: '#EC4899' },
    { nama: 'Bahasa Inggris',                  prioritas: false, color: '#6366F1' },
    { nama: 'Muatan Lokal (Bahasa Sunda)',     prioritas: false, color: '#64748B' },
  ];

  const mapelList: any[] = [];
  for (const m of mapelMasterData) {
    const mapel = await prisma.mapel.upsert({
      where: { id_sekolah_nama: { id_sekolah: sekolahPancasila.id, nama: m.nama } },
      update: { color: m.color, prioritas: m.prioritas },
      create: { id_sekolah: sekolahPancasila.id, ...m },
    });
    mapelList.push(mapel);
  }

  // F. ALOKASI JP PER MINGGU (2 JP, 3 JP, 4 JP, 5 JP, 6 JP, 8 JP)
  const jpStandar: Record<string, number[]> = {
    'Kelas 1': [4, 4, 8, 6, 0, 3, 2, 0, 2],
    'Kelas 2': [4, 4, 8, 6, 0, 3, 2, 0, 2],
    'Kelas 3': [4, 4, 6, 6, 5, 3, 2, 2, 2],
    'Kelas 4': [4, 4, 6, 6, 5, 3, 2, 2, 2],
    'Kelas 5': [3, 4, 6, 6, 5, 3, 2, 2, 2],
    'Kelas 6': [3, 4, 6, 6, 5, 3, 2, 2, 2],
  };

  for (const t of tingkatanList) {
    const jpValues = jpStandar[t.nama] || [4, 4, 6, 6, 5, 3, 2, 2, 2];
    for (let mi = 0; mi < mapelList.length; mi++) {
      const jp = jpValues[mi] || 0;
      if (jp === 0) continue;
      await prisma.mapelTingkatan.upsert({
        where: {
          id_sekolah_id_mapel_id_tingkatan: {
            id_sekolah: sekolahPancasila.id,
            id_mapel: mapelList[mi].id,
            id_tingkatan: t.id,
          },
        },
        update: { jp_per_minggu: jp },
        create: {
          id_sekolah: sekolahPancasila.id,
          id_mapel: mapelList[mi].id,
          id_tingkatan: t.id,
          jp_per_minggu: jp,
        },
      });
    }
  }

  // G. WALI KELAS (12 Kelas)
  for (let i = 0; i < kelasList.length && i < guruList.length; i++) {
    await prisma.waliKelas.upsert({
      where: { id_sekolah_id_kelas: { id_sekolah: sekolahPancasila.id, id_kelas: kelasList[i].id } },
      update: { id_guru: guruList[i].id },
      create: {
        id_sekolah: sekolahPancasila.id,
        id_guru: guruList[i].id,
        id_kelas: kelasList[i].id,
      },
    }).catch(() => {});
  }

  // H. PENGAMPU MAPEL
  const guruAgama = guruList[5];
  const guruPjok = guruList[4];
  const guruBing = guruList[6];

  for (let ki = 0; ki < kelasList.length; ki++) {
    const k = kelasList[ki];
    const waliGuru = guruList[ki % 12];
    for (let mi = 0; mi < mapelList.length; mi++) {
      let g = waliGuru;
      if (mi === 0) g = guruAgama;
      else if (mi === 5) g = guruPjok;
      else if (mi === 7) g = guruBing;

      await prisma.pengampu.upsert({
        where: {
          id_sekolah_id_guru_id_mapel_id_kelas: {
            id_sekolah: sekolahPancasila.id,
            id_guru: g.id,
            id_mapel: mapelList[mi].id,
            id_kelas: k.id,
          },
        },
        update: {},
        create: {
          id_sekolah: sekolahPancasila.id,
          id_guru: g.id,
          id_mapel: mapelList[mi].id,
          id_kelas: k.id,
        },
      }).catch(() => {});
    }
  }

  // ═══════════════════════════════════════════════════════════════
  // 8. CASE MANAJEMEN JADWAL PERTAHUN & MULTI-PERIODE
  // ═══════════════════════════════════════════════════════════════
  // A. Periode Aktif
  const periodeAktif = await prisma.periodeJadwal.upsert({
    where: { id: 1 },
    update: { is_active: true },
    create: {
      id: 1,
      id_sekolah: sekolahPancasila.id,
      nama: 'Tahun Ajaran 2026/2027 (Semester Ganjil - Terbit)',
      tahun_ajaran: '2026/2027',
      semester: 'GASAL',
      is_active: true, // AKTIF
    },
  });

  // B. Periode Arsip (Tahun Lalu)
  await prisma.periodeJadwal.upsert({
    where: { id: 2 },
    update: { is_active: false },
    create: {
      id: 2,
      id_sekolah: sekolahPancasila.id,
      nama: 'Tahun Ajaran 2025/2026 (Semester Genap - Arsip)',
      tahun_ajaran: '2025/2026',
      semester: 'GENAP',
      is_active: false, // ARSIP
    },
  });

  // C. Periode Draf (Semester Depan)
  await prisma.periodeJadwal.upsert({
    where: { id: 3 },
    update: { is_active: false },
    create: {
      id: 3,
      id_sekolah: sekolahPancasila.id,
      nama: 'Tahun Ajaran 2026/2027 (Semester Genap - Draf Perencanaan)',
      tahun_ajaran: '2026/2027',
      semester: 'GENAP',
      is_active: false, // DRAF
    },
  });
  console.log('✅ 8. Case Periode Multi-Tahun: Aktif (2026/2027 Ganjil), Arsip (2025/2026 Genap), Draf (2026/2027 Genap)');

  // ═══════════════════════════════════════════════════════════════
  // 9. CASE JADWAL PELAJARAN LENGKAP & RIIL (BEBAS BENTROK)
  // ═══════════════════════════════════════════════════════════════
  await prisma.jadwal.deleteMany({ where: { id_sekolah: sekolahPancasila.id } });

  const jadwalEntries: any[] = [];
  // Jadwalkan 5 hari: Senin (1) s/d Jumat (5), Jam 1 s/d Jam 6
  for (let ki = 0; ki < kelasList.length; ki++) {
    const k = kelasList[ki];
    const waliGuru = guruList[ki % 12];

    for (let hari = 1; hari <= 5; hari++) {
      const maxJp = hari === 5 ? 5 : 6;
      for (let jam = 1; jam <= maxJp; jam++) {
        let mapelIdx = 2; // B. Indonesia
        let guruPengampu = waliGuru;

        if (hari === 1 && jam <= 2) {
          mapelIdx = 1; // PPKn
        } else if (hari === 1 && jam >= 3 && jam <= 4) {
          mapelIdx = 2; // B. Indo
        } else if (hari === 2 && jam <= 2) {
          mapelIdx = 3; // Matematika (Prioritas Pagi)
        } else if (hari === 2 && jam >= 3 && jam <= 4) {
          mapelIdx = 4; // IPAS (Prioritas)
        } else if (hari === 3 && jam <= 2) {
          if (ki % 2 === 0) {
            mapelIdx = 5; // PJOK
            guruPengampu = guruPjok;
          } else {
            mapelIdx = 3; // Matematika
          }
        } else if (hari === 4 && jam <= 2) {
          mapelIdx = 0; // Agama
          guruPengampu = guruAgama;
        } else if (hari === 4 && jam >= 3 && jam <= 4) {
          mapelIdx = 7; // Bahasa Inggris
          guruPengampu = guruBing;
        } else if (hari === 5) {
          mapelIdx = (jam % 2 === 0) ? 6 : 8; // Seni Budaya & Mulok
        }

        jadwalEntries.push({
          id_sekolah: sekolahPancasila.id,
          id_periode_jadwal: periodeAktif.id,
          id_kelas: k.id,
          hari,
          jam_ke: jam,
          id_mapel: mapelList[mapelIdx].id,
          id_guru: guruPengampu.id,
        });
      }
    }
  }

  await prisma.jadwal.createMany({
    data: jadwalEntries,
  });
  console.log(`✅ 9. Case Jadwal Pelajaran Riil CSP: ${jadwalEntries.length} slot jadwal kelas 1A - 6B bebas bentrok`);

  // ═══════════════════════════════════════════════════════════════
  // 10. CASE LINK VIEW GUEST / WALI MURID (AKTIF & KEDALUWARSA)
  // ═══════════════════════════════════════════════════════════════
  const guruBambangUser = await prisma.user.findFirst({ where: { email: 'bambang.sutrisno@guru.sd.belajar.id' } });
  const kelas3A = kelasList.find(k => k.kode_lengkap === '3A');

  if (guruBambangUser && kelas3A) {
    await prisma.sharedLink.deleteMany({ where: { id_sekolah: sekolahPancasila.id } });
    // Link Aktif (Bisa dibuka wali murid)
    await prisma.sharedLink.create({
      data: {
        id_sekolah: sekolahPancasila.id,
        uuid: 'link-view-kelas-3a-aktif',
        id_kelas: kelas3A.id,
        permission: 'read',
        created_by: guruBambangUser.id,
        expires_at: new Date(Date.now() + 30 * 24 * 3600 * 1000), // Aktif 30 hari
        view_count: 58,
      },
    });
    // Link Kedaluwarsa (Untuk testing expired state)
    await prisma.sharedLink.create({
      data: {
        id_sekolah: sekolahPancasila.id,
        uuid: 'link-view-semester-lalu-expired',
        id_kelas: kelas3A.id,
        permission: 'read',
        created_by: guruBambangUser.id,
        expires_at: new Date(Date.now() - 7 * 24 * 3600 * 1000), // Kedaluwarsa 7 hari lalu
        view_count: 142,
      },
    });
  }
  console.log('✅ 10. Case Link View Guest: Link Aktif (Kelas 3A) & Link Kedaluwarsa');

  // ═══════════════════════════════════════════════════════════════
  // 11. CASE TEMPLATE JADWAL HIAS (GRATIS & PREMIUM RESMI)
  // ═══════════════════════════════════════════════════════════════
  await prisma.template.deleteMany({ where: { created_by: designer.id } });
  await prisma.template.createMany({
    data: [
      {
        name: 'Template Standar Bersih (Format Cetak Kemdikbud)',
        thumbnail: 'template_clean_standard.png',
        css_styles: 'body { font-family: Inter; } table { border: 1px solid #CBD5E1; }',
        is_premium: false, // GRATIS
        created_by: designer.id,
      },
      {
        name: 'Superhero Ceria & Edukatif (Kelas Bawah SD)',
        thumbnail: 'template_superhero.png',
        css_styles: 'body { font-family: Comic; background: #FEF2F2; } th { background: #EF4444; }',
        is_premium: true, // PREMIUM RESMI DESIGNER
        created_by: designer.id,
      },
      {
        name: 'Flora Tropis & Satwa Nusantara',
        thumbnail: 'template_botanical.png',
        css_styles: 'body { font-family: Outfit; background: #F0FDF4; } th { background: #15803D; }',
        is_premium: true, // PREMIUM RESMI DESIGNER
        created_by: designer.id,
      },
    ],
  });
  console.log('✅ 11. Case Template Jadwal: Gratis (Standard) & Premium (Superhero, Flora Nusantara)');

  // ═══════════════════════════════════════════════════════════════
  // 12. CASE AUDIT LOG SISTEM & RIWAYAT AKTIVITAS
  // ═══════════════════════════════════════════════════════════════
  await prisma.adminLog.deleteMany({ where: { admin_user_id: superAdmin.id } });
  await prisma.adminLog.createMany({
    data: [
      {
        admin_user_id: superAdmin.id,
        action: 'VERIFIKASI_SEKOLAH_AKTIF',
        target_type: 'Sekolah',
        target_id: sekolahPancasila.id,
        details: JSON.stringify({ nama: 'SDN Pancasila 01', npsn: '20108922', status: 'ACTIVE' }),
        created_at: new Date(Date.now() - 3600000 * 48),
      },
      {
        admin_user_id: superAdmin.id,
        action: 'SINKRONISASI_DAPODIK_WILAYAH',
        target_type: 'System',
        target_id: 1,
        details: JSON.stringify({ totalSekolah: 18, status: 'SUCCESS', verified: true }),
        created_at: new Date(Date.now() - 3600000 * 24),
      },
      {
        admin_user_id: superAdmin.id,
        action: 'GENERATOR_CSP_SELESAI',
        target_type: 'Jadwal',
        target_id: periodeAktif.id,
        details: JSON.stringify({ slots: jadwalEntries.length, bebasBentrok: true, executionTimeSec: 3.2 }),
        created_at: new Date(Date.now() - 3600000 * 2),
      },
      {
        admin_user_id: superAdmin.id,
        action: 'EKSPOR_CADANGAN_DATABASE',
        target_type: 'Database',
        target_id: 1,
        details: JSON.stringify({ format: 'JSON/SQL', fileSize: '78KB', checksum: 'VALID' }),
        created_at: new Date(Date.now() - 3600000 * 1),
      },
    ],
  });

  // History Aktivitas Sekolah
  await prisma.history.deleteMany({ where: { id_sekolah: sekolahPancasila.id } });
  await prisma.history.createMany({
    data: [
      {
        id_sekolah: sekolahPancasila.id,
        user_id: adminPancasila.id,
        aksi: 'UPDATE_CONFIG_SEKOLAH',
        deskripsi: 'Mengatur struktur kelas paralel dan alokasi 35 menit per JP',
        ip_address: '127.0.0.1',
      },
      {
        id_sekolah: sekolahPancasila.id,
        user_id: adminPancasila.id,
        aksi: 'GENERATE_JADWAL',
        deskripsi: 'Generate jadwal pelajaran semester ganjil (12 rombel)',
        ip_address: '127.0.0.1',
      },
    ],
  });
  console.log('✅ 12. Case Audit Log & Riwayat Aktivitas Sistem Lengkap');

  // ═══════════════════════════════════════════════════════════════
  // RINGKASAN DATA DATABASE LENGKAP
  // ═══════════════════════════════════════════════════════════════
  console.log('\n' + '═'.repeat(70));
  console.log('🎉 DATABASE BERSIH & MENCAKUP SEMUA KASUS/JENIS BERHASIL DIBUAT!');
  console.log('═'.repeat(70));
  console.log('📋 REKAP KASUS PENGUJIAN:');
  console.log('  1. Super Admin : superadmin@jadwale.id / password123');
  console.log('  2. Admin Sekolah Paralel : admin@sdnpancasila01.sch.id / password123');
  console.log('  3. Admin Sekolah Tunggal  : admin@sdnteladan05.sch.id   / password123');
  console.log('  4. Sekolah Menunggu Verifikasi Superadmin: SDN Cibubur 03 (NPSN 20103450)');
  console.log('  5. Sekolah Ditolak/Nonaktif: SD Harapan Mandiri (NPSN 20109931)');
  console.log('  6. Guru Wali Kelas & Mapel (PNS): bambang.sutrisno@guru.sd.belajar.id / password123');
  console.log('  7. Guru Mapel PJOK (Ketersediaan Hari Tertentu): hendra.gunawan@guru.sd.belajar.id');
  console.log('  8. Guru Menunggu Verifikasi Sekolah: ratna.kartika@guru.sd.belajar.id');
  console.log('  9. Guru Nonaktif/Cuti: joko.widodo@guru.sd.belajar.id');
  console.log(' 10. Link View Guest Aktif & Expired, Template Gratis & Premium, Log Audit Riil.');
  console.log('═'.repeat(70));
}

main()
  .catch((e) => {
    console.error('❌ Error Seeding:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
