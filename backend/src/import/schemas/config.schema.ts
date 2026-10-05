import { FieldRule } from '../utils/validation.util';

export const CONFIG_SCHEMA: Record<string, FieldRule> = {
  'Jumlah Hari Sekolah': { type: 'number', required: false, label: 'Jumlah Hari Sekolah' },
  'Durasi Per JP (Menit)': { type: 'number', required: false, label: 'Durasi Per JP' },
  'Sekolah Paralel': { type: 'boolean', required: false, label: 'Sekolah Paralel' },
  'Ada Upacara Senin': { type: 'boolean', required: false, label: 'Ada Upacara Senin' },
};
