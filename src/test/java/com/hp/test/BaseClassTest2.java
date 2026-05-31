package com.hp.test;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.hp.base.BaseClass;
import com.hp.utilities.ExtentManager;
// import com.hp.utilities.ScreenshotUtil;


public class BaseClassTest2 extends BaseClass{
	
	@Test
	public void verifyUrlTest() {
		String url = getDriver().getCurrentUrl();
		// ScreenshotUtil.captureScreenshot("verifyUrlTest"); // Capture screenshot for verification step //test 
		assert url.contains("login") : "Test Failed - URL does not contains login!!!";
		// System.out.println("Test Passed - URL contains login");
		ExtentManager.logStepWithScreenshot(Status.SKIP, "Skipping this test as it's just a demonstration of SkipException in TestNG");
		throw new SkipException("Skipping this test as it's just a demonstration of SkipException in TestNG");
	}
	
}
