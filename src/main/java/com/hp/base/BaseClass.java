package com.hp.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseClass {
	
	protected Properties prop;
	protected WebDriver driver;
	
	@BeforeMethod
	public void setup() throws IOException {
		//1. load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		//to load the file
		prop.load(fis);
		
		
		//2.Initialize the WebDriver based on browser property from config.properties file.
		String browser = prop.getProperty("browser");
		if(browser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();
		} else if(browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
		} else if(browser.equalsIgnoreCase("edge")) {
			driver = new EdgeDriver();
		} else {
			throw new IllegalArgumentException("Browser not supported: "+ browser); //throw exception or make one browser as default.
		}
		
		
		//Implicit Wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		
		//Maximize the driver
		driver.manage().window().maximize();
		
		//Navigate to URL
		try {
			driver.get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to navigate to the URL: " + e.getMessage());
		}
		
	}
	
	@AfterMethod
	public  void teardown() {
		if(driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				System.out.println("Failed to quit the driver: " + e.getMessage());
			}
		}
	}

}
