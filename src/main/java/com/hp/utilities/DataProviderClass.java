package com.hp.utilities;

import java.util.List;

import org.testng.annotations.DataProvider;

public class DataProviderClass {

    private static final String EXCEL_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return getSheetData("validLoginData");
    }

    @DataProvider(name = "inValidLoginData")
    public static Object[][] inValidLoginData() {
        return getSheetData("inValidLoginData");
    }

    private static Object[][] getSheetData(String sheetName) {
        // Get the data from the specified sheet
        List<String[]> sheetData = ExcelUtil.getSheetData(EXCEL_FILE_PATH, sheetName);
        //convert List<String[]> to Object[][]
        Object[][] data = new Object[sheetData.size()][sheetData.get(0).length];
        for (int i = 0; i < sheetData.size(); i++) {
            data[i] = sheetData.get(i);
        }
        return data;
    }

}
