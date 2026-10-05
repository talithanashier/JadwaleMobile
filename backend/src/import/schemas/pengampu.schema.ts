import { FieldRule } from '../utils/validation.util';

export const PENGAMPU_SCHEMA: Record<string, FieldRule> = {
  Kelas: { type: 'string', required: true, label: 'Nama Kelas' },
  Mapel: { type: 'string', required: true, label: 'Nama / Kode Mapel' },
  Guru: { type: 'string', required: true, label: 'Nama / Kode / NIP Guru' },
};
