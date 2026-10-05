import { FieldRule } from '../utils/validation.util';

export const ISTIRAHAT_SCHEMA: Record<string, FieldRule> = {
  'Nama Kegiatan': { type: 'string', required: true, label: 'Nama Kegiatan' },
  'Sebelum JP Ke': { type: 'number', required: true, label: 'Sebelum JP Ke' },
  'Durasi (Menit)': { type: 'number', required: true, label: 'Durasi (Menit)' },
};
