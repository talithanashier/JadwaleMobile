export interface FieldRule {
  type?: 'string' | 'number' | 'enum' | 'email' | 'boolean';
  required?: boolean;
  unique?: boolean;
  values?: string[];
  label?: string;
}

export interface ValidationErrorItem {
  rowNumber: number;
  field: string;
  value?: any;
  message: string;
}

export function validateRow(
  row: Record<string, any>,
  schema: Record<string, FieldRule>,
  rowNumber: number,
): { valid: boolean; errors: ValidationErrorItem[]; data: Record<string, any> } {
  const errors: ValidationErrorItem[] = [];
  const normalized: Record<string, any> = {};

  const rowKeyMap: Record<string, string> = {};
  for (const actualKey of Object.keys(row)) {
    const normKey = actualKey.toLowerCase().replace(/[^a-z0-9]/g, '');
    rowKeyMap[normKey] = actualKey;
  }

  for (const [schemaKey, rules] of Object.entries(schema)) {
    const normSchemaKey = schemaKey.toLowerCase().replace(/[^a-z0-9]/g, '');
    const actualKeyInRow = rowKeyMap[normSchemaKey] || schemaKey;

    let value = row[actualKeyInRow] ?? row[schemaKey];
    if (typeof value === 'string') {
      value = value.trim();
    }

    if (rules.required && (value === undefined || value === null || value === '')) {
      errors.push({
        rowNumber,
        field: schemaKey,
        value,
        message: `${rules.label || schemaKey} wajib diisi`,
      });
      continue;
    }

    if (value === undefined || value === null || value === '') {
      normalized[schemaKey] = null;
      continue;
    }

    if (rules.type === 'number') {
      const num = Number(value);
      if (isNaN(num)) {
        errors.push({
          rowNumber,
          field: schemaKey,
          value,
          message: `${rules.label || schemaKey} harus berupa angka`,
        });
      } else {
        normalized[schemaKey] = num;
      }
    } else if (rules.type === 'enum' && rules.values) {
      const upper = String(value).toUpperCase();
      const normalizedAllowed = rules.values.map((v) => v.toUpperCase());
      if (!normalizedAllowed.includes(upper)) {
        errors.push({
          rowNumber,
          field: schemaKey,
          value,
          message: `${rules.label || schemaKey} harus salah satu dari: ${rules.values.join(', ')}`,
        });
      } else {
        normalized[schemaKey] = upper;
      }
    } else if (rules.type === 'email') {
      const emailStr = String(value).toLowerCase();
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(emailStr)) {
        errors.push({
          rowNumber,
          field: schemaKey,
          value,
          message: `${rules.label || schemaKey} harus berupa format email yang valid`,
        });
      } else {
        normalized[schemaKey] = emailStr;
      }
    } else if (rules.type === 'boolean') {
      const strVal = String(value).toLowerCase();
      if (['ya', 'true', '1', 'yes'].includes(strVal)) {
        normalized[schemaKey] = true;
      } else if (['tidak', 'false', '0', 'no'].includes(strVal)) {
        normalized[schemaKey] = false;
      } else {
        errors.push({
          rowNumber,
          field: schemaKey,
          value,
          message: `${rules.label || schemaKey} harus berupa Ya atau Tidak`,
        });
      }
    } else {
      normalized[schemaKey] = value;
    }
  }

  return {
    valid: errors.length === 0,
    errors,
    data: normalized,
  };
}
