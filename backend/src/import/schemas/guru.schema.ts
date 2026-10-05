import { FieldRule } from '../utils/validation.util';

export const GURU_SCHEMA: Record<string, FieldRule> = {
  NIP: { type: 'string', required: false, unique: true, label: 'NIP' },
  Nama: { type: 'string', required: true, label: 'Nama Lengkap' },
  Email: { type: 'email', required: false, unique: true, label: 'Email' },
  'No HP': { type: 'string', required: false, label: 'Nomor HP' },
  'Kode Guru': { type: 'string', required: true, unique: true, label: 'Kode Guru' },
  'Jenis Kelamin': { type: 'enum', values: ['L', 'P'], required: false, label: 'Jenis Kelamin' },
  Status: { type: 'enum', values: ['PNS', 'PPPK', 'GTY', 'GTT'], required: false, label: 'Status Kepegawaian' },
  'Mapel Utama': { type: 'string', required: false, label: 'Mapel Utama' },
  'Max Jam Per Minggu': { type: 'number', required: false, label: 'Max Jam Per Minggu' },
  'Hari Tidak Available': { type: 'string', required: false, label: 'Hari Tidak Available' },
};
