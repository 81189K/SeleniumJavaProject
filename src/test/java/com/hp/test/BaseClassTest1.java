package com.hp.test;

import org.testng.annotations.Test;

import com.hp.base.BaseClass;

public class BaseClassTest1 extends BaseClass{

	@Test
	public void verifyTitleTest() {
		String title = getDriver().getTitle();
		assert title.equals("OrangeHRM") : "Test Failed - Title is not matching!!!";
		System.out.println("Test Passed - Title is matching");
	}
	
}
