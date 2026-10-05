import { FieldRule } from '../utils/validation.util';

export const SISWA_SCHEMA: Record<string, FieldRule> = {
  NISN: { type: 'string', required: true, unique: true, label: 'NISN' },
  Nama: { type: 'string', required: true, label: 'Nama Lengkap Siswa' },
  Kelas: { type: 'string', required: true, label: 'Nama Kelas' },
  'Jenis Kelamin': { type: 'enum', values: ['L', 'P'], required: false, label: 'Jenis Kelamin' },
  'No Telp Orangtua': { type: 'string', required: false, label: 'No Telp Orangtua' },
};
