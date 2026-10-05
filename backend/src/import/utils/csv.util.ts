export async function parseCsv(buffer: Buffer): Promise<{ rowNumber: number; data: Record<string, any> }[]> {
  const content = buffer.toString('utf-8').replace(/^\uFEFF/, ''); // Remove UTF-8 BOM
  const lines = content.split(/\r?\n/).filter((line) => line.trim().length > 0);
  if (lines.length === 0) return [];

  // Auto-detect delimiter (, or ;)
  const firstLine = lines[0];
  const delimiter = (firstLine.match(/;/g) || []).length > (firstLine.match(/,/g) || []).length ? ';' : ',';

  const headers = firstLine.split(delimiter).map((h) => h.trim().replace(/^["']|["']$/g, ''));
  const rows: { rowNumber: number; data: Record<string, any> }[] = [];

  for (let i = 1; i < lines.length; i++) {
    const values = lines[i].split(delimiter).map((v) => v.trim().replace(/^["']|["']$/g, ''));
    const rowData: Record<string, any> = {};
    let hasData = false;

    headers.forEach((header, colIdx) => {
      const val = values[colIdx];
      if (val !== undefined && val !== '') {
        hasData = true;
        rowData[header] = val;
      }
    });

    if (hasData) {
      rows.push({ rowNumber: i + 1, data: rowData });
    }
  }

  return rows;
}
