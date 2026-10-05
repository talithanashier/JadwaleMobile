import { FieldRule } from '../utils/validation.util';

export const JPPERHARI_SCHEMA: Record<string, FieldRule> = {
  Kelas: { type: 'string', required: true, label: 'Nama Kelas' },
  Hari: { type: 'string', required: true, label: 'Hari' },
  'Jumlah JP': { type: 'number', required: true, label: 'Jumlah JP' },
};
