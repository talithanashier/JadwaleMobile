import { FieldRule } from '../utils/validation.util';

export const KELAS_SCHEMA: Record<string, FieldRule> = {
  'Nama Kelas': { type: 'string', required: true, label: 'Nama Kelas' },
  Tingkat: { type: 'number', required: true, label: 'Tingkatan (1-6)' },
  'Wali Kelas': { type: 'string', required: false, label: 'Wali Kelas (Nama/Kode Guru)' },
  'Jumlah Siswa': { type: 'number', required: false, label: 'Jumlah Siswa' },
  Ruangan: { type: 'string', required: false, label: 'Ruangan' },
};
