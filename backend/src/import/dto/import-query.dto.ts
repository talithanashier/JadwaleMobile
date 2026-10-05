export class ImportQueryDto {
  mode?: 'insert' | 'upsert' | 'replace' | 'skipDuplicate' = 'upsert';
  previewToken?: string;
}
