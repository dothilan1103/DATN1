package Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;

public class ExcelUtils {

    public static Object[][] getData(String sheetName) {

        // Đổi tên file cho đúng với file thực tế của bạn
        String fileName = "data.xlsx";

        InputStream is = ExcelUtils.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (is == null) {
            throw new RuntimeException(
                    "KHÔNG TÌM THẤY FILE: " + fileName +
                            " — kiểm tra lại tên file trong src/test/resources/"
            );
        }

        try (Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "KHÔNG TÌM THẤY SHEET: '" + sheetName +
                                "' — kiểm tra tên sheet trong file Excel"
                );
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' không có dòng header");
            }

            int colCount = headerRow.getLastCellNum();
            int rowCount = sheet.getLastRowNum(); // số dòng data (không tính header)

            System.out.println("[ExcelUtils] Sheet: " + sheetName +
                    " | Rows: " + rowCount +
                    " | Cols: " + colCount);

            Object[][] data = new Object[rowCount][colCount];

            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    if (row == null) {
                        data[i - 1][j] = "";
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        data[i - 1][j] = "";
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        // Tránh số bị đọc thành "123.0" thay vì "123"
                        double val = cell.getNumericCellValue();
                        if (val == Math.floor(val)) {
                            data[i - 1][j] = String.valueOf((long) val);
                        } else {
                            data[i - 1][j] = String.valueOf(val);
                        }
                    } else {
                        data[i - 1][j] = cell.toString().trim();
                    }
                }
            }

            return data;

        } catch (RuntimeException e) {
            throw e; // ném thẳng lên để TestNG báo lỗi rõ
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc Excel: " + e.getMessage(), e);
        }
    }
}