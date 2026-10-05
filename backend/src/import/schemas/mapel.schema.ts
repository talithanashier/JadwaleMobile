import { FieldRule } from '../utils/validation.util';

export const MAPEL_SCHEMA: Record<string, FieldRule> = {
  'Nama Mapel': { type: 'string', required: true, label: 'Nama Mata Pelajaran' },
  'Kode Mapel': { type: 'string', required: true, unique: true, label: 'Kode Mapel' },
  Kategori: { type: 'enum', values: ['WAJIB', 'PILIHAN', 'MUATAN_LOKAL'], required: false, label: 'Kategori' },
  'Jam Per Minggu': { type: 'number', required: false, label: 'Jam Per Minggu' },
  'Butuh Lab': { type: 'boolean', required: false, label: 'Butuh Lab' },
};
