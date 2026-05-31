package com.hp.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.hp.base.BaseClass;

public class ScreenshotUtil {

    public static final Logger logger = BaseClass.logger;

    /**
	 * Captures a screenshot and converts it directly into a Base64 string.
	 * @param driver The active WebDriver instance for the current thread.
	 * @return Base64 string representing the image.
	 */
	public static String captureScreenshotAsBase64() {
		// Cast WebDriver to TakesScreenshot and grab the Base64 output type
		return ((TakesScreenshot) BaseClass.getDriver()).getScreenshotAs(OutputType.BASE64);  // best for com.aventstack.extentreports.MediaEntityBuilder(String base64)
	}

    // Method to capture screenshot and returns the path to the saved screenshot
    public static String captureScreenshot(String testName) {
        TakesScreenshot ts = (TakesScreenshot) BaseClass.getDriver();
        File sourceFile = ts.getScreenshotAs(OutputType.FILE); 

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        String screenshotPath = System.getProperty("user.dir") + "/reports/Screenshots/" + testName + "_" + timeStamp + ".png";
		File destinationFile = new File(screenshotPath);
        destinationFile.getParentFile().mkdirs(); // Ensure the directory exists before saving the screenshot
        try {
            FileUtils.copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            logger.error("Error occurred while copying screenshot file: " + e.getMessage());
            throw new RuntimeException("Failed to save screenshot: " + e.getMessage(), e);
        }
        logger.info("Screenshot captured: " + screenshotPath);
        return screenshotPath; // Return the path to the saved screenshot
    }

}
