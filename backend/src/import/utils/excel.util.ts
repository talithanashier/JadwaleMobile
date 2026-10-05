import * as ExcelJS from 'exceljs';

export interface ParsedSheet {
  sheetName: string;
  rows: { rowNumber: number; data: Record<string, any> }[];
}

export async function parseExcel(buffer: Buffer): Promise<ParsedSheet[]> {
  const workbook = new ExcelJS.Workbook();
  await workbook.xlsx.load(buffer as any);
  const result: ParsedSheet[] = [];

  workbook.eachSheet((worksheet) => {
    const rows: { rowNumber: number; data: Record<string, any> }[] = [];
    const headers: Record<number, string> = {};

    worksheet.eachRow((row, rowNumber) => {
      if (rowNumber === 1) {
        row.eachCell((cell, colNumber) => {
          const val = cell.value?.toString().trim();
          if (val) {
            headers[colNumber] = val;
          }
        });
      } else {
        const rowData: Record<string, any> = {};
        let hasData = false;

        row.eachCell((cell, colNumber) => {
          const key = headers[colNumber];
          if (key) {
            let val = cell.value;
            if (val !== null && typeof val === 'object' && 'result' in val) {
              val = (val as any).result;
            } else if (val !== null && typeof val === 'object' && 'text' in val) {
              val = (val as any).text;
            }

            if (typeof val === 'number') {
              // Convert long numeric values (NIP/NISN/Phone) safely to string without scientific notation
              if (val > 999999999) {
                val = BigInt(Math.round(val)).toString();
              } else {
                val = val.toString();
              }
            } else if (typeof val === 'string') {
              val = val.trim();
            }

            if (val !== undefined && val !== null && val !== '') {
              hasData = true;
              rowData[key] = val;
            }
          }
        });

        if (hasData) {
          rows.push({ rowNumber, data: rowData });
        }
      }
    });

    result.push({ sheetName: worksheet.name, rows });
  });

  return result;
}
