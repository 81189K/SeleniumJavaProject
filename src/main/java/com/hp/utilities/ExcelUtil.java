package com.hp.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    //Method to get data from given sheet name
    public static List<String[]> getSheetData(String filePath, String sheetName) {
        // List to hold the data read from Excel, each String[] represents a row of data
        List<String[]> dataList = new ArrayList<>();
        // try-with-resources to ensure proper resource management and automatic closing of file streams and workbook
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' does not exist in the Excel file.");
            }

            //Method1: Using for-each loop to iterate through rows and cells to read data, starting from row 1 to skip header row, and read all cells in each row to create a String[] for each row of data, which is then added to the dataList
            //iterate through rows and cells to read data, starting from row 1 to skip header row, and read all cells in each row to create a String[] for each row of data, which is then added to the dataList
            for(Row row : sheet) {
                if(row.getRowNum() == 0) { // Skip header row
                    continue;
                }
                //Read all cells in the current row
                List<String> rowData = new ArrayList<>();
                for(Cell cell : row) {
                    rowData.add(getCellValue(cell)); // Get cell value as String and add to rowData list
                }
                // Convert rowData list to String[] and add to dataList
                dataList.add(rowData.toArray(new String[0]));
            }

            //Method2: Using traditional for loop to iterate through rows and cells to read data, starting from row 1 to skip header row, and read all cells in each row to create a String[] for each row of data, which is then added to the dataList
            // int numRows = sheet.getPhysicalNumberOfRows();
            // for (int i = 1; i < numRows; i++) { // Start from 1 to skip header row
            //     Row row = sheet.getRow(i);
            //     if (row != null) {
            //         int numCells = row.getLastCellNum();
            //         String[] rowData = new String[numCells];
            //         for (int j = 0; j < numCells; j++) {
            //             Cell cell = row.getCell(j);
            //             rowData[j] = getCellValue(cell); // Get cell value as String
            //         }
            //         dataList.add(rowData);
            //     }
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    //Method to get cell value as String, handling different cell types (String, Numeric, Boolean, Formula, Blank)
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

}
