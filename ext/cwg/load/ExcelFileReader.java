package ext.cwg.load;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.*;

public class ExcelFileReader {

    // Functional interface for processing each row
    @FunctionalInterface
    public interface RowProcessor {
        void process(Map<String, String> row) throws Exception;
    }

    public static List<Map<String, String>> readExcel(String filePath) {
        List<Map<String, String>> records = new ArrayList<>();
        processExcelRows(filePath, records::add);
        return records;
    }

    public static List<Map<String, String>> readExcel(String filePath, int limit) {
        List<Map<String, String>> records = new ArrayList<>();
        processExcelRows(filePath, row -> {
            if (limit > 0 && records.size() >= limit) {
                // effective break
                return;
            }
            records.add(row);
        });
        return records;
    }

    public static void processExcelRows(String filePath, RowProcessor processor) {
        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext())
                return;

            // Read header row
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.getStringCellValue().trim() : "");
            }

            DataFormatter formatter = new DataFormatter();

            // Read remaining rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    // Use the formatter to get the value as it appears in Excel
                    // This handles numeric cells, dates, strings, etc. properly
                    String cellValue = (cell != null) ? formatter.formatCellValue(cell).trim() : "";
                    rowData.put(headers.get(i), cellValue);
                }

                try {
                    processor.process(rowData);
                } catch (Exception e) {
                    // Log the error but continue processing other rows?
                    // Or stop? Implementation choice: let's print stack trace and continue
                    System.err.println("Error processing row: " + rowData);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
