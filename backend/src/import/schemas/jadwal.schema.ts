import { FieldRule } from '../utils/validation.util';

export const JADWAL_SCHEMA: Record<string, FieldRule> = {
  Hari: { type: 'enum', values: ['SENIN', 'SELASA', 'RABU', 'KAMIS', 'JUMAT', 'SABTU'], required: true, label: 'Hari' },
  'Jam Ke': { type: 'number', required: true, label: 'Jam Ke (1-10)' },
  'Waktu Mulai': { type: 'string', required: false, label: 'Waktu Mulai (HH:MM)' },
  'Waktu Selesai': { type: 'string', required: false, label: 'Waktu Selesai (HH:MM)' },
  Kelas: { type: 'string', required: true, label: 'Nama Kelas' },
  Mapel: { type: 'string', required: true, label: 'Kode / Nama Mapel' },
  Guru: { type: 'string', required: true, label: 'Kode / Nama Guru' },
  Tipe: { type: 'enum', values: ['PELAJARAN', 'ISTIRAHAT', 'UPACARA', 'KULTUM', 'PEMBIASAAN', 'PROJEK'], required: false, label: 'Tipe Slot' },
  Ruangan: { type: 'string', required: false, label: 'Ruangan' },
  Catatan: { type: 'string', required: false, label: 'Catatan' },
};
