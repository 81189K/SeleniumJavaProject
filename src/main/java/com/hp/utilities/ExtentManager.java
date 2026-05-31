package com.hp.utilities;

import static com.hp.utilities.ScreenshotUtil.captureScreenshot;
import static com.hp.utilities.ScreenshotUtil.captureScreenshotAsBase64;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.hp.base.BaseClass;

public class ExtentManager {
	
	private static ExtentReports extentReport;
	private static ThreadLocal<ExtentTest> tlExtentTest = new ThreadLocal<>();
	private static Map<Long,WebDriver> driverMap = new HashMap<>();
	private static final Logger logger = BaseClass.logger;

	//Initialize ExtentReports instance
	public static ExtentReports getReporter() {
		if(extentReport == null) {
			// String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReports/ExtentReport_" + System.currentTimeMillis() + ".html";
			String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
			ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
			sparkReporter.config().setReportName("Automation Test Report");
			sparkReporter.config().setDocumentTitle("OrangeHRM Report");
			sparkReporter.config().setTheme(Theme.DARK);

			extentReport = new ExtentReports();
			extentReport.attachReporter(sparkReporter); // Attach the SparkReporter to the ExtentReports instance
			//Adding system info to the report
			extentReport.setSystemInfo("Operating System", System.getProperty("os.name"));
			extentReport.setSystemInfo("Java Version", System.getProperty("java.version"));
			extentReport.setSystemInfo("User Name", System.getProperty("user.name"));
		}
		return extentReport;
	}

	//Method to start a test and create an ExtentTest instance for the current thread
	public static ExtentTest startTest(String testName) {
		ExtentTest extentTest = getReporter().createTest(testName);
		tlExtentTest.set(extentTest); // Set the ExtentTest instance for the current thread using ThreadLocal
		return extentTest;
	}

	//Method to end the test and flush the report
	public static void endTest() {
		getReporter().flush();
		tlExtentTest.remove();
	}

	//Method to get the ExtentTest instance for the current thread
	public static ExtentTest getTest() {
		ExtentTest test = tlExtentTest.get();
		if (test == null) {
			throw new IllegalStateException("ExtentTest not initialized for thread: " + Thread.currentThread().threadId());
		}
		return test;
	}


	//Method to get the name of the current test method for logging purposes
	public static String getTestName() {
		ExtentTest currentExtentTest = getTest();
		if (currentExtentTest != null) {
			return currentExtentTest.getModel().getName();
		}
		return "No Test is currently running";
	}

	//Method to log a step
	public static void logStep(String logMessage) {
		getTest().info(logMessage); // Log the step description to the current test in the report
	}

	public static void logStep(Status status, String logMessage) {
		switch (status) {
				case PASS:
					getTest().pass(logMessage);
					break;
				case FAIL:
					getTest().fail(formattedLogMessage(logMessage, "red"));
					break;
				case SKIP:
					getTest().skip(formattedLogMessage(logMessage, "orange"));
					break;
				case WARNING:
					getTest().warning(formattedLogMessage(logMessage, "yellow"));
					break;
				default:
					getTest().info(logMessage);
			}
	}

	//Method to log a step validation with screenshot
	public static void logStepWithScreenshot(String logMessage) {
		// Attach a screenshot to the current test in the report with the step description as the message
		attachBase64ScreenshotToReport(Status.PASS, logMessage); // Attach the screenshot using Base64 string to avoid file handling issues and ensure compatibility across different environments
	}

	public static void logStepWithScreenshot(Status status, String logMessage) {
		// Attach a screenshot to the current test in the report with the step description as the message
		attachBase64ScreenshotToReport(status, logMessage); 
	}

	//Method to log a step failure with screenshot
	public static void logStepFailure(String logMessage) {
		attachBase64ScreenshotToReport(Status.FAIL, formattedLogMessage(logMessage, "red"));
	}

	//Method to log a skip
	public static void logStepSkip(String logMessage) {
		getTest().skip(formattedLogMessage(logMessage, "orange")); // Log the skip message to the current test in the report
	}

	//formatted log messge with status
	public static String formattedLogMessage(String logMessage, String color) {
		return "<span style='color:" + color + ";'>" + logMessage + "</span>";
	}

	//Register WebDriver instance for the current thread
	public static void registerDriver(WebDriver driver) {
		driverMap.put(Thread.currentThread().threadId(), driver);	
	}

	// Method to attach screenshot to the report with a message //path + test+level with preview  ==> too primitive
	public static void attachScreenshotPathToReport() {
		try {
			String screenshotPath = captureScreenshot(getTestName());
			getTest().addScreenCaptureFromPath(screenshotPath, getTestName()); // Attach the screenshot to the current test in the report with a message
		} catch (Exception e) {
			logger.error("Failed to capture or attach base64 screenshot: " , e);
		}
	}

	// Method to attach screenshot to the report using Base64 string //base64 + test+level without preview  ==> redundant
	public static void attachScreenshotAsBase64ToReport(String screenshotTitle) {
		try {
			// Get the base64 string
			String base64Image = captureScreenshotAsBase64();
			// Attach it to the current test node
			getTest().addScreenCaptureFromBase64String(base64Image, screenshotTitle);
		} catch (Exception e) {
			logger.warn("Failed to capture or attach base64 screenshot: " , e);
		}
	}

	// Method to attach screenshot to the report using path with a message
	public static void attachPathScreenshotToReport(String screenShotMessage) {
		try {
			// 1. Capture the screenshot path
			String screenshotPath = captureScreenshot(getTestName());

			// 2. Build the media entity using Extent Reports Builder pattern
			Media mediaEntity = MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath, screenShotMessage).build();

			// 3. Log the failure step along with the embedded screenshot
			getTest().info(screenShotMessage, mediaEntity);
		} catch (Exception e) {
			logger.error("Failed to attach screenshot: " , e);
		}
	}

	// Method to attach screenshot to the report using Base64 string
	public static void attachBase64ScreenshotToReport(Status status, String screenShotMessage) {
		try {
			// 1. Capture the screenshot as a Base64 string
			String base64Screenshot = captureScreenshotAsBase64();

			// 2. Build the media entity using Extent Reports Builder pattern
			// Note: Extent Reports automatically prepends "data:image/png;base64," internally
			Media mediaEntity = MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build();

			// 3. Log the step status along with the embedded screenshot
			switch (status) {
				case PASS:
					getTest().pass(screenShotMessage, mediaEntity);
					break;
				case FAIL:
					getTest().fail(formattedLogMessage(screenShotMessage, "red"), mediaEntity);
					break;
				default:
					getTest().info(screenShotMessage, mediaEntity);
					break;
			}
		} catch (Exception e) {
			logger.error("Failed to attach screenshot: " , e);
		}
	}

}