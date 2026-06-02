package com.hp.test;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.hp.base.BaseClass;
import com.hp.utilities.ExtentManager;

public class BaseClassTest1 extends BaseClass{

	@Test
	public void verifyTitleTest() {
		String title = getDriver().getTitle();
		// ExtentManager.logStep(Status.FAIL,"Intentionally failing as part of testing"); // Log the verification step to the current test in the report
		// assert title.equals("OrangeHRM11111") : "Test Failed - Title is not matching!!!";
		assert title.equals("OrangeHRM") : "Test Failed - Title is not matching!!!";
		staticWait(1);
		ExtentManager.logStep(Status.PASS,"Test Passed - Title is matching"); 
	}
	
}
