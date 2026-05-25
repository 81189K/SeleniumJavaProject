package com.hp.test;

import org.testng.annotations.Test;

import com.hp.base.BaseClass;

public class BaseClassTest2 extends BaseClass{
	
	@Test
	public void verifyUrlTest() {
		String url = driver.getCurrentUrl();
		assert url.contains("login") : "Test Failed - URL does not contains login!!!";
		System.out.println("Test Passed - URL contains login");
	}
	
}
