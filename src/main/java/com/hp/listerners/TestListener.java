package com.hp.listerners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.hp.utilities.ExtentManager;

public class TestListener implements ITestListener {

    @Override
	public void onStart(ITestContext context) {
		//Intialize ExtentReports
        ExtentManager.getReporter();
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
        ExtentManager.startTest(testName); // Start the Extent Report for the current test method when the test starts
        ExtentManager.logStep(Status.INFO,"Test Started: '" + testName + "'"); // Log the test start step to the current test in the report
	}

	@Override
	public void onTestSuccess(ITestResult result) { 
		String testName = result.getMethod().getMethodName();
        ExtentManager.logStepWithScreenshot(Status.PASS, "Test '" + testName + "' passed successfully");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
        String failureMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "No exception message available";
        ExtentManager.logStepWithScreenshot(Status.FAIL, "'" + testName + "' " + failureMessage);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
        ExtentManager.logStep(Status.SKIP, "Test '" + testName + "' was skipped");
	}

	@Override
	public void onFinish(ITestContext context) {
		// flush the Extent Report after all tests are completed
        ExtentManager.endTest();
	}

}
